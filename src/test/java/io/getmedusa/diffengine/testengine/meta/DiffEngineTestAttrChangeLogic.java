package io.getmedusa.diffengine.testengine.meta;

import io.getmedusa.diffengine.model.ServerSideDiff;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public class DiffEngineTestAttrChangeLogic extends DiffEngineTestLogic {

    public static Document applyAttrEdit(Document html, ServerSideDiff diff) {
        final Node node = xpath(html, diff.getXpath());

        if(diff.getAttributeValue() == null) {
            node.removeAttr(diff.getAttributeKey());
        } else {
            node.attr(diff.getAttributeKey(), diff.getAttributeValue());
        }

        return html;
    }
}
