package com.bhxx.xs_family.entity;

/**
 * Created by bh1988034 on 2016/8/23.
 */
public class AlbumCheckOneEntity {
    public static final int ALBUM_NOT_CHECKED = 0;
    public static final int ALBUM_CHECKED = 1;
    private int key;

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public AlbumCheckOneEntity(int key) {
        super();
        this.key = key;
    }
}
