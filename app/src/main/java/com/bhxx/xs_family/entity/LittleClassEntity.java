package com.bhxx.xs_family.entity;

public class LittleClassEntity {
    public static final int CANCEL_COLLECT = 0;
    public static final int COLLECT = 1;
    private int littleClassId;
    private int key;

    public LittleClassEntity(int littleClassId, int key) {
        super();
        this.key = key;
        this.littleClassId = littleClassId;
    }

    public int getLittleClassId() {
        return littleClassId;
    }

    public void setLittleClassId(int littleClassId) {
        this.littleClassId = littleClassId;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }
}
