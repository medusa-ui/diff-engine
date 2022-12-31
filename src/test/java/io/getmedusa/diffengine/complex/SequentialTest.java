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
                <div id="top_top" th:if="${top}">top <code>top</code></div>
                <p id="top_mid" th:if="${mid}">mid <code>top</code></p>
                <p id="top_down" th:if="${down}">down <code>top</code></p>
                <button id="always">always there</button>
                <div id="down_top" th:if="${top}">top <code>down</code></div>
                <p id="down_mid" th:if="${mid}">mid <code>down</code></p>
                <p id="down_down" th:if="${down}">down <code>down</code></p>
            </section>
            """;
    List<List<Boolean>> booleans =
            List.of(
                    List.of(true, true, false),
                    List.of(true, false, false),
                    List.of(false, false, false),
                    List.of(true, true, false),
                    List.of(false, true, false)
            );

    @Test
    void sequentialChanges(){
        String start = rendered(true, true, true).html();
        for (List<Boolean> list : booleans) {
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
