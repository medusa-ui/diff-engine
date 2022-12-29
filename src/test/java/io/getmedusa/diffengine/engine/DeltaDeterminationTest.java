package io.getmedusa.diffengine.engine;

import io.getmedusa.diffengine.model.meta.Delta;
import io.getmedusa.diffengine.model.HTMLLayer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

class DeltaDeterminationTest {

    @Test
    void testDeltaSimpleRemoval() {
        Delta delta = DeltaDeterminationEngineLogic.determine(layers("section", "div"), layers("section"));
        Assertions.assertTrue(delta.isDelete());
        Assertions.assertEquals(toXPath("div"), delta.getLayer().getXpath());
    }

    @Test
    void testDeltaSimpleAddition() {
        Delta delta = DeltaDeterminationEngineLogic.determine(layers("section"), layers("section", "div"));
        Assertions.assertTrue(delta.isInsert());
        Assertions.assertEquals(toXPath("div"), delta.getLayer().getXpath());
        Assertions.assertEquals(1, delta.getPosition()); //0-based index, added as second item, so 1
    }

    @Test
    void testDeltaSimpleOrder() {
        Delta delta = DeltaDeterminationEngineLogic.determine(layers("section", "div"), layers("div", "section"));
        Assertions.assertTrue(delta.isDelete());
        Assertions.assertEquals(toXPath("section"), delta.getLayer().getXpath());
    }

    private String toXPath(String tag) {
        return "/body[1]/" + tag + "[1]";
    }

    private List<HTMLLayer> layers(String... tags) {
        List<HTMLLayer> layers = new ArrayList<>();
        for(String tag : tags) {
            layers.add(new HTMLLayer(null, toXPath(tag), null));
        }
        return layers;
    }

}
