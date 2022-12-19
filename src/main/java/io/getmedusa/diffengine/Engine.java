package io.getmedusa.diffengine;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.model.HTMLLayer;
import org.joox.JOOX;
import org.joox.Match;

import java.util.*;

public class Engine {

    public Set<ServerSideDiff> calculate(String oldHTML, String newHTML) {
        Map<Integer, List<HTMLLayer>> oldHTMLLayersMap = interpretLayer(JOOX.$(oldHTML));
        Map<Integer, List<HTMLLayer>> newHTMLLayersMap = interpretLayer(JOOX.$(newHTML));

        Set<Integer> layers = new TreeSet<>(oldHTMLLayersMap.keySet());
        layers.addAll(newHTMLLayersMap.keySet());

        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        for(int layer : layers) {
            var diffsForOneLayer = calculateForLayer(oldHTMLLayersMap, newHTMLLayersMap, layer);
            diffs.addAll(diffsForOneLayer);
        }
        return diffs;
    }

    private LinkedHashSet<ServerSideDiff> calculateForLayer(Map<Integer, List<HTMLLayer>> oldHTMLLayersMap, Map<Integer, List<HTMLLayer>> newHTMLLayersMap, int layer) {
        List<ServerSideDiff> diffsBefore = new LinkedList<>();
        List<ServerSideDiff> diffsAfter = new LinkedList<>();
        List<ServerSideDiff> diffsIn = new LinkedList<>();

        List<HTMLLayer> oldHTMLLayers = oldHTMLLayersMap.get(layer);
        List<HTMLLayer> newHTMLLayers = newHTMLLayersMap.get(layer);

        //find addition
        List<HTMLLayer> didntExistBefore = new LinkedList<>(newHTMLLayers);
        oldHTMLLayers.forEach(didntExistBefore::remove);

        //find where they get added (before/after)
        for(HTMLLayer newLayer : didntExistBefore) {
            int indexFound = findMatch(newLayer, newHTMLLayers);
            if(indexFound == 0) {
                //first one, so has to be an add before
                ServerSideDiff possibleBeforeDiff = checkIfPossibleBeforeDiff(indexFound, newLayer, newHTMLLayers, oldHTMLLayers, 99);
                if (possibleBeforeDiff != null) {
                    diffsBefore.add(possibleBeforeDiff);
                } else {
                    diffsIn.add(ServerSideDiff.buildInDiff(newLayer));
                }
            } else {
                //does next (i+1) exist in old? if so add before
                ServerSideDiff possibleBeforeDiff = checkIfPossibleBeforeDiff(indexFound, newLayer, newHTMLLayers, oldHTMLLayers, 1);
                if(null != possibleBeforeDiff) {
                    diffsBefore.add(possibleBeforeDiff);
                    continue;
                }

                //does previous (i-1) exist in old? if so add after
                ServerSideDiff possiblePreviousDiff = checkIfPossiblePreviousDiff(indexFound, newLayer, newHTMLLayers, oldHTMLLayers, 99);
                if(null != possiblePreviousDiff) {
                    diffsAfter.add(possiblePreviousDiff);
                    continue;
                }

                //if neither exists ...; could happen if you add multiple items at once?
                diffsIn.add(ServerSideDiff.buildInDiff(newLayer));
            }
        }

        //maintain proper order when adding multiple items
        //the order of afters should be B - A (reversed)
        //the order of before and in should be A - B
        List<ServerSideDiff> diffsAfterReverse = new LinkedList<>(diffsAfter);
        Collections.reverse(diffsAfterReverse);
        diffsBefore.addAll(diffsAfterReverse); //diffsBefore <- diffsAfterReverse
        diffsIn.addAll(diffsBefore); //diffsIn <- diffsBefore (<- diffsAfterReverse)

        return new LinkedHashSet<>(diffsIn);
    }

    private ServerSideDiff checkIfPossiblePreviousDiff(int indexFound, HTMLLayer newLayer, List<HTMLLayer> newHTMLLayers,
                                                       List<HTMLLayer> oldHTMLLayers, int maxDepth) {
        if(indexFound-1 < 0 || maxDepth == 0) {
            return null;
        }
        final HTMLLayer previous = newHTMLLayers.get(indexFound - 1);
        int indexPrevInOld = findMatch(previous, oldHTMLLayers);
        if(-1 != indexPrevInOld) {
            //add after
            return ServerSideDiff.buildNewAfterDiff(newLayer, oldHTMLLayers.get(indexPrevInOld));
        } else {
            return checkIfPossiblePreviousDiff(--indexFound, newLayer, newHTMLLayers, oldHTMLLayers, --maxDepth);
        }
    }

    private ServerSideDiff checkIfPossibleBeforeDiff(int indexFound, HTMLLayer newLayer, List<HTMLLayer> newHTMLLayers,
                                                     List<HTMLLayer> oldHTMLLayers, int maxDepth) {
        if(newHTMLLayers.size() <= (indexFound + 1) || maxDepth == 0) {
            return null;
        }

        final HTMLLayer next = newHTMLLayers.get(indexFound + 1);
        int indexNextInOld = findMatch(next, oldHTMLLayers);
        if(-1 != indexNextInOld) {
            //add before
            return ServerSideDiff.buildNewBeforeDiff(newLayer, oldHTMLLayers.get(indexNextInOld));
        } else {
            return checkIfPossibleBeforeDiff(++indexFound, newLayer, newHTMLLayers, oldHTMLLayers, --maxDepth);
        }
    }

    private static int findMatch(HTMLLayer layerToMatch, List<HTMLLayer> listToMatchIn) {
        for (int i = 0; i < listToMatchIn.size(); i++) {
            if(listToMatchIn.get(i).equals(layerToMatch)) {
               return i;
            }
        }
        return -1;
    }

    private static Map<Integer, List<HTMLLayer>> interpretLayer(Match match) {
        Map<Integer, List<HTMLLayer>> layerMap = new HashMap<>();
        layerMap.put(1, List.of(new HTMLLayer(match)));
        recursive(match, layerMap, 2);
        return layerMap;
    }

    private static void recursive(Match match, Map<Integer, List<HTMLLayer>> map, int layer) {
        List<HTMLLayer> layers = new LinkedList<>();
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
