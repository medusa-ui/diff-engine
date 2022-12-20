package io.getmedusa.diffengine.testengine.meta;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;

public abstract class DiffEngineTestLogic {

    protected static Element xpath(Document html, String xpath) {
        if(!xpath.startsWith("/html[1]/body[1]")) {
            xpath = "/html[1]/body[1]" + xpath;
        }
        Elements elements = html.selectXpath(xpath);
        Assertions.assertEquals(1, elements.size(), "Expected to match a single element for '" + xpath + "'");
        return elements.get(0);
    }

}
