package io.getmedusa.diffengine.engine;

import io.getmedusa.diffengine.model.HTMLLayer;
import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.model.meta.TextNode;
import org.joox.JOOX;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

public class TextEditEngineLogic {

    private TextEditEngineLogic() {}

    public static LinkedHashSet<ServerSideDiff> handleTextEdits(List<HTMLLayer> newHTMLLayers, List<HTMLLayer> buildup) {
        LinkedHashSet<ServerSideDiff> diffs = new LinkedHashSet<>();

        //can only be a removal if it was in the original and not in the new ones anymore
        for(var potentialRemovalLayer : buildup) {
            if(potentialRemovalLayer.hasTextNode()) {
                final int index = findMatch(potentialRemovalLayer, newHTMLLayers);
                if (-1 == index) {
                    continue;
                }
                var match = newHTMLLayers.get(index);

                LinkedList<ServerSideDiff> determinedRemovals = determineTextNodeRemovals(match.getTextNodes(), potentialRemovalLayer.getTextNodes());

                diffs.addAll(determinedRemovals);
            }
        }

        //additions and edits are only based on the new data
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

                //if there's a different value = text edit
                LinkedList<ServerSideDiff> determinedEdits = determineTextNodeEdits(potentialEditLayer.getTextNodes(), match.getTextNodes());
                determinedAdditions.forEach(a -> determinedEdits.removeAll(determinedEdits.stream().filter(e -> e.getXpath().equals(a.getXpath())).toList()));
                //determinedRemovals.forEach(a -> determinedRemovals.removeAll(determinedRemovals.stream().filter(e -> e.getXpath().equals(a.getXpath())).toList()));
                diffs.addAll(determinedEdits);
            }
        }

        return diffs;
    }

    private static LinkedList<ServerSideDiff> determineTextNodeEdits(LinkedList<TextNode> newTextNodes, LinkedList<TextNode> oldTextNodes) {
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

    private static LinkedList<ServerSideDiff> determineTextNodeRemovals(LinkedList<TextNode> newTextNodes, LinkedList<TextNode> oldTextNodes) {
        if(newTextNodes == null) {
            newTextNodes = new LinkedList<>();
        }
        var removals = new LinkedList<>(oldTextNodes);
        // do text() removals in reverse order
        Collections.reverse(removals);
        newTextNodes.forEach(removals::remove);
        return new LinkedList<>(removals.stream().filter(r -> !r.getContent().trim().isBlank()).map(ServerSideDiff::buildRemoval).toList());
    }

    private static LinkedList<ServerSideDiff> determineTextNodeAdditions(HTMLLayer layer, LinkedList<TextNode> newTextNodes, LinkedList<TextNode> oldTextNodes) {
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

    private static int findMatch(HTMLLayer layerToMatch, List<HTMLLayer> listToMatchIn) {
        for (int i = 0; i < listToMatchIn.size(); i++) {
            if(listToMatchIn.get(i).equals(layerToMatch)) {
                return i;
            }
        }
        return -1;
    }

}
