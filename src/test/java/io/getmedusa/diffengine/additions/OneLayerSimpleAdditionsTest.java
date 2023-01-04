package io.getmedusa.diffengine.additions;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

class OneLayerSimpleAdditionsTest extends DiffEngineTest {

    private static Stream<Arguments> additionParameters() {
        return Stream.of(Arguments.of(
                """
                        <section>
                            <section></section>
                            <div></div>
                        </section>
                        """,
                        """
                        <section>
                            <p></p>
                            <section></section>
                            <div></div>
                        </section>
                        """
                ),
                Arguments.of(
                """
                        <section>
                            <p></p>
                            <div></div>
                        </section>
                        """,
                        """
                        <section>
                            <p></p>
                            <section></section>
                            <div></div>
                        </section>
                        """
                ),
                Arguments.of(
                """
                        <section>
                            <p></p>
                            <section></section>
                        </section>
                        """,
                        """
                        <section>
                            <p></p>
                            <section></section>
                            <div></div>
                        </section>
                        """
                ),
                Arguments.of(
            """
                        <section>
                            <div></div>
                        </section>
                        """,
                        """
                        <section>
                            <p></p>
                            <section></section>
                            <div></div>
                        </section>
                        """
                ),
                Arguments.of(
            """
                        <section>
                            <section></section>
                        </section>
                        """,
                        """
                        <section>
                            <p></p>
                            <section></section>
                            <div></div>
                        </section>
                        """
                ),
                Arguments.of(
            """
                        <section>
                            <p></p>
                        </section>
                        """,
                        """
                        <section>
                            <p></p>
                            <section></section>
                            <div></div>
                        </section>
                        """
                ),
                Arguments.of(
                        """
                                    <section>
                                    </section>
                                    """,
                        """
                        <section>
                            <p></p>
                        </section>
                        """
                ),
                Arguments.of(
                        """
                                    <section>
                                    </section>
                                    """,
                        """
                        <section>
                            <p></p>
                            <section></section>
                            <div></div>
                        </section>
                        """
                ), Arguments.of(
             """
                        <section>
                            <p>HELLO</p>
                        </section>
                        """,
                        """
                        <section>
                            <p>HELLO</p>
                            World
                        </section>
                        """
                ), Arguments.of(
                        """
                        <section>
                           <p></p>
                           <span></span>
                           <span></span>
                           <p></p>
                        </section>
                        """,
                        """
                        <section>
                           <p></p>
                           <p></p>
                           <span></span>
                           <span></span>
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
