package io.getmedusa.diffengine.testengine;

import io.getmedusa.diffengine.Engine;
import io.getmedusa.diffengine.model.ServerSideDiff;
import io.getmedusa.diffengine.testengine.meta.DiffEngineTestAdditionLogic;
import io.getmedusa.diffengine.testengine.meta.DiffEngineTestAttrChangeLogic;
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
            //System.out.println(diff);
            html = applyDiff(html, diff);
        }

        Document expectedHTML = Jsoup.parse(newHTML);

        Assertions.assertEquals(prettyPrint(expectedHTML.outerHtml()), prettyPrint(html.outerHtml()),"Rebuilt HTML and expected HTML do not match");
    }

    private String prettyPrint(String dirty) {
        dirty = dirty
                .replace("\r", "")
                .replace("\n", "")
                .replace("\t", "");
        while(dirty.contains("  ")) {
            dirty = dirty.replace("  ", " ");
        }
        dirty = dirty.replace("> <", "><");
        final Document document = Jsoup.parse(dirty);
        document.outputSettings().prettyPrint(true);
        document.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
        return document.body().outerHtml();
    }

    private Document applyDiff(Document html, ServerSideDiff diff) {
        if(diff.isAddition()) {
            return DiffEngineTestAdditionLogic.applyAddition(html, diff);
        } else if(diff.isRemoval()) {
            return DiffEngineTestRemovalLogic.applyRemoval(html, diff);
        } else if(diff.isTextEdit()) {
            return DiffEngineTestEditLogic.applyEdit(html, diff);
        } else if(diff.isAttrChange()) {
            return DiffEngineTestAttrChangeLogic.applyAttrEdit(html, diff);
        }
        return html;
    }
}
