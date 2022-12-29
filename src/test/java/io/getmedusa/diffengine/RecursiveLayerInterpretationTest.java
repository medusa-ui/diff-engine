package io.getmedusa.diffengine;

import io.getmedusa.diffengine.engine.HTMLLayerBuildupEngineLogic;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class RecursiveLayerInterpretationTest {

    @Test
    void testSimpleLayer() {
        var layers = HTMLLayerBuildupEngineLogic.interpretLayer("<section><p></p></section>");
        Assertions.assertEquals(3, layers.keySet().size());
        Assertions.assertEquals(1, layers.get(1).size()); //body
        Assertions.assertEquals(1, layers.get(2).size()); //section
        Assertions.assertEquals(1, layers.get(3).size()); //p
        //System.out.println(layers.get(1));
        //System.out.println(layers.get(2));
    }

    @Test
    void testSimpleLayer2() {
        var layers = HTMLLayerBuildupEngineLogic.interpretLayer("<section><p>A</p><div><p>2</p></div><p>B</p></section>");
        Assertions.assertEquals(4, layers.keySet().size());
        Assertions.assertEquals(1, layers.get(1).size()); //body
        Assertions.assertEquals(1, layers.get(2).size()); //section
        Assertions.assertEquals(3, layers.get(3).size()); //(section>)p, (section>)div, (section>)p
        Assertions.assertEquals(1, layers.get(4).size()); //(div>)p
        //System.out.println(layers.get(2));
        //System.out.println(layers.get(3));
        //System.out.println(layers.get(4));
    }

    @Test
    void testSimpleLayer3() {
        var layers = HTMLLayerBuildupEngineLogic.interpretLayer("<section><div><p></p></div></section>");
        Assertions.assertEquals(4, layers.keySet().size());
        Assertions.assertEquals(1, layers.get(1).size()); //body
        Assertions.assertEquals(1, layers.get(2).size()); //section
        Assertions.assertEquals(1, layers.get(3).size()); //(section>)div
        Assertions.assertEquals(1, layers.get(4).size()); //(div>)p
        //System.out.println(layers.get(2));
        //System.out.println(layers.get(3));
        //System.out.println(layers.get(4));
    }

    @Test
    void testSimpleLayer4() {
        var layers = HTMLLayerBuildupEngineLogic.interpretLayer("<section><div><p></p><p></p></div></section>");
        Assertions.assertEquals(4, layers.keySet().size());
        Assertions.assertEquals(1, layers.get(1).size()); //body
        Assertions.assertEquals(1, layers.get(2).size()); //section
        Assertions.assertEquals(1, layers.get(3).size()); //(section>)div
        Assertions.assertEquals(2, layers.get(4).size()); //(div>)p, (div>)p
        //System.out.println(layers.get(2));
        //System.out.println(layers.get(3));
        //System.out.println(layers.get(4));
    }

    @Test
    void testMultiLayer() {
        var layers = HTMLLayerBuildupEngineLogic.interpretLayer("<outer><section><p></p></section><div><p></p></div></outer>");
        Assertions.assertEquals(4, layers.keySet().size());
        Assertions.assertEquals(1, layers.get(1).size()); //body
        Assertions.assertEquals(1, layers.get(2).size()); //outer
        Assertions.assertEquals(2, layers.get(3).size()); //(outer>)section, (outer>)div
        Assertions.assertEquals(2, layers.get(4).size()); //(section>)p, (div>)p
        //System.out.println(layers.get(2));
        //System.out.println(layers.get(3));
        //System.out.println(layers.get(4));
    }
}
