package io.getmedusa.diffengine.additions;

import io.getmedusa.diffengine.diff.ServerSideDiff;
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
                ),
                Arguments.of(
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
                ),
                Arguments.of(
                """
                        <section>
                            <p>1</p>
                            <section>2</section>
                        </section>
                        """,
                        """
                        <section>
                            <p>1</p>
                            <section>2</section>
                            <div>3</div>
                        </section>
                        """
                ),
                Arguments.of(
            """
                        <section>
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
                ),
                Arguments.of(
            """
                        <section>
                            <section>2</section>
                        </section>
                        """,
                        """
                        <section>
                            <p>1</p>
                            <section>2</section>
                            <div>3</div>
                        </section>
                        """
                ),
                Arguments.of(
            """
                        <section>
                            <p>1</p>
                        </section>
                        """,
                        """
                        <section>
                            <p>1</p>
                            <section>2</section>
                            <div>3</div>
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
                            <p>1</p>
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
                            <p>1</p>
                            <section>2</section>
                            <div>3</div>
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
