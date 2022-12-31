package io.getmedusa.diffengine.testengine.meta;

import io.getmedusa.diffengine.model.ServerSideDiff;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;

public class DiffEngineTestRemovalLogic extends DiffEngineTestLogic {
    public static Document applyRemoval(Document html, ServerSideDiff diff) {
        final Node node = xpathWithoutVerify(html, diff.getXpath());
        if(node != null) {
            node.remove();
        } else {
            System.err.printf("DiffEngineTestRemovalLogic.applyRemoval could not find node!\nxpath: '%s' in  \nhtml:\n%s%n", diff.getXpath(), html);
        }
        return html;
    }
}
