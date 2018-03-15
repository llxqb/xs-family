package com.bhxx.xs_family.beans;

import java.io.Serializable;

public class AlbumModel implements Serializable {
    //相册id
    private int abId;
    //相册创建时间
    private String abCraeteTime;
    //相册描述
    private String abDesction;
    //相册浏览人数
    private int abLookCount;
    //相册图片
    private String abPics;
    //相册发布者id
    private int abPublisherId;
    //审批备注
    private String abRemark;
    //审批状态0已提交未审批1审批通过2审批不通过
    private int abState;
    //审批状态变化时间
    private String abStateTime;
    //相册类型0班级1院所2平台
    private int abType;
    //申请删除0正常1申请删除
    private int abDelete;
    //园所id
    private int abParkId;
    //班级id
    private int abClassId;
    //班级名
    private String abClassName;
    //院所名
    private String abParkName;


    //非持久参数
    //是否收藏0否1是
    private String abIsCollect;
    //收藏人数
    private int abCollectCount;
    //是否点赞0否1是
    private int abIsClick;
    //点赞数
    private int abClickCount;
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

    public int getAbClickCount() {
        return abClickCount;
    }

    public void setAbClickCount(int abClickCount) {
        this.abClickCount = abClickCount;
    }

    public int getAbIsClick() {
        return abIsClick;
    }

    public void setAbIsClick(int abIsClick) {
        this.abIsClick = abIsClick;
    }

    public int getAbCollectCount() {
        return abCollectCount;
    }

    public void setAbCollectCount(int abCollectCount) {
        this.abCollectCount = abCollectCount;
    }

    public int getAbClassId() {
        return abClassId;
    }

    public void setAbClassId(int abClassId) {
        this.abClassId = abClassId;
    }

    public String getAbStateTime() {
        return abStateTime;
    }

    public void setAbStateTime(String abStateTime) {
        this.abStateTime = abStateTime;
    }

    public int getAbId() {
        return abId;
    }

    public void setAbId(int abId) {
        this.abId = abId;
    }

    public int getAbPublisherId() {
        return abPublisherId;
    }

    public void setAbPublisherId(int abPublisherId) {
        this.abPublisherId = abPublisherId;
    }

    public String getAbRemark() {
        return abRemark;
    }

    public void setAbRemark(String abRemark) {
        this.abRemark = abRemark;
    }

    public String getAbDesction() {
        return abDesction;
    }

    public void setAbDesction(String abDesction) {
        this.abDesction = abDesction;
    }

    public String getAbCraeteTime() {
        return abCraeteTime;
    }

    public void setAbCraeteTime(String abCraeteTime) {
        this.abCraeteTime = abCraeteTime;
    }

    public int getAbLookCount() {
        return abLookCount;
    }

    public void setAbLookCount(int abLookCount) {
        this.abLookCount = abLookCount;
    }

    public String getAbPics() {
        return abPics;
    }

    public void setAbPics(String abPics) {
        this.abPics = abPics;
    }

    public int getAbState() {
        return abState;
    }

    public void setAbState(int abState) {
        this.abState = abState;
    }

    public int getAbType() {
        return abType;
    }

    public void setAbType(int abType) {
        this.abType = abType;
    }

    public int getAbDelete() {
        return abDelete;
    }

    public void setAbDelete(int abDelete) {
        this.abDelete = abDelete;
    }

    public int getAbParkId() {
        return abParkId;
    }

    public void setAbParkId(int abParkId) {
        this.abParkId = abParkId;
    }

    public String getAbIsCollect() {
        return abIsCollect;
    }

    public void setAbIsCollect(String abIsCollect) {
        this.abIsCollect = abIsCollect;
    }

    public String getAbClassName() {
        return abClassName;
    }

    public void setAbClassName(String abClassName) {
        this.abClassName = abClassName;
    }

    public String getAbParkName() {
        return abParkName;
    }

    public void setAbParkName(String abParkName) {
        this.abParkName = abParkName;
    }
}
