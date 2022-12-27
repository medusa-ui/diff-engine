package io.getmedusa.diffengine.testengine.meta;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

public class DiffEngineTestEditLogic extends DiffEngineTestLogic {

    public static Document applyEdit(Document html, ServerSideDiff diff) {
        final String[] splitXpath = diff.getXpath().split("/~text@");
        final String parentXPath = splitXpath[0];
        final int textNodeIndex = Integer.parseInt(splitXpath[1]);

        final Node parent = xpath(html, parentXPath);
        final TextNode node = findTextNode(parent.childNodes(), textNodeIndex);
        node.text(diff.getContent());
        return html;
    }
}
