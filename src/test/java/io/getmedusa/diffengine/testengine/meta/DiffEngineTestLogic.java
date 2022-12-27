package io.getmedusa.diffengine.testengine.meta;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;

import java.util.List;

public abstract class DiffEngineTestLogic {

    protected static Node xpath(Document html, String xpath) {

        if(xpath.contains("/~text@")) {
            final String[] splitXpath = xpath.split("/~text@");
            final String parentXPath = splitXpath[0];
            final int textNodeIndex = Integer.parseInt(splitXpath[1]);
            final Node parent = xpath(html, parentXPath);
            return findTextNode(parent.childNodes(), textNodeIndex);
        }

        if(!xpath.startsWith("/html[1]/body[1]")) {
            xpath = "/html[1]/body[1]" + xpath;
        }
        xpath = xpath.replace("/body[1]/body[1]", "/body[1]");

        Elements elements = html.selectXpath(xpath);
        Assertions.assertEquals(1, elements.size(), "Expected to match a single element for '" + xpath + "'");
        return elements.get(0);
    }

    protected static TextNode findTextNode(List<Node> childNodes, int textNodeIndex) {
        int currentFoundIndex = 0;
        for(Node node : childNodes) {
            if(node.nodeName().equals("#text")) {
                if(currentFoundIndex++ == textNodeIndex) {
                    return (TextNode) node;
                }
            }
        }
        return null;
    }

}
