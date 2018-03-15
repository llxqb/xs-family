package com.bhxx.xs_family.entity;

/**
 * Created by bh1988034 on 2016/8/23.
 * 监听 删除图片动作
 */
public class LookCheckStatusEntity {
    public static final int NOT_CHANGE = 0;
    public static final int CHANGE = 1;
    private int key;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public LookCheckStatusEntity(int key) {
        super();
        this.key = key;
    }
}
