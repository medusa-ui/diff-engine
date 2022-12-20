package io.getmedusa.diffengine.additions;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

class NestedLayerSimpleAdditionsTest extends DiffEngineTest {

    private static Stream<Arguments> additionParameters() {
        return Stream.of(Arguments.of(
                """
                        <section>
                            <div>
                                <p>1</p>
                            </div>
                        </section>
                        """,
                        """
                        <section>
                            <div>
                                <p>1</p>
                                <p>2</p>
                            </div>
                        </section>
                        """
                ),Arguments.of(
                """
                        <section>
                        </section>
                        """,
                        """
                        <section>
                            <p>A</p>
                            <div>
                                <p>1</p>
                            </div>
                            <p>B</p>
                        </section>
                        """
                ),Arguments.of(
                """
                        <section>
                            <div>
                                <p>1</p>
                            </div>
                        </section>
                        """,
                        """
                        <section>
                            <div>
                                <p>1</p>
                                <p>2</p>
                                <p>3 <span>A</span></p>
                                <p>4</p>
                                <p>5</p>
                            </div>
                        </section>
                        """
                ));
    }

    @ParameterizedTest
    @MethodSource("additionParameters")
    void testAdditions(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
