package com.bhxx.xs_family.entity;

public class DynamicCollectEntity {
    public static final int CANCEL_COLLECT = 0;
    public static final int COLLECT = 1;
    private int dynamicId;
    private int key;

    public DynamicCollectEntity(int dynamicId, int key) {
        super();
        this.key = key;
        this.dynamicId = dynamicId;
    }

    public int getDynamicId() {
        return dynamicId;
    }

    public void setDynamicId(int dynamicId) {
        this.dynamicId = dynamicId;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
