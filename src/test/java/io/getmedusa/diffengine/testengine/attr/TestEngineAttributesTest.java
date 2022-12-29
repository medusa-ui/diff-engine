package io.getmedusa.diffengine.testengine.attr;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * This test, tests the test engine more. It failing means we won't be able to properly test the engine itself.
 * The implementation should be reflected in JS.
 */
class TestEngineAttributesTest extends DiffEngineTest {

    @Test
    void testAttributeAddition() {
        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.ATTR_CHANGE);
        diff.setXpath("/html[1]/body[1]/section[1]/section[1]");
        diff.setAttributeKey("class");
        diff.setAttributeValue("hello");
        diffs.add(diff);
        applyAndTest("""
                        <section>
                            <section>ABC</section>
                        </section>
                        """,
                """
                        <section>
                            <section class="hello">ABC</section>
                        </section>
                        """, diffs);
    }

    @Test
    void testAttributeRemoval() {
        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.ATTR_CHANGE);
        diff.setXpath("/html[1]/body[1]/section[1]/section[1]");
        diff.setAttributeKey("class");
        diff.setAttributeValue(null);
        diffs.add(diff);
        applyAndTest("""
                        <section>
                            <section class="hello">ABC</section>
                        </section>
                        """,
                """
                        <section>
                            <section>ABC</section>
                        </section>
                        """, diffs);
    }
}
