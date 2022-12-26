package io.getmedusa.diffengine.complex;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.junit.jupiter.api.Test;

import java.util.Set;

class ComplexTest extends DiffEngineTest {

    @Test
    void testComplex1() {
        String oldHTML = """
                <section>
                    <p>A</p>
                    <p>B</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <p>A</p>
                    <div>
                        <p>2</p>
                    </div>
                    <p>B</p>
                </section>""";

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex1B() {
        String oldHTML = """
                <section>
                    <p>A</p>
                    <p>B</p>
                    <p>C</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <p>A</p>
                    <div>
                        <p>1</p>
                    </div>
                    <p>B</p>
                    <div>
                        <p>2</p>
                    </div>
                    <p>C</p>
                </section>""";

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex2() {
        String oldHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <p>3</p>" +
                "   <div>4 change</div>" +
                "   <h5>5</h5>" +
                "   <p>7</p>" +
                "  </section>";

        final String newHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <div><p>2</p></div>" + //<- new
                "   <p>3</p>" +
                "   <div>4 change</div>" +
                "   <h5>5</h5>" +
                "   <div><p>6</p></div>" + //<- new
                "   <p>7</p>" +
                "  </section>";

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex3() {
        String oldHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <div>" +
                "    <p>2</p>" +
                "   </div>" +
                "   <h5>5</h5>" +
                "   <div>" +
                "    <p>6</p>" +
                "   </div>" +
                "  </section>";

        final String newHTML = "<section>" +
                "   <h5>1</h5>" +
                "   <div>" +
                "    <p>2</p>" +
                "   </div>" +
                "   <p>3</p>" + //<- new
                "   <h5>5</h5>" +
                "   <div>" +
                "    <p>6</p>" +
                "   </div>" +
                "   <p>7</p>" + //<- new
                "  </section>";

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex4() {
        String oldHTML = """
                <section>
                    <span>1</span>
                    <h5>1 <code>th:if</code>Above button</h5>
                    
                    <p>3 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                    <div><button>4 change</button>
                    </div>
                    <h5>5 <code>th:if</code>Below button</h5>
                    
                    <p>7 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code>, but below the button</p>
                </section>
                """;

        final String newHTML = """
                <section>
                    <span>2</span>
                    <h5>1 <code>th:if</code>Above button</h5>
                    <div>
                    <p>2 TOP If top is <code>true</code> this should be visible on <code>top</code> of the page</p>
                    </div>
                    <p>3 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code></p>
                    <div><button>4 change</button>
                    </div>
                    <h5>5 <code>th:if</code>Below button</h5>
                    <div>
                    <p>6 TOP If top is <code>true</code> this should be visible on <code>top</code> of the page, but below the button</p>
                    </div>
                    <p>7 BOTTOM If bottom is <code>true</code> this should be visible at the <code>bottom</code>, but below the button</p>
                </section>
                """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex5() {
        String oldHTML = """
                <section>
                    <div>X <code>Y</code> Z</div>
                </section>
                """;

        final String newHTML = """
                <section>
                    <p>1 <code>2</code> 3</p>
                </section>
                """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }

    @Test
    void testComplex6() {
        String oldHTML = """
                <section>
                    <div>X Z</div>
                </section>
                """;

        final String newHTML = """
                <section>
                    <div>X <code>Y</code> Z</div>
                </section>
                """;

        Set<ServerSideDiff> diffs = engine.calculate(oldHTML, newHTML);
        applyAndTest(oldHTML, newHTML, diffs);
    }
}
