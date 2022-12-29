package io.getmedusa.diffengine.model;

import io.getmedusa.diffengine.model.ServerSideDiff;

public abstract class AbstractDiff {

    protected String content;
    protected final DiffType type;

    protected AbstractDiff(DiffType type) {
        this.type = type;
    }

    protected AbstractDiff(String content, DiffType type) {
        this.content = content;
        this.type = type;
    }

    public static ServerSideDiff buildNewRedirect(String url) {
        return new ServerSideDiff(url, DiffType.REDIRECT);
    }

    public static ServerSideDiff buildNewJSFunction(String jsFunctionCall) {
        return new ServerSideDiff(jsFunctionCall, DiffType.JS_FUNCTION);
    }

    public static ServerSideDiff buildNewLoading(String value) {
        return new ServerSideDiff(value, DiffType.LOADING);
    }

    public String getType() {
        return type.name();
    }

    public enum DiffType {
        //main structure
        ADDITION,
        REMOVAL,
        EDIT,

        //extras
        REDIRECT, JS_FUNCTION, LOADING

        /*,
        ATTR_CHANGE,
        TAG_CHANGE,
        SEQUENCE_CHANGE*/
    }
}
