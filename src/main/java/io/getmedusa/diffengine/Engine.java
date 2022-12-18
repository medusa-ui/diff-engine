package io.getmedusa.diffengine;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.model.HTMLLayer;
import org.joox.JOOX;
import org.joox.Match;

import java.util.*;

public class Engine {

    public Set<ServerSideDiff> calculate(String oldHTML, String newHTML) {
        List<ServerSideDiff> diffsBefore = new LinkedList<>();
        List<ServerSideDiff> diffsAfter = new LinkedList<>();

        //TODO - per section
        List<HTMLLayer> oldHTMLLayers = interpretLayer(JOOX.$(oldHTML));
        List<HTMLLayer> newHTMLLayers = interpretLayer(JOOX.$(newHTML));

        //find addition
        List<HTMLLayer> didntExistBefore = new LinkedList<>(newHTMLLayers);
        oldHTMLLayers.forEach(didntExistBefore::remove);

        //find where they get added (before/after)
        for(HTMLLayer newLayer : didntExistBefore) {
            int indexFound = findMatch(newLayer, newHTMLLayers);
            if(indexFound == 0) {
                //first one, so has to be an add before
                ServerSideDiff possibleBeforeDiff = checkIfPossibleBeforeDiff(indexFound, newLayer, newHTMLLayers, oldHTMLLayers, 99);

                if(possibleBeforeDiff == null) {
                    possibleBeforeDiff = ServerSideDiff.buildInDiff(newLayer); //does it make sense to add an in
                }

                diffsBefore.add(possibleBeforeDiff);
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

                //if neither exists ... idk yet; could happen if you add multiple items at once?
                //TODO
                throw new RuntimeException("Not yet implemented - Unknown scenario - Following an in?");
            }
        }

        //this feels hacky? but reverse it to maintain proper order when adding multiple items
        //the order of afters should be A - B
        //the order of before should be B - A (reversed)
        List<ServerSideDiff> diffsAfterReverse = new LinkedList<>(diffsAfter);
        Collections.reverse(diffsBefore);
        diffsBefore.addAll(diffsAfterReverse);
        return new HashSet<>(diffsBefore);
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

    private static List<HTMLLayer> interpretLayer(Match match) {
        List<HTMLLayer> layers = new LinkedList<>();
        for (var child : match.children()) {
            layers.add(new HTMLLayer(child));
        }
        return layers;
    }

}
