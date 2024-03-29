package io.getmedusa.diffengine.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.getmedusa.diffengine.model.meta.TextNode;

import static io.getmedusa.diffengine.model.AbstractDiff.DiffType.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ServerSideDiff extends AbstractDiff {

    private String before;
    private String after;
    private String in;

    private String xpath;

    private String attributeKey;
    private String attributeValue;

    public ServerSideDiff(DiffType type) {
        super(type);
    }

    public ServerSideDiff(String content, DiffType type) {
        super(content, type);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getAfter() {
        return after;
    }

    public void setAfter(String after) {
        this.after = after;
    }

    public String getIn() {
        return in;
    }

    public void setIn(String in) {
        this.in = in;
    }

    public String getXpath() {
        return xpath;
    }

    public void setXpath(String xpath) {
        this.xpath = xpath;
    }

    public String getAttributeKey() {
        return attributeKey;
    }

    public void setAttributeKey(String attributeKey) {
        this.attributeKey = attributeKey;
    }

    public String getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(String attributeValue) {
        this.attributeValue = attributeValue;
    }

    public static ServerSideDiff buildAttrChange(String xpath, String key, String value) {
        ServerSideDiff diff = new ServerSideDiff(ATTR_CHANGE);
        diff.setXpath(xpath);
        diff.setAttributeKey(key);
        diff.setAttributeValue(value);
        return diff;
    }

    public static ServerSideDiff buildEdit(TextNode e) {
        ServerSideDiff diff = new ServerSideDiff(TEXT_EDIT);
        diff.setContent(e.getContent());
        diff.setXpath(e.getXpath());
        return diff;
    }


    public static ServerSideDiff buildNewAfterDiff(HTMLLayer newLayer, HTMLLayer addAfterThisLayer) {
        ServerSideDiff diff = new ServerSideDiff(ADDITION);
        diff.setContent(newLayer.getContent());
        diff.setAfter(addAfterThisLayer.getXpath());
        return diff;
    }

    public static ServerSideDiff buildNewAfterDiff(TextNode text, String afterXPATH) {
        ServerSideDiff diff = new ServerSideDiff(ADDITION);
        diff.setContent(text.getContent());
        diff.setAfter(afterXPATH);
        diff.setXpath(text.getXpath());
        return diff;
    }

    public static ServerSideDiff buildNewBeforeDiff(HTMLLayer newLayer, HTMLLayer addBeforeThisLayer) {
        ServerSideDiff diff = new ServerSideDiff(ADDITION);
        diff.setContent(newLayer.getContent());
        diff.setBefore(addBeforeThisLayer.getXpath());
        return diff;
    }

    public static ServerSideDiff buildNewBeforeDiff(TextNode text, String afterXPATH) {
        ServerSideDiff diff = new ServerSideDiff(ADDITION);
        diff.setContent(text.getContent());
        diff.setBefore(afterXPATH);
        diff.setXpath(text.getXpath());
        return diff;
    }

    public static ServerSideDiff buildInDiff(HTMLLayer layer) {
        ServerSideDiff diff = new ServerSideDiff(ADDITION);
        if(layer.getParentXpath() != null) {
            diff.setContent(layer.getContent());
            diff.setIn(layer.getParentXpath());
        } else {
            diff.setContent(layer.getContent());
            diff.setIn(layer.getXpath());
        }

        return diff;
    }

    public static ServerSideDiff buildInDiff(TextNode text, String inXPath) {
        ServerSideDiff diff = new ServerSideDiff(ADDITION);
        diff.setContent(text.getContent());
        diff.setIn(inXPath);
        diff.setXpath(text.getXpath());
        return diff;
    }

    public static ServerSideDiff buildRemoval(HTMLLayer layer) {
        ServerSideDiff diff = new ServerSideDiff(REMOVAL);
        diff.setXpath(layer.getXpath());
        return diff;
    }

    public static ServerSideDiff buildRemoval(TextNode r) {
        ServerSideDiff diff = new ServerSideDiff(REMOVAL);
        diff.setXpath(r.getXpath());
        return diff;
    }

    public static ServerSideDiff buildValidation(String field, String message) {
        ServerSideDiff diff = new ServerSideDiff(VALIDATION);
        diff.setAttributeKey(field);
        diff.setAttributeValue(message);
        return diff;
    }

    @Override
    public String toString() {
        return "ServerSideDiff{" +
                "type=" + type +
                ((xpath != null) ? (", xpath='" + xpath + '\'') : "") +
                ((content != null) ? (", content='" + content.replace("\r\n", "").replace(" ", "").trim() + '\'') : "") +
                ((before != null) ? (", before='" + before + '\'') : "") +
                ((after != null) ? (", after='" + after + '\'') : "") +
                ((in != null) ? (", in='" + in + '\'') : "") +
                '}';
    }



}
