package io.getmedusa.diffengine.engine;

import io.getmedusa.diffengine.model.Delta;
import io.getmedusa.diffengine.model.HTMLLayer;

import java.util.List;

public class DeltaDeterminationEngineLogic {

    private DeltaDeterminationEngineLogic() {}

    public static Delta determine(List<HTMLLayer> oldHTMLLayers, List<HTMLLayer> newHTMLLayers) {

        //removals
        //ie in old but not in new
        //loop over old, if respective one not in new, then a removal
        for(var oldHTMLLayer : oldHTMLLayers) {
            if(!newHTMLLayers.contains(oldHTMLLayer)) {
                return Delta.deletionDelta(oldHTMLLayer, oldHTMLLayers.indexOf(oldHTMLLayer));
            }
        }

        //additions
        //ie in new but not in old
        //loop over new, if respective one not in old, then an addition; make not of index where it would be added
        for(var newHTMLLayer : newHTMLLayers) {
            if(!oldHTMLLayers.contains(newHTMLLayer)) {
                return Delta.insertionDelta(newHTMLLayer, newHTMLLayers.indexOf(newHTMLLayer));
            }
        }

        //order problems, solved by removals
        //go index by index, if non match = removal
        for (int i = 0; i < oldHTMLLayers.size(); i++) {
            var oldHTMLLayer = oldHTMLLayers.get(i);
            var newHTMLLayer = newHTMLLayers.get(i);
            if(!oldHTMLLayer.equals(newHTMLLayer)) {
                return Delta.deletionDelta(oldHTMLLayer, i);
            }
        }

        return null; //default null, no delta found
    }

}
