package io.getmedusa.diffengine.testengine.edits;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This test, tests the test engine more. It failing means we won't be able to properly test the engine itself.
 * The implementation should be reflected in JS.
 */
class TestEngineEditsTest extends DiffEngineTest {

    private static Stream<Arguments> editParameters() {
        return Stream.of(Arguments.of(
            """
                        <section>
                            <p>A</p>
                        </section>
                        """,
                        """
                        <section>
                            <p>B</p>
                        </section>
                        """
                ));
    }

    @ParameterizedTest
    @MethodSource("editParameters")
    void testEditParameters(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.TEXT_EDIT);
        diff.setXpath("/html[1]/body[1]/section[1]/p[1]/text()[0]");
        diff.setContent("B");
        diffs.add(diff);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
