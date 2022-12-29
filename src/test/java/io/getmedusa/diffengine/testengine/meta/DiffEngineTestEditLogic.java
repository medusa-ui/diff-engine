package io.getmedusa.diffengine.testengine.meta;

import io.getmedusa.diffengine.model.ServerSideDiff;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public class DiffEngineTestEditLogic extends DiffEngineTestLogic {

    public static Document applyEdit(Document html, ServerSideDiff diff) {
        final Node xpath = xpath(html, diff.getXpath());
        xpath.replaceWith(Jsoup.parse(diff.getContent()).body().childNode(0));
        return html;
    }
}
