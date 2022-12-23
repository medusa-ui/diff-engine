package io.getmedusa.diffengine.model;

import org.joox.Match;
import org.w3c.dom.Element;

import java.util.Objects;

public class HTMLLayer {

    private final String hash;

    private final String content;
    private final String xpath;

    private final String parentXpath;

    public HTMLLayer(Match $child) {
        Element element = $child.get(0);

        String[] xpathSplit = $child.xpath().split("/");
        StringBuilder hashBuilder = new StringBuilder(xpathSplit[xpathSplit.length-2]);
        hashBuilder.append(">");
        hashBuilder.append(xpathSplit[xpathSplit.length-1]);
        for (int i = 0; i < element.getAttributes().getLength(); i++) { //? do we want this?
            var attr = element.getAttributes().item(i);
            hashBuilder.append("/").append(attr.getNodeName()).append(attr.getNodeValue());
        }
        this.hash = hashBuilder.toString();
        //TODO ensure this is unique, but consistent; xpath is not a good choice because that doesn't describe it as the same node

        this.content = $child.toString();
        this.xpath = $child.xpath();
        this.parentXpath = $child.parent().xpath();
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

    public String getParentXpath() {
        return parentXpath;
    }

    @Override
    public String toString() {
        return "HTMLLayer{" +
                "hash='" + hash + '\'' +
                ", xpath='" + xpath + '\'' +
                ", parentXpath='" + parentXpath + '\'' +
                '}';
    }
}
