package io.getmedusa.diffengine.engine;

import io.getmedusa.diffengine.model.HTMLLayer;
import io.getmedusa.diffengine.model.ServerSideDiff;

import java.util.LinkedHashSet;
import java.util.List;

public class AttrChangeEngineLogic {

    private AttrChangeEngineLogic() {}


    public static LinkedHashSet<ServerSideDiff> handleAttrEdits(List<HTMLLayer> newHTMLLayers, List<HTMLLayer> oldHTMLLayers) {
        LinkedHashSet<ServerSideDiff> diffs = new LinkedHashSet<>();
        for (int i = 0; i < newHTMLLayers.size(); i++) {
            var newLayer = newHTMLLayers.get(i);
            var oldLayer = oldHTMLLayers.get(i);

            for(var entry : newLayer.getAttributes().entrySet()) {
                var oldValue = oldLayer.getAttributes().get(entry.getKey());
                if(!entry.getValue().equals(oldValue)) {
                    //removal or new value
                    diffs.add(ServerSideDiff.buildAttrChange(newLayer.getXpath(), entry.getKey(), entry.getValue()));
                }
            }

            for(var entry : oldLayer.getAttributes().entrySet()) {
                var newValue = newLayer.getAttributes().get(entry.getKey());
                if(!entry.getValue().equals(newValue)) {
                    diffs.add(ServerSideDiff.buildAttrChange(newLayer.getXpath(), entry.getKey(), newValue));
                }
            }
        }
        return diffs;
    }
}
