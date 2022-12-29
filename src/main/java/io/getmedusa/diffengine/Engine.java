package io.getmedusa.diffengine;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.engine.HTMLLayerBuildupEngineLogic;
import io.getmedusa.diffengine.engine.RecursiveDiffEngineLogic;
import io.getmedusa.diffengine.engine.TextEditEngineLogic;
import io.getmedusa.diffengine.model.HTMLLayer;

import java.util.*;

public class Engine {

    public Set<ServerSideDiff> calculate(String oldHTML, String newHTML) {
        Map<Integer, List<HTMLLayer>> oldHTMLLayersMap = HTMLLayerBuildupEngineLogic.interpretLayer(oldHTML);
        Map<Integer, List<HTMLLayer>> newHTMLLayersMap = HTMLLayerBuildupEngineLogic.interpretLayer(newHTML);

        Set<Integer> layers = new TreeSet<>(oldHTMLLayersMap.keySet());
        layers.addAll(newHTMLLayersMap.keySet());

        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        Set<ServerSideDiff> diffsText = new LinkedHashSet<>();
        for(int layer : layers) {
            var diffsForOneLayerArray = calculateForLayer(oldHTMLLayersMap, newHTMLLayersMap, layer);
            diffs.addAll(diffsForOneLayerArray[0]);
            diffsText.addAll(diffsForOneLayerArray[1]); //delayed add; you first want to have completed all layer structures before adding text changes
        }
        diffs.addAll(diffsText);
        return diffs;
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

}
