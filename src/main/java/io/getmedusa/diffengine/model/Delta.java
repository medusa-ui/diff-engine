package io.getmedusa.diffengine.model;

public class Delta {

    private HTMLLayer layer;
    private Integer position;
    private boolean isInsert = false;
    private boolean isDelete = false;

    public static Delta deletionDelta(HTMLLayer layer, int index) {
        Delta delta = new Delta();
        delta.isDelete = true;
        delta.layer = layer;
        delta.position = index;
        return delta;
    }

    public static Delta insertionDelta(HTMLLayer layer, int index) {
        Delta delta = new Delta();
        delta.isInsert = true;
        delta.layer = layer;
        delta.position = index;
        return delta;
    }

    public boolean isInsert() {
        return isInsert;
    }

    public boolean isDelete() {
        return isDelete;
    }

    public Integer getPosition() {
        return position;
    }

    public HTMLLayer getLayer() {
        return layer;
    }
}
