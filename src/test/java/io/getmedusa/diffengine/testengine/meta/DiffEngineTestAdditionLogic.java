package io.getmedusa.diffengine.testengine.meta;

import io.getmedusa.diffengine.model.ServerSideDiff;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

public class DiffEngineTestAdditionLogic extends DiffEngineTestLogic {
    public static Document applyAddition(Document html, ServerSideDiff diff) {
        if(diff.getBefore() != null) {
            Node matchingBeforeElement = xpath(html, diff.getBefore());
            matchingBeforeElement.before(diff.getContent());
        } else if(diff.getAfter() != null) {
            Node matchingAfterElement = xpath(html, diff.getAfter());
            matchingAfterElement.after(diff.getContent());
        } else if(diff.getIn() != null) {
            Element matchingParentElement = (Element) xpath(html, diff.getIn());
            matchingParentElement.append(diff.getContent());
        }
        return html;
    }
}
