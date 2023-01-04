package io.getmedusa.diffengine.edits;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Set;
import java.util.stream.Stream;

class OneLayerSimpleEditsTest extends DiffEngineTest {

    private static Stream<Arguments> editParameters() {
        return Stream.of(Arguments.of(
                """
                        <section>
                            <p></p>
                        </section>
                        """,
                        """
                        <section>
                            <p>Hello world</p>
                        </section>
                        """
                ), Arguments.of(
                        """
                           <p>hello <b>BIG</b> world</p>
                        """,
                        """
                           <p>HELLO <b>big</b> WORLD</p>
                        """
                )
        );
    }

    @ParameterizedTest
    @MethodSource("editParameters")
    void testEdits(String oldHTML, String newHTML) {
        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
