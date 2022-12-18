package io.getmedusa.diffengine.testengine.meta;

import io.getmedusa.diffengine.diff.ServerSideDiff;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;

public class DiffEngineTestAdditionLogic {
    public static Document applyAddition(Document html, ServerSideDiff diff) {
        if(diff.getBefore() != null) {
            Element matchingBeforeElement = xpath(html, diff.getBefore());
            matchingBeforeElement.before(diff.getContent());
        } else if(diff.getAfter() != null) {
            Element matchingAfterElement = xpath(html, diff.getAfter());
            matchingAfterElement.after(diff.getContent());
        } else if(diff.getIn() != null) {
            Element matchingParentElement = xpath(html, diff.getIn());
            matchingParentElement.append(diff.getContent());
        }
        return html;
    }

    protected static Element xpath(Document html, String xpath) {
        if(!xpath.startsWith("/html[1]/body[1]")) {
            xpath = "/html[1]/body[1]" + xpath;
        }
        Elements elements = html.selectXpath(xpath);
        Assertions.assertEquals(1, elements.size(), "Expected to match a single element for '" + xpath + "'");
        return elements.get(0);
    }
}
