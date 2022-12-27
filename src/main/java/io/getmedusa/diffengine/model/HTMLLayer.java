package io.getmedusa.diffengine.model;

import org.joox.JOOX;
import org.joox.Match;
import org.w3c.dom.Node;

import java.util.Objects;

public class HTMLLayer {

    private final String content;
    private final String xpath;

    private final String parentXpath;

    public HTMLLayer(Match $child) {
        this.content = $child.toString();
        this.xpath = $child.xpath();
        this.parentXpath = $child.parent().xpath();
    }

    private HTMLLayer(String content, String xpath, String parentXpath) {
        this.content = content;
        this.xpath = xpath;
        this.parentXpath = parentXpath;
    }

    public static HTMLLayer textNode(Node childNode, int index) {
        var parentNode = JOOX.$(childNode.getParentNode());
        return new HTMLLayer(childNode.getNodeValue(), parentNode.xpath() + "/~text@" + index, parentNode.xpath());
    }

    public boolean hasTextNode() {
        return xpath.contains("/~text@");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof HTMLLayer layer)) return false;
        return getXpath().equals(layer.getXpath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getXpath());
    }

    public String getContent() {
        return content;
    }

    public String getXpath() {
        return xpath;
    }

    public String getParentXpath() {
        return parentXpath;
    }

    @Override
    public String toString() {
        return "HTMLLayer{" +
                "xpath='" + xpath +
                ", content='" + content + '\'' +
                ", parentXpath='" + parentXpath + '\'' +
                '}';
    }
}
