package io.getmedusa.diffengine.model;

import io.getmedusa.diffengine.diff.TextNode;
import org.joox.JOOX;
import org.joox.Match;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.LinkedList;
import java.util.Objects;

public class HTMLLayer {

    private final String content;
    private final String xpath;

    private final String parentXpath;
    private final LinkedList<TextNode> textNodes = new LinkedList<>();

    public HTMLLayer(Match $child) {
        this.content = $child.toString();
        this.xpath = $child.xpath();
        this.parentXpath = $child.parent().xpath();
        determineIfHasTextNodes($child);
    }

    private HTMLLayer(String content, String xpath, String parentXpath) {
        this.content = content;
        this.xpath = xpath;
        this.parentXpath = parentXpath;
    }

    private boolean determineIfHasTextNodes(Match match) {
        int textIndex = 0;
        for (var element : match) {
            final NodeList childNodes = element.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                var childNode = childNodes.item(i);
                if ("#text".equals(childNode.getNodeName())) {
                    textNodes.add(new TextNode((Text) childNode, textIndex++));
                }
            }
        }
        return false;
    }

    public LinkedList<TextNode> getTextNodes() {
        return textNodes;
    }

    public boolean hasTextNode() {
        return !textNodes.isEmpty();
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
                "xpath='" + xpath + '\'' +
                "content='" + content + '\'' +
                ", parentXpath='" + parentXpath + '\'' +
                '}';
    }

    public HTMLLayer cloneAndPruneContentIntoTagOnly() {
        //I do not want additions to add deeper child nodes
        final Match match = JOOX.$(content);
        String newContent = "<" + match.tag() + "></" + match.tag() + ">";
        return new HTMLLayer(newContent, xpath, parentXpath);
    }
}
