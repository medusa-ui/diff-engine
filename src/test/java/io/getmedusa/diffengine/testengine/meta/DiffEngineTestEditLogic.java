package io.getmedusa.diffengine.testengine.meta;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import org.jsoup.nodes.Document;

public class DiffEngineTestEditLogic extends DiffEngineTestLogic {

    public static Document applyEdit(Document html, ServerSideDiff diff) {
        //xpath(html, diff.getXpath()).replaceWith(Jsoup.parse(diff.getContent()).body().child(0));
        xpath(html, diff.getXpath()).text(diff.getContent());
        return html;
    }
}
