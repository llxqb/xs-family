package com.bhxx.xs_family.entity;

/**
 * 监听全屏播放返回后小界面播放
 */
public class PlayLiveEntity {
    public static final int PLAY = 0;//

    private int key;


    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public PlayLiveEntity(int key) {
        super();
        this.key = key;
    }
}
