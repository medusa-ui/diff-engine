package io.getmedusa.diffengine;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.diff.TextNode;
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
        Set<ServerSideDiff> diffsText = new LinkedHashSet<>();
        for(int layer : layers) {
            var diffsForOneLayerArray = calculateForLayer(oldHTMLLayersMap, newHTMLLayersMap, layer);
            diffs.addAll(diffsForOneLayerArray[0]);
            diffsText.addAll(diffsForOneLayerArray[1]);
        }
        diffs.addAll(diffsText);
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

    private LinkedHashSet<ServerSideDiff>[] calculateForLayer(Map<Integer, List<HTMLLayer>> oldHTMLLayersMap, Map<Integer, List<HTMLLayer>> newHTMLLayersMap, int layer) {
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
                if(diff.isRemoval()) {
                    removeDeeperElementsFromBuildup(diff.getXpath(), oldHTMLLayersMap, layer);
                }
                diffs.add(diff);
            }
        }

        LinkedHashSet<ServerSideDiff> textChangesToBeAddedLast = new LinkedHashSet<>();
        handleTextEdits(newHTMLLayers, textChangesToBeAddedLast, buildup);

        return new LinkedHashSet[]{diffs, textChangesToBeAddedLast};
    }

    /**
     * If at any point we remove a nested structure, we cannot expect to refer to deeper elements of the nested structure any more
     * Thus upon a removal diff, check the xpath for deeper structures and get rid of them
     * @param xpath of the parent just removed
     * @param oldHTMLLayersMap
     * @param layer check only deeper layers
     */
    private void removeDeeperElementsFromBuildup(String xpath, Map<Integer, List<HTMLLayer>> oldHTMLLayersMap, int layer) {
        List<HTMLLayer> deeperLayers = oldHTMLLayersMap.getOrDefault(layer + 1, new LinkedList<>());
        List<HTMLLayer> layersToRemove = new ArrayList<>();
        for(HTMLLayer deeperLayer : deeperLayers) {
            if(deeperLayer.getParentXpath().equals(xpath)) {
                System.out.println("Removed deeper layer: " + deeperLayer.getXpath());
                layersToRemove.add(deeperLayer);
                removeDeeperElementsFromBuildup(deeperLayer.getXpath(), oldHTMLLayersMap, layer + 1);
            }
        }
        deeperLayers.removeAll(layersToRemove);
    }

    private void handleTextEdits(List<HTMLLayer> newHTMLLayers, LinkedHashSet<ServerSideDiff> diffs, LinkedList<HTMLLayer> buildup) {
        for(var potentialEditLayer : newHTMLLayers) {
            if(potentialEditLayer.hasTextNode()) {
                final int index = findMatch(potentialEditLayer, buildup);
                if(-1 == index) {
                    continue;
                }
                var match = buildup.get(index);

                //if potentialEditLayer has one that match does not have = addition
                LinkedList<ServerSideDiff> determinedAdditions = determineTextNodeAdditions(potentialEditLayer, potentialEditLayer.getTextNodes(), match.getTextNodes());
                diffs.addAll(determinedAdditions);

                //if potentialEditLayer does not have one that matches match = removal
                LinkedList<ServerSideDiff> determinedRemovals = determineTextNodeRemovals(potentialEditLayer.getTextNodes(), match.getTextNodes());
                diffs.addAll(determinedRemovals);

                //if there's a different value = text edit
                LinkedList<ServerSideDiff> determinedEdits = determineTextNodeEdits(potentialEditLayer.getTextNodes(), match.getTextNodes());
                determinedAdditions.forEach(a -> determinedEdits.removeAll(determinedEdits.stream().filter(e -> e.getXpath().equals(a.getXpath())).toList()));
                determinedRemovals.forEach(a -> determinedEdits.removeAll(determinedEdits.stream().filter(e -> e.getXpath().equals(a.getXpath())).toList()));
                diffs.addAll(determinedEdits);
            }
        }
    }

    private LinkedList<ServerSideDiff> determineTextNodeEdits(LinkedList<TextNode> newTextNodes, LinkedList<TextNode> oldTextNodes) {
        LinkedList<TextNode> edits = new LinkedList<>();
        if(oldTextNodes == null) {
            oldTextNodes = new LinkedList<>();
        }
        for (int i = 0; i < newTextNodes.size(); i++) {
            String oldTextNode = "";
            if(oldTextNodes.size() > i) {
                oldTextNode = oldTextNodes.get(i).getContent();
            }
            if(!newTextNodes.get(i).getContent().equals(oldTextNode)) {
                edits.add(newTextNodes.get(i));
            }
        }
        return new LinkedList<>(edits.stream().filter(r -> !r.getContent().trim().isBlank()).map(ServerSideDiff::buildEdit).toList());
    }

    private LinkedList<ServerSideDiff> determineTextNodeRemovals(LinkedList<TextNode> newTextNodes, LinkedList<TextNode> oldTextNodes) {
        if(newTextNodes == null) {
            newTextNodes = new LinkedList<>();
        }
        var removals = new LinkedList<>(oldTextNodes);
        newTextNodes.forEach(removals::remove);
        return new LinkedList<>(removals.stream().filter(r -> !r.getContent().trim().isBlank()).map(ServerSideDiff::buildRemoval).toList());
    }

    private LinkedList<ServerSideDiff> determineTextNodeAdditions(HTMLLayer layer, LinkedList<TextNode> newTextNodes, LinkedList<TextNode> oldTextNodes) {
        var additions = new LinkedList<>(newTextNodes);
        oldTextNodes.forEach(additions::remove);

        LinkedList<ServerSideDiff> result = new LinkedList<>();
        for(TextNode text : additions) {
            if(text.getContent().trim().isBlank()) {
                continue;
            }

            //
            final ServerSideDiff diff;
            if(text.getPrevious() != null) {
                //after
                String afterXPATH = JOOX.$(text.getPrevious()).xpath();
                diff = ServerSideDiff.buildNewAfterDiff(text, afterXPATH);
            } else if(text.getNext() != null) {
                //before
                String beforeXPATH = JOOX.$(text.getNext()).xpath();
                diff = ServerSideDiff.buildNewBeforeDiff(text, beforeXPATH);
            } else {
                //in
                diff = ServerSideDiff.buildInDiff(text, layer.getXpath());
            }
            result.add(diff);
        }
        return result;
    }

    private ServerSideDiff recurringPatch(LinkedList<HTMLLayer> buildup, List<HTMLLayer> newHTMLLayers) {
        Patch<HTMLLayer> patch = DiffUtils.diff(buildup, newHTMLLayers);
        if(!patch.getDeltas().isEmpty()) {
            var delta = patch.getDeltas().get(0);
            System.out.println(delta);

            if(DeltaType.DELETE.equals(delta.getType()) || DeltaType.CHANGE.equals(delta.getType())) {
                final int indexToRemove = delta.getSource().getPosition();
                final HTMLLayer layerToRemove = buildup.get(indexToRemove);

                buildup.remove(layerToRemove);
                return ServerSideDiff.buildRemoval(layerToRemove);
            } else if(DeltaType.INSERT.equals(delta.getType())) {
                HTMLLayer layerToAdd = delta.getTarget().getLines().get(0);
                layerToAdd = layerToAdd.cloneAndPruneContentIntoTagOnly();
                int indexPosition = delta.getSource().getPosition();

                final ServerSideDiff diff;

                if (indexPosition == buildup.size()) { //so this is saying: add all the way at the end of possible tags
                    //linkLast(layerToAdd);
                    //but if you just add last, you can have different sections of layers (adding 2 p tags, 1 under /section and 1 under /div; would look the same!)
                    //so really you need to find the last tag with xpath that matches the parent xpath of the to-add node
                    if(!buildup.isEmpty()) {
                        final HTMLLayer lastLayerMatchingXPathParent = getLastLayerMatchingXPathParent(buildup, layerToAdd);
                        if(lastLayerMatchingXPathParent != null) {
                            diff = ServerSideDiff.buildNewAfterDiff(layerToAdd, lastLayerMatchingXPathParent);
                        } else { //and should there not be one of those, we do an in
                            diff = ServerSideDiff.buildInDiff(layerToAdd);
                        }

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

    private static HTMLLayer getLastLayerMatchingXPathParent(LinkedList<HTMLLayer> buildup, HTMLLayer layerToAdd) {
        for (int i = buildup.size() - 1; i >= 0; i--) {
            final HTMLLayer layer = buildup.get(i);
            if(layer.getParentXpath().equals(layerToAdd.getParentXpath())) {
                return layer;
            }
        }
        return null;
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
