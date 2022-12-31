package io.getmedusa.diffengine.complex;

import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.DiffEngineTest;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class SequentialTest extends DiffEngineTest {

    String template = """
            <section>
                <div id="top_top" th:if="${top}">on <code>top</top> on <code>top</code> of the button</div>
                <p id="top_mid" th:if="${mid}"> at <code>mid</code> on <code>top</code> of the button</p>
                <div><p id="top_down" th:if="${down}">must be <code>down</code>, but on <code>top</code> of the button</p></div>
                <button id="always">always there</button>
                <div id="down_top" th:if="${top}"><code>top</code> <code>down</code> the button</div>
                <p id="down_mid" th:if="${mid}"><code>mid</code> <code>down</code>the button</p>
                <div><p id="down_down" th:if="${down}">must last <code>down</code> and <code>down</code> the button</p></div>
            </section>
            """;
    List<List<Boolean>> booleans =
            List.of(
                    List.of(true, true, false),
                    List.of(true, false, false),
                    List.of(false, false, false),
                    List.of(true, true, false),
                    List.of(false, true, false),
                    List.of(true, true, true),
                    List.of(false, true, false)
            );

    @Test
    void sequentialChanges(){
        int run = 1;
        String start = rendered(true, true, true).html();
        for (List<Boolean> list : booleans) {
            System.out.printf("run: %s with top: %s, mid: %s, down: %s\n", run++ ,list.get(0), list.get(1), list.get(2));
            start = sequentialTest(start, list.get(0), list.get(1), list.get(2), true);
        }
    }
    String sequentialTest(String start, boolean top, boolean mid, boolean down, boolean trace) {
        String changed = rendered(top, mid, down).html();
        Set<ServerSideDiff> calculated = engine.calculate(start, changed);
        applyAndTest(start, changed, calculated, trace);
        return changed;
    }

    Element rendered(boolean top, boolean mid, boolean down) {
        String initial = template.replace("th:if=\"${top}\"", visibleClass(top))
                .replace("th:if=\"${mid}\"", visibleClass(mid))
                .replace("th:if=\"${down}\"", visibleClass(down));
        Document document = Jsoup.parse(initial);
        document.select(".hide").forEach(Element::remove);
        return document.body();
    }

    String visibleClass(boolean visible) {
        String show = visible ? "show" : "hide";
        return "class='%s'".formatted(show);
    }

}
