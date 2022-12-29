package io.getmedusa.diffengine.attr;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

class OneLayerSimpleAttrChangesTest extends DiffEngineTest {

    private static Stream<Arguments> attrChangeParams() {
        return Stream.of(Arguments.of(
                """
                        <section>
                            <section></section>
                        </section>
                        """,
                        """
                        <section>
                            <section class="a"></section>
                        </section>
                        """
                ),
                Arguments.of(
                """
                        <section>
                            <section class="a"></section>
                        </section>
                        """,
                        """
                        <section>
                            <section></section>
                        </section>
                        """
                ),
                Arguments.of(
                """
                        <section>
                            <section class="a"></section>
                            <section></section>
                        </section>
                        """,
                        """
                        <section>
                            <section></section>
                            <section class="a"></section>
                        </section>
                        """
                ),
                Arguments.of(
            """
                        <section>
                            <section class="a" color="red"></section>
                            <section z="213"></section>
                        </section>
                        """,
                        """
                        <section>
                            <section x="y"></section>
                            <section c="b"></section>
                        </section>
                        """
                ));
    }

    @ParameterizedTest
    @MethodSource("attrChangeParams")
    void testAttrChanges(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
