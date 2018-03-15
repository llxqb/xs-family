package com.bhxx.xs_family.beans;

import java.io.Serializable;

public class ActModel implements Serializable {
    //活动id
    private int acId;
    //活动标题
    private String acTitle;
    //活动参与人数
    private int acCount;
    //活动剩余参与人数
    private int acSurCount;
    //活动描述
    private String acDesction;
    //活动行程
    private String acTrip;
    //活动报名须知
    private String acNotice;
    //活动主图片
    private String acMainPic;
    //活动图片  多张图片用;分割
    private String acPics;
    //活动结束报名时间
    private String acEndSigeUpTime;
    //活动开始时间
    private String acStartTime;
    //活动结束时间
    private String acEndTime;
    //活动创建时间
    private String acCreateTime;
    //活动类型0班级1园所2平台
    private int acType;
    //活动发布者id
    private int acPublisherId;
    //活动浏览量
    private int acLookCount;
    //活动审批状态0已提交未审批1审批通过2审批不通过
    private int acState;
    //活动审批状态变化时间
    private String acStateTime;
    //审批备注
    private String acRemark;
    //置顶号
    private long acTopNum;
    //班级id
    private int acClassId;
    //园所id
    private int acParkId;
    //非持久化属性
//活动状态0报名中1未开始2进行中3已结束
    private int activityState;
    //发布者
    private UserModel abPublisher;
    private String acClassName;
    private String acParkName;
    private String acPublisherName;

    public int getAcPublisherId() {
        return acPublisherId;
    }

    public String getAcClassName() {
        return acClassName;
    }

    public void setAcClassName(String acClassName) {
        this.acClassName = acClassName;
    }

    public String getAcPublisherName() {
        return acPublisherName;
    }

    public void setAcPublisherName(String acPublisherName) {
        this.acPublisherName = acPublisherName;
    }

    public String getAcParkName() {
        return acParkName;
    }

    public void setAcParkName(String acParkName) {
        this.acParkName = acParkName;
    }

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

    public int getAcId() {
        return acId;
    }

    public void setAcId(int acId) {
        this.acId = acId;
    }

    public String getAcTitle() {
        return acTitle;
    }

    public void setAcTitle(String acTitle) {
        this.acTitle = acTitle;
    }

    public int getAcCount() {
        return acCount;
    }

    public void setAcCount(int acCount) {
        this.acCount = acCount;
    }

    public String getAcDesction() {
        return acDesction;
    }

    public void setAcDesction(String acDesction) {
        this.acDesction = acDesction;
    }

    public int getAcState() {
        return acState;
    }

    public void setAcState(int acState) {
        this.acState = acState;
    }

    public int getAcSurCount() {
        return acSurCount;
    }

    public void setAcSurCount(int acSurCount) {
        this.acSurCount = acSurCount;
    }

    public String getAcPics() {
        return acPics;
    }

    public void setAcPics(String acPics) {
        this.acPics = acPics;
    }

    public int getAcLookCount() {
        return acLookCount;
    }

    public void setAcLookCount(int acLookCount) {
        this.acLookCount = acLookCount;
    }

    public String getAcEndTime() {
        return acEndTime;
    }

    public void setAcEndTime(String acEndTime) {
        this.acEndTime = acEndTime;
    }

    public String getAcStartTime() {
        return acStartTime;
    }

    public void setAcStartTime(String acStartTime) {
        this.acStartTime = acStartTime;
    }

    public String getAcEndSigeUpTime() {
        return acEndSigeUpTime;
    }

    public void setAcEndSigeUpTime(String acEndSigeUpTime) {
        this.acEndSigeUpTime = acEndSigeUpTime;
    }

    public String getAcMainPic() {
        return acMainPic;
    }

    public void setAcMainPic(String acMainPic) {
        this.acMainPic = acMainPic;
    }

    public String getAcCreateTime() {
        return acCreateTime;
    }

    public void setAcCreateTime(String acCreateTime) {
        this.acCreateTime = acCreateTime;
    }

    public int getAcType() {
        return acType;
    }

    public void setAcType(int acType) {
        this.acType = acType;
    }


    public void setAcPublisherId(int acPublisherId) {
        this.acPublisherId = acPublisherId;
    }

    public String getAcStateTime() {
        return acStateTime;
    }

    public void setAcStateTime(String acStateTime) {
        this.acStateTime = acStateTime;
    }

    public long getAcTopNum() {
        return acTopNum;
    }

    public void setAcTopNum(long acTopNum) {
        this.acTopNum = acTopNum;
    }

    public String getAcRemark() {
        return acRemark;
    }

    public void setAcRemark(String acRemark) {
        this.acRemark = acRemark;
    }

    public int getAcClassId() {
        return acClassId;
    }

    public void setAcClassId(int acClassId) {
        this.acClassId = acClassId;
    }

    public int getAcParkId() {
        return acParkId;
    }

    public void setAcParkId(int acParkId) {
        this.acParkId = acParkId;
    }

    public int getActivityState() {
        return activityState;
    }

    public void setActivityState(int activityState) {
        this.activityState = activityState;
    }


    public String getAcTrip() {
        return acTrip;
    }

    public void setAcTrip(String acTrip) {
        this.acTrip = acTrip;
    }

    public String getAcNotice() {
        return acNotice;
    }

    public void setAcNotice(String acNotice) {
        this.acNotice = acNotice;
    }
}
