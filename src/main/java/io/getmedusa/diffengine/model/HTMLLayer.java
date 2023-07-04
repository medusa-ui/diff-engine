package io.getmedusa.diffengine.model;

import io.getmedusa.diffengine.model.meta.TextNode;
import org.joox.Match;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

public class HTMLLayer {

    private String contentCache;
    private final String xpath;
    private final int position;
    private final String tag;

    private final String parentXpath;
    private final LinkedList<TextNode> textNodes = new LinkedList<>();
    private final Map<String, String> attributes = new HashMap<>();
    private Match match;

    public HTMLLayer(Match $child) {
        this.match = $child;
        this.xpath = $child.xpath();
        this.parentXpath = $child.parent().xpath();
        this.position = determineNodePosition($child);
        this.tag = $child.tag();
        determineIfHasTextNodes($child);
        determineAttributes($child);
    }

    private int determineNodePosition(Match $child) {
        final Match children = $child.parent().children();
        for (int j = 0; j < children.size(); j++) {
            if($child.get(0).equals(children.get(j))){
                return j;
            }
        }
        return 0;
    }

    @Deprecated
    public HTMLLayer(String tag, String content, String xpath, String parentXpath, int position) {
        this.contentCache = content;
        this.xpath = xpath;
        this.parentXpath = parentXpath;
        this.position = position;
        this.tag = tag;
    }

    private void determineIfHasTextNodes(Match match) {
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
    }

    private void determineAttributes(Match $child) {
        for(var elem : $child.get()) {
            var attrNodes = elem.getAttributes();
            for (int i = 0; i < attrNodes.getLength(); i++) {
                var node = attrNodes.item(i);
                attributes.put(node.getNodeName(), node.getNodeValue());
            }
        }
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
        return position == layer.position && getXpath().equals(layer.getXpath());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getXpath(), position);
    }

    public String getContent() {
        if(contentCache == null) {
            this.contentCache = match.toString();
        }
        return contentCache;
    }

    public String getXpath() {
        return xpath;
    }

    public String getParentXpath() {
        return parentXpath;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    @Override
    public String toString() {
        return "HTMLLayer{" +
                "xpath='" + xpath + '\'' +
                "content='" + getContent() + '\'' +
                ", parentXpath='" + parentXpath + '\'' +
                '}';
    }

    public HTMLLayer cloneAndPruneContentIntoTagOnly() {
        //I do not want additions to add deeper child nodes
        String newContent = "<" + tag + "></" + tag + ">";
        return new HTMLLayer(tag, newContent, xpath, parentXpath, position);
    }
}
