package io.getmedusa.diffengine.testengine.meta;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import org.jsoup.nodes.Document;

public class DiffEngineTestRemovalLogic extends DiffEngineTestLogic {
    public static Document applyRemoval(Document html, ServerSideDiff diff) {
        xpath(html, diff.getXpath()).remove();
        return html;
    }
}
