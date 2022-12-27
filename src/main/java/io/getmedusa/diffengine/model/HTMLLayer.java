package io.getmedusa.diffengine.model;

import org.joox.Match;

import java.util.Objects;

public class HTMLLayer {

    private final String content;
    private final String xpath;
    private final boolean hasTextNode;

    private final String parentXpath;

    public HTMLLayer(Match $child) {
        this.content = $child.toString();
        this.xpath = $child.xpath();
        this.parentXpath = $child.parent().xpath();
        this.hasTextNode = $child.children().isEmpty();
    }

    public boolean hasTextNode() {
        return hasTextNode;
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
}
