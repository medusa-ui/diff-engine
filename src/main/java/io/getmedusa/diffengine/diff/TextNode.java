package io.getmedusa.diffengine.diff;

import org.joox.JOOX;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import java.util.Objects;

public class TextNode {

    private final String content;
    private final String xpath;
    private final Node previous;
    private final Node next;

    public TextNode(Text text, int index) {
        this.content = text.getNodeValue();
        this.xpath = buildXPath(text, index);
        this.previous = text.getPreviousSibling();
        this.next = text.getNextSibling();
    }

    private String buildXPath(Text text, int index) {
        return JOOX.$(text.getParentNode()).xpath() + "/text()[" + index + "]";
    }

    public String getContent() {
        return content;
    }

    public String getXpath() {
        return xpath;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TextNode textNode)) return false;
        return getContent().equals(textNode.getContent()) && getXpath().equals(textNode.getXpath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getContent(), getXpath());
    }

    public Node getPrevious() {
        return previous;
    }

    public Node getNext() {
        return next;
    }
}
