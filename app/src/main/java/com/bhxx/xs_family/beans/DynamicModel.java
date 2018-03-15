package com.bhxx.xs_family.beans;

import java.io.Serializable;

public class DynamicModel implements Serializable{
    //动态id
    private int dcId;
    //动态标题
    private String dcTitle;
    //动态描述
    private String dcDesction;
    //动态浏览人数
    private int dcLookCount;
    //动态主图
    private String dcMainPic;
    //活动类型0班级1园所2平台
    private int dcType;
    //动态图片
    private String dcPics;
    //动态发布者id
    private int dcPublisherId;
    //动态创建时间
    private String dcCreateTime;
    //动态审批备注
    private String dcRemark;
    //审批状态0已提交未审批1审批通过2审批不通过
    private int dcState;
    //审批状态变化时间
    private String dcStateTime;
    //班级id
    private int dcClassId;
    //园所id
    private int dcParkId;
    //班级名
    private String dcClassName;
    //发布者名
    private String dcPublisherName;
    //收藏数
    private int  dcCollectCount;
    //是否收藏 0 没有 1 有
    private int dcIsCollect;

    public int getDcIsCollect() {
        return dcIsCollect;
    }

    public void setDcIsCollect(int dcIsCollect) {
        this.dcIsCollect = dcIsCollect;
    }

    //发布者
    private UserModel abPublisher;

    private int checked;

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public UserModel getAbPublisher() {
        return abPublisher;
    }

    public void setAbPublisher(UserModel abPublisher) {
        this.abPublisher = abPublisher;
    }

    public String getDcDesction() {
        return dcDesction;
    }

    public void setDcDesction(String dcDesction) {
        this.dcDesction = dcDesction;
    }

    public String getDcTitle() {
        return dcTitle;
    }

    public void setDcTitle(String dcTitle) {
        this.dcTitle = dcTitle;
    }

    public int getDcId() {
        return dcId;
    }

    public void setDcId(int dcId) {
        this.dcId = dcId;
    }

    public int getDcLookCount() {
        return dcLookCount;
    }

    public void setDcLookCount(int dcLookCount) {
        this.dcLookCount = dcLookCount;
    }

    public int getDcType() {
        return dcType;
    }

    public void setDcType(int dcType) {
        this.dcType = dcType;
    }

    public String getDcCreateTime() {
        return dcCreateTime;
    }

    public void setDcCreateTime(String dcCreateTime) {
        this.dcCreateTime = dcCreateTime;
    }

    public int getDcState() {
        return dcState;
    }

    public void setDcState(int dcState) {
        this.dcState = dcState;
    }

    public int getDcParkId() {
        return dcParkId;
    }

    public void setDcParkId(int dcParkId) {
        this.dcParkId = dcParkId;
    }

    public String getDcPublisherName() {
        return dcPublisherName;
    }

    public void setDcPublisherName(String dcPublisherName) {
        this.dcPublisherName = dcPublisherName;
    }

    public int getDcCollectCount() {
        return dcCollectCount;
    }

    public void setDcCollectCount(int dcCollectCount) {
        this.dcCollectCount = dcCollectCount;
    }

    public String getDcClassName() {
        return dcClassName;
    }

    public void setDcClassName(String dcClassName) {
        this.dcClassName = dcClassName;
    }

    public int getDcClassId() {
        return dcClassId;
    }

    public void setDcClassId(int dcClassId) {
        this.dcClassId = dcClassId;
    }

    public String getDcStateTime() {
        return dcStateTime;
    }

    public void setDcStateTime(String dcStateTime) {
        this.dcStateTime = dcStateTime;
    }

    public String getDcRemark() {
        return dcRemark;
    }

    public void setDcRemark(String dcRemark) {
        this.dcRemark = dcRemark;
    }

    public int getDcPublisherId() {
        return dcPublisherId;
    }

    public void setDcPublisherId(int dcPublisherId) {
        this.dcPublisherId = dcPublisherId;
    }

    public String getDcPics() {
        return dcPics;
    }

    public void setDcPics(String dcPics) {
        this.dcPics = dcPics;
    }

    public String getDcMainPic() {
        return dcMainPic;
    }

    public void setDcMainPic(String dcMainPic) {
        this.dcMainPic = dcMainPic;
    }
}
