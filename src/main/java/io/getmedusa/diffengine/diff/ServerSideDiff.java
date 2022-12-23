package io.getmedusa.diffengine.diff;

import io.getmedusa.diffengine.model.HTMLLayer;
import org.joox.JOOX;
import org.joox.Match;

public class ServerSideDiff {

    //addition
    private String content;
    private String before;
    private String after;
    private String in;

    //removal
    private String xpath;

    private final DiffType type;

    public ServerSideDiff(DiffType type) {
        this.type = type;
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

    public boolean isAddition() {
        return this.type.equals(DiffType.ADDITION);
    }

    public boolean isRemoval()  {
        return this.type.equals(DiffType.REMOVAL);
    }

    public boolean isEdit() {
        return this.type.equals(DiffType.EDIT);
    }

    public static ServerSideDiff buildEdit(HTMLLayer newLayer) {
        ServerSideDiff diff = new ServerSideDiff(DiffType.EDIT);
        diff.setContent(JOOX.$(newLayer.getContent()).text());
        diff.setXpath(newLayer.getXpath());
        return diff;
    }

    public static ServerSideDiff buildNewAfterDiff(HTMLLayer newLayer, HTMLLayer addAfterThisLayer) {
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.ADDITION);
        diff.setContent(additionContentFilter(newLayer.getContent()));
        diff.setAfter(addAfterThisLayer.getXpath());
        return diff;
    }

    public static ServerSideDiff buildNewBeforeDiff(HTMLLayer newLayer, HTMLLayer addBeforeThisLayer) {
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.ADDITION);
        diff.setContent(additionContentFilter(newLayer.getContent()));
        diff.setBefore(addBeforeThisLayer.getXpath());
        return diff;
    }

    public static ServerSideDiff buildInDiff(HTMLLayer layer) {
        ServerSideDiff diff = new ServerSideDiff(ServerSideDiff.DiffType.ADDITION);
        if(layer.getParentXpath() != null) {
            diff.setContent(additionContentFilter(layer.getContent()));
            diff.setIn(layer.getParentXpath());
        } else {
            diff.setContent(additionContentFilter(layer.getContent()));
            diff.setIn(layer.getXpath());
        }

        return diff;
    }

    private static String additionContentFilter(String content) {
        //I do not want additions to add deeper child nodes
        final Match match = JOOX.$(content);
        if(match.children().isNotEmpty()) {
            match.children().remove();
        }
        return match.toString();
    }

    public static ServerSideDiff buildRemoval(HTMLLayer layer) {
        ServerSideDiff diff = new ServerSideDiff(DiffType.REMOVAL);
        diff.setXpath(layer.getXpath());
        return diff;
    }

    public enum DiffType {
        ADDITION,
        REMOVAL,
        EDIT
        /*,
        ATTR_CHANGE,
        TAG_CHANGE,
        REDIRECT,
        JS_FUNCTION,
        LOADING,
        SEQUENCE_CHANGE*/
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
