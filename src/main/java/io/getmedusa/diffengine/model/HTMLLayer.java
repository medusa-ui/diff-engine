package io.getmedusa.diffengine.model;

import org.joox.JOOX;
import org.w3c.dom.Element;

import java.util.Objects;

public class HTMLLayer {

    private final String hash;

    private final String content;
    private final String xpath;

    public HTMLLayer(Element child) {
        var $child = JOOX.$(child);
        this.hash = child.getNodeName(); //TODO ensure this is unique, but consistent
        this.content = $child.toString();
        this.xpath = $child.xpath();
    }

    public String getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HTMLLayer htmlLayer = (HTMLLayer) o;
        return hash.equals(htmlLayer.hash);
    }

    @Override
    public int hashCode() {
        return Objects.hash(hash);
    }

    public String getContent() {
        return content;
    }

    public String getXpath() {
        return xpath;
    }
}
