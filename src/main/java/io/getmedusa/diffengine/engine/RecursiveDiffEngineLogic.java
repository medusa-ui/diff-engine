package io.getmedusa.diffengine.engine;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.model.HTMLLayer;

import java.util.LinkedList;
import java.util.List;

public class RecursiveDiffEngineLogic {

    private RecursiveDiffEngineLogic() {}

    public static ServerSideDiff recurringPatch(LinkedList<HTMLLayer> buildup, List<HTMLLayer> newHTMLLayers) {
        Patch<HTMLLayer> patch = DiffUtils.diff(buildup, newHTMLLayers);
        if(!patch.getDeltas().isEmpty()) {
            var delta = patch.getDeltas().get(0);
            //System.out.println(delta);

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
                    //if you want to insert a /body[1]/section[1]/div[1]/p[1]
                    //but the buildup layers are /body[1]/section[1]/h5[1]/code[1]
                    //then adding 'before' isn't good enough, you need to get into the right xpath
                    //otherwise you end up WITHIN h5[1], which is not the intention according to the XPATH
                    //this might instead be a better IN for your parent pom
                    final HTMLLayer lastLayerMatchingXPathParent = getLastLayerMatchingXPathParent(buildup, layerToAdd);
                    if(lastLayerMatchingXPathParent != null) {
                        diff = ServerSideDiff.buildNewBeforeDiff(layerToAdd, buildup.get(indexPosition));
                    } else { //and should there not be one of those, we do an in
                        diff = ServerSideDiff.buildInDiff(layerToAdd);
                    }
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
}
