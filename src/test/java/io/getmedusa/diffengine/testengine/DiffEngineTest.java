package io.getmedusa.diffengine.testengine;

import io.getmedusa.diffengine.Engine;
import io.getmedusa.diffengine.diff.ServerSideDiff;
import io.getmedusa.diffengine.testengine.meta.DiffEngineTestAdditionLogic;
import io.getmedusa.diffengine.testengine.meta.DiffEngineTestEditLogic;
import io.getmedusa.diffengine.testengine.meta.DiffEngineTestRemovalLogic;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public abstract class DiffEngineTest {

    protected static final Engine engine = new Engine();

    protected void applyAndTest(String oldHTML, String newHTML, Set<ServerSideDiff> diffSet) {
        Assertions.assertNotNull(diffSet, "Expected at least 1 diff");
        List<ServerSideDiff> diffs = new LinkedList<>(diffSet);
        Assertions.assertTrue(diffs.size() >= 1, "Expected at least 1 diff");

        Document html = Jsoup.parse(oldHTML);

        for(ServerSideDiff diff : diffs) {
            System.out.println(diff);
            html = applyDiff(html, diff);
        }

        Document expectedHTML = Jsoup.parse(newHTML);

        Assertions.assertEquals(expectedHTML.outerHtml().replace(" ", ""), html.outerHtml().replace(" ", ""),"Rebuilt HTML and expected HTML do not match");
    }

    private Document applyDiff(Document html, ServerSideDiff diff) {
        if(diff.isAddition()) {
            return DiffEngineTestAdditionLogic.applyAddition(html, diff);
        } else if(diff.isRemoval()) {
            return DiffEngineTestRemovalLogic.applyRemoval(html, diff);
        } else if(diff.isEdit()) {
            return DiffEngineTestEditLogic.applyEdit(html, diff);
        }
        return html;
    }
}
