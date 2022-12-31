package io.getmedusa.diffengine.testengine.removals;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This test, tests the test engine more. Failing means that we won't be able to properly test the engine itself.
 * The implementation should be reflected in JS.
 */
class TestEngineRemovalTest extends DiffEngineTest {

    private static Stream<Arguments> removalParameters() {
        return Stream.of(Arguments.of(
                        """
                                <section>
                                    <section>DELETE ME</section>
                                </section>
                                """,
                        """
                        <section>
                        </section>
                        """
                ));
    }

    @ParameterizedTest
    @MethodSource("removalParameters")
    void testBeforeAdditions(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = new LinkedHashSet<>();
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.REMOVAL);
        diff.setXpath("/html[1]/body[1]/section[1]/section[1]");
        diffs.add(diff);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
