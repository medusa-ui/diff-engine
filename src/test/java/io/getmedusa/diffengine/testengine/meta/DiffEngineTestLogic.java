package io.getmedusa.diffengine.testengine.meta;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;

public abstract class DiffEngineTestLogic {

    protected static Node xpath(Document html, String xpath) {
        if(!xpath.startsWith("/html[1]/body[1]")) {
            xpath = "/html[1]/body[1]" + xpath;
        }
        xpath = xpath.replace("/body[1]/body[1]", "/body[1]");
        Elements elements = html.selectXpath(xpath);

        if(elements.isEmpty() && xpath.contains("/text()[")) {
            String[] xpathSplit = xpath.split("/text\\(\\)");
            var element = html.selectXpath(xpathSplit[0]).get(0);
            final int expectedIndex = Integer.parseInt(xpathSplit[1].substring(1, xpathSplit[1].length() - 1));
            int currentIndex = 0;
            for(var child : element.childNodes()) {
                if(child.nodeName().equals("#text") && currentIndex++ == expectedIndex) {
                    return child;
                }
            }
        }

        Assertions.assertEquals(1, elements.size(), "Expected to match a single element for '" + xpath + "'");
        return elements.get(0);
    }

}
