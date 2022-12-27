package io.getmedusa.diffengine;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.model.HTMLLayer;
import org.joox.JOOX;
import org.joox.Match;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

import java.util.*;

public class Engine {

    public Set<ServerSideDiff> calculate(String oldHTML, String newHTML) {
        Map<Integer, List<HTMLLayer>> oldHTMLLayersMap = interpretLayer(initialParse(oldHTML));
        Map<Integer, List<HTMLLayer>> newHTMLLayersMap = interpretLayer(initialParse(newHTML));

        Set<Integer> layers = new TreeSet<>(oldHTMLLayersMap.keySet());
        layers.addAll(newHTMLLayersMap.keySet());

        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        for(int layer : layers) {
            var diffsForOneLayer = calculateForLayer(oldHTMLLayersMap, newHTMLLayersMap, layer);
            diffs.addAll(diffsForOneLayer);
        }
        return diffs;
    }

    private static Match initialParse(String html) {
        //clear scripts, clear comments, etc
        final Document document = Jsoup.parse(html);
        removeComments(document);
        document.outputSettings().prettyPrint(false);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return JOOX.$(document.body().outerHtml());
    }

    private static void removeComments(Node node) {
        for (int i = 0; i < node.childNodeSize();) {
            Node child = node.childNode(i);
            if (child.nodeName().equals("#comment"))
                child.remove();
            else {
                removeComments(child);
                i++;
            }
        }
    }

    private LinkedHashSet<ServerSideDiff> calculateForLayer(Map<Integer, List<HTMLLayer>> oldHTMLLayersMap, Map<Integer, List<HTMLLayer>> newHTMLLayersMap, int layer) {
        List<HTMLLayer> oldHTMLLayers = oldHTMLLayersMap.getOrDefault(layer, new LinkedList<>());
        List<HTMLLayer> newHTMLLayers = newHTMLLayersMap.getOrDefault(layer, new LinkedList<>());

        LinkedHashSet<ServerSideDiff> diffs = new LinkedHashSet<>();

        LinkedList<HTMLLayer> buildup = new LinkedList<>(oldHTMLLayers);

        boolean moreDiffsAvailable = true;
        while (moreDiffsAvailable) {
            ServerSideDiff diff = recurringPatch(buildup, newHTMLLayers);
            if(diff == null) {
                moreDiffsAvailable = false;
            } else {
                diffs.add(diff);
            }
        }

        //TODO so comparing text nodes works, but its possible you have text nodes mixed with actual nodes
        //not sure how to do this ... keep in mind we are handling it one layer at a time
        for(var potentialEditLayer : newHTMLLayers) {
            if(potentialEditLayer.hasTextNode()) {
                final int index = findMatch(potentialEditLayer, buildup);
                if(-1 == index) {
                    continue;
                }
                var match = buildup.get(index);
                if(match.hasTextNode() && !match.getContent().equals(potentialEditLayer.getContent())) {
                    diffs.add(ServerSideDiff.buildEdit(potentialEditLayer));
                }
            }
        }

        return diffs;
    }

    private ServerSideDiff recurringPatch(LinkedList<HTMLLayer> buildup, List<HTMLLayer> newHTMLLayers) {
        Patch<HTMLLayer> patch = DiffUtils.diff(buildup, newHTMLLayers);
        if(!patch.getDeltas().isEmpty()) {
            var delta = patch.getDeltas().get(0);
            System.out.println(delta);

            if(DeltaType.DELETE.equals(delta.getType())) {
                final int indexToRemove = delta.getSource().getPosition();
                final HTMLLayer layerToRemove = buildup.get(indexToRemove);

                buildup.remove(layerToRemove);
                return ServerSideDiff.buildRemoval(layerToRemove);
            } else if(DeltaType.INSERT.equals(delta.getType())) {
                HTMLLayer layerToAdd = delta.getTarget().getLines().get(0);
                int indexPosition = delta.getSource().getPosition();

                final ServerSideDiff diff;

                if (indexPosition == buildup.size()) {
                    //linkLast(layerToAdd);
                    if(containsXPath(buildup, layerToAdd.getParentXpath())) { //TODO this is the wrong buildup; should depend on the xpath?
                        diff = ServerSideDiff.buildNewAfterDiff(layerToAdd, buildup.getLast());
                    } else {
                        diff = ServerSideDiff.buildInDiff(layerToAdd);
                    }
                } else {
                    //linkBefore(layerToAdd, node(indexPosition));
                    diff = ServerSideDiff.buildNewBeforeDiff(layerToAdd, buildup.get(indexPosition));
                }

                buildup.add(indexPosition, layerToAdd);
                return diff;
            }
        }
        return null;
    }

    private boolean containsXPath(LinkedList<HTMLLayer> buildup, String xpath) {
        var found = buildup.stream().filter(l -> l.getXpath().equals(xpath)).findAny().orElse(null);
        return found != null;
    }

    private Set<ServerSideDiff> findEdits(List<HTMLLayer> oldHTMLLayers, List<HTMLLayer> newHTMLLayers) {
        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        for(HTMLLayer newLayer : newHTMLLayers) {
            int index = findMatch(newLayer, oldHTMLLayers);
            if(-1 != index && layerContentIsLimitedToLayer(newLayer)) {
                HTMLLayer layer = oldHTMLLayers.get(index);
                if (layerContentIsLimitedToLayer(layer) && !newLayer.getContent().equals(layer.getContent())) {
                    diffs.add(ServerSideDiff.buildEdit(newLayer));
                }
            }
        }
        return diffs;
    }

    private boolean layerContentIsLimitedToLayer(HTMLLayer layer) {
        return JOOX.$(layer.getContent()).children().isEmpty();
    }

    private static int findMatch(HTMLLayer layerToMatch, List<HTMLLayer> listToMatchIn) {
        for (int i = 0; i < listToMatchIn.size(); i++) {
            if(listToMatchIn.get(i).equals(layerToMatch)) {
               return i;
            }
        }
        return -1;
    }

    public static Map<Integer, List<HTMLLayer>> interpretLayer(String html) {
        return interpretLayer(initialParse(html));
    }

    private static Map<Integer, List<HTMLLayer>> interpretLayer(Match match) {
        Map<Integer, List<HTMLLayer>> layerMap = new HashMap<>();
        layerMap.put(1, List.of(new HTMLLayer(match)));
        recursive(match, layerMap, 2);
        return layerMap;
    }

    private static void recursive(Match match, Map<Integer, List<HTMLLayer>> map, int layer) {
        List<HTMLLayer> layers = map.getOrDefault(layer, new LinkedList<>());
        for (var child : match.children()) {
            var $child = JOOX.$(child);
            layers.add(new HTMLLayer($child));
            if($child.children().isNotEmpty()) {
                recursive($child, map, layer + 1);
            }
        }
        map.put(layer, layers);
    }

}
