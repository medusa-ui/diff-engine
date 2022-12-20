package io.getmedusa.diffengine.removals;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

class OneLayerSimpleRemovalTest extends DiffEngineTest {

    private static Stream<Arguments> removalParameters() {
        return Stream.of(Arguments.of(
                """
                        <section>
                            <div>
                                <p>1</p>
                                <p>2</p>
                            </div>
                        </section>
                        """,
                """
                <section>
                    <div>
                        <p>1</p>
                    </div>
                </section>
                """
        ),Arguments.of(
                """
                        <section>
                            <div>
                                <p>2</p>
                            </div>
                        </section>
                        """,
                        """
                        <section>
                            <div>
                            </div>
                        </section>
                        """
                ));
    }

    @ParameterizedTest
    @MethodSource("removalParameters")
    void testRemovals(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
