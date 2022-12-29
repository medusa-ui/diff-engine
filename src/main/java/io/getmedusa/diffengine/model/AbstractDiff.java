package io.getmedusa.diffengine.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import static io.getmedusa.diffengine.model.AbstractDiff.DiffType.*;

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


    @JsonIgnore
    public boolean isAddition() {
        return this.type.equals(ADDITION);
    }

    @JsonIgnore
    public boolean isRemoval()  {
        return this.type.equals(REMOVAL);
    }

    @JsonIgnore
    public boolean isAttrChange()   {
        return this.type.equals(ATTR_CHANGE);
    }

    @JsonIgnore
    public boolean isTextEdit()   {
        return this.type.equals(TEXT_EDIT);
    }

    public enum DiffType {
        //main structure
        ADDITION,
        REMOVAL,
        TEXT_EDIT,
        ATTR_CHANGE,

        //extras
        REDIRECT, JS_FUNCTION, LOADING
    }
}
