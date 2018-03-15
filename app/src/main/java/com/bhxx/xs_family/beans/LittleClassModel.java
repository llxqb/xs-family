package com.bhxx.xs_family.beans;

import java.io.Serializable;

public class LittleClassModel implements Serializable {
    private int ptId;
    private String ptCreateTime;
    private String ptDesction;
    private int ptLookCount;
    private String ptMainPic;
    private String ptTitle;
    //是否收藏 0 没有 1 有
    private int ptIsCollect;
    private int ptCollectCount;

    public String getPtCreateTime() {
        return ptCreateTime;
    }

    public void setPtCreateTime(String ptCreateTime) {
        this.ptCreateTime = ptCreateTime;
    }

    public int getPtId() {
        return ptId;
    }

    public void setPtId(int ptId) {
        this.ptId = ptId;
    }

    public String getPtDesction() {
        return ptDesction;
    }

    public void setPtDesction(String ptDesction) {
        this.ptDesction = ptDesction;
    }

    public int getPtLookCount() {
        return ptLookCount;
    }

    public void setPtLookCount(int ptLookCount) {
        this.ptLookCount = ptLookCount;
    }

    public String getPtMainPic() {
        return ptMainPic;
    }

    public void setPtMainPic(String ptMainPic) {
        this.ptMainPic = ptMainPic;
    }

    public String getPtTitle() {
        return ptTitle;
    }

    public void setPtTitle(String ptTitle) {
        this.ptTitle = ptTitle;
    }

    public int getPtIsCollect() {
        return ptIsCollect;
    }

    public void setPtIsCollect(int ptIsCollect) {
        this.ptIsCollect = ptIsCollect;
    }

    public int getPtCollectCount() {
        return ptCollectCount;
    }

    public void setPtCollectCount(int ptCollectCount) {
        this.ptCollectCount = ptCollectCount;
    }
}
