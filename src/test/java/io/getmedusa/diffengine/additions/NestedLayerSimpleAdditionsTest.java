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
                                <p></p>
                            </div>
                        </section>
                        """,
                        """
                        <section>
                            <div>
                                <p></p>
                                <p></p>
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
                            <p></p>
                            <div>
                                <p></p>
                            </div>
                            <p></p>
                        </section>
                        """
                ),Arguments.of(
                """
                        <section>
                            <div>
                                <p></p>
                            </div>
                        </section>
                        """,
                        """
                        <section>
                            <div>
                                <p></p>
                                <p></p>
                                <p><span></span></p>
                                <p></p>
                                <p></p>
                            </div>
                        </section>
                        """
                ),Arguments.of(
                """
                    <section>
                     <div></div>
                    </section>
                    """,
                    """
                    <section>
                     <div><p></p></div>
                     <div></div>
                     <div><p></p></div>
                    </section>
                """
        ),Arguments.of(
                """
                    <outer>
                    </outer>
                        """,
                """
                    <outer>
                     <section>
                        <p></p>
                     </section>
                     <div>
                        <p></p>
                     </div>
                    </outer>
                """
        ),Arguments.of(
                """
                            <parent>
                             <p></p>
                             <div></div>
                            </parent>
                        """,
                        """
                        <parent>
                         <div></div>
                         <p></p>
                         <div></div>
                         <div></div>
                        </parent>
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
