package com.bhxx.xs_family.beans;

import java.io.Serializable;

/**
 * Created by bh1988034 on 2016/8/16.
 * 物品申领实体
 */
public class TeachingtoolModel implements Serializable {
    //教具申请id
    private int ttId;
    //教具申请数量
    private int ttCount;
    //教具申请时间
    private String ttCreateTime;
    //教具申请描述
    private String ttDesction;
    //教具申请图片
    private String ttPics;
    //审批备注
    private String ttRemark;
    //审批状态0已提交未审批1审批通过2审批不通过
    private int ttState;
    //审批状态变化时间
    private String ttStateTime;
    //申请人id
    private int ttTeacherId;
    //班級id
    private int ttClassId;
    //園所id
    private int ttParkId;
    //  班級名
    private String ttClassName;
    //申請人
//    private AppUser appUser=new AppUser();
    //申领类别
    private String ttType;

    //申领人
    private UserModel appUser;

    private int checked;

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public UserModel getAppUser() {
        return appUser;
    }

    public void setAppUser(UserModel appUser) {
        this.appUser = appUser;
    }

    public int getTtId() {
        return ttId;
    }

    public void setTtId(int ttId) {
        this.ttId = ttId;
    }

    public int getTtCount() {
        return ttCount;
    }

    public void setTtCount(int ttCount) {
        this.ttCount = ttCount;
    }

    public String getTtCreateTime() {
        return ttCreateTime;
    }

    public void setTtCreateTime(String ttCreateTime) {
        this.ttCreateTime = ttCreateTime;
    }

    public String getTtDesction() {
        return ttDesction;
    }

    public void setTtDesction(String ttDesction) {
        this.ttDesction = ttDesction;
    }

    public String getTtPics() {
        return ttPics;
    }

    public void setTtPics(String ttPics) {
        this.ttPics = ttPics;
    }

    public String getTtRemark() {
        return ttRemark;
    }

    public void setTtRemark(String ttRemark) {
        this.ttRemark = ttRemark;
    }

    public int getTtState() {
        return ttState;
    }

    public void setTtState(int ttState) {
        this.ttState = ttState;
    }

    public String getTtStateTime() {
        return ttStateTime;
    }

    public void setTtStateTime(String ttStateTime) {
        this.ttStateTime = ttStateTime;
    }

    public int getTtTeacherId() {
        return ttTeacherId;
    }

    public void setTtTeacherId(int ttTeacherId) {
        this.ttTeacherId = ttTeacherId;
    }

    public int getTtClassId() {
        return ttClassId;
    }

    public void setTtClassId(int ttClassId) {
        this.ttClassId = ttClassId;
    }

    public int getTtParkId() {
        return ttParkId;
    }

    public void setTtParkId(int ttParkId) {
        this.ttParkId = ttParkId;
    }

    public String getTtClassName() {
        return ttClassName;
    }

    public void setTtClassName(String ttClassName) {
        this.ttClassName = ttClassName;
    }


    public String getTtType() {
        return ttType;
    }

    public void setTtType(String ttType) {
        this.ttType = ttType;
    }
}
