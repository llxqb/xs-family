package com.bhxx.xs_family.entity;

/**
 * Created by Administrator on 2016/9/2.
 */
public class UploadPicEntity {
    public static final int UPLOAD_PARENT = 0;//家长
    public static final int UPLOAD_KING = 1;//院长头像
    public static final int UPLOAD_KING_NAME = 2;//院长姓名
    public static final int UPLOAD_PARENT_NAME = 3;//家长

    private int key;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public UploadPicEntity(int key) {
        super();
        this.key = key;
    }
}
