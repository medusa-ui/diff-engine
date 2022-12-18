package io.getmedusa.diffengine.testengine.additions;

import io.getmedusa.diffengine.diff.ServerSideDiff;
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
class TestEngineAdditionsTest extends DiffEngineTest {

    private static Stream<Arguments> additionBeforeParameters() {
        return Stream.of(Arguments.of(
                        """
                                <section>
                                    <section>2</section>
                                    <div>3</div>
                                </section>
                                """,
                        """
                        <section>
                            <p>1</p>
                            <section>2</section>
                            <div>3</div>
                        </section>
                        """
                ));
    }

    @ParameterizedTest
    @MethodSource("additionBeforeParameters")
    void testBeforeAdditions(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.ADDITION);
        diff.setBefore("/html[1]/body[1]/section[1]/section[1]");
        diff.setContent("<p>1</p>");
        diffs.add(diff);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    private static Stream<Arguments> additionAfterParameters() {
        return Stream.of(Arguments.of(
                """
                        <section>
                            <p>1</p>
                            <div>3</div>
                        </section>
                        """,
                """
                <section>
                    <p>1</p>
                    <section>2</section>
                    <div>3</div>
                </section>
                """
        ));
    }

    @ParameterizedTest
    @MethodSource("additionAfterParameters")
    void testAfterAdditions(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.ADDITION);
        diff.setAfter("/html[1]/body[1]/section[1]/p[1]");
        diff.setContent("<section>2</section>");
        diffs.add(diff);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    private static Stream<Arguments> additionWithinParameters() {
        return Stream.of(Arguments.of(
                """
                        <section>
                        </section>
                        """,
                """
                <section>
                    <p>1</p>
                </section>
                """
        ));
    }

    @ParameterizedTest
    @MethodSource("additionWithinParameters")
    void testWithinAdditions(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.ADDITION);
        diff.setIn("/html[1]/body[1]/section[1]");
        diff.setContent("<p>1</p>");
        diffs.add(diff);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
