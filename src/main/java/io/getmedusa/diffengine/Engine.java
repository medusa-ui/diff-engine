package io.getmedusa.diffengine;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.engine.RecursiveDiffEngineLogic;
import io.getmedusa.diffengine.engine.TextEditEngineLogic;
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
            ServerSideDiff diff = RecursiveDiffEngineLogic.recurringPatch(buildup, newHTMLLayers);
            if(diff == null) {
                moreDiffsAvailable = false;
            } else {
                if(diff.isRemoval()) {
                    removeDeeperElementsFromBuildup(diff.getXpath(), oldHTMLLayersMap, layer);
                }
                diffs.add(diff);
            }
        }

        return new LinkedHashSet[]{diffs, TextEditEngineLogic.handleTextEdits(newHTMLLayers, buildup)};
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
