package com.bhxx.xs_family.beans;

import java.io.Serializable;

/**
 * Created by bh1988034 on 2016/8/16.
 * 请假实体
 */
public class LeaveModel implements Serializable {
    //请假申请id
    private int leId;
    //请假类型名
    private String leTypeName;
    //请假申请描述
    private String leDesction;
    //请假开始时间
    private String leStartTime;
    //请假结束时间
    private String leEndTime;
    //请假凭证图片
    private String lePics;
    //请假申请创建时间
    private String leCreateTime;
    //请假审批状态
    private int leState;
    //请假状态变化时间
    private String leStateTime;
    //请假审批备注
    private String leRemark;
    //老师id
    private int leTeacherId;
    // 班级id
    private int leClassId;
    //园所id
    private int leParkId;

    //非持久话对象
    //班级名
    private String leClassName;
    //发布者
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

    public int getLeId() {
        return leId;
    }

    public void setLeId(int leId) {
        this.leId = leId;
    }

    public String getLeTypeName() {
        return leTypeName;
    }

    public void setLeTypeName(String leTypeName) {
        this.leTypeName = leTypeName;
    }

    public String getLeDesction() {
        return leDesction;
    }

    public void setLeDesction(String leDesction) {
        this.leDesction = leDesction;
    }

    public String getLeStartTime() {
        return leStartTime;
    }

    public void setLeStartTime(String leStartTime) {
        this.leStartTime = leStartTime;
    }

    public String getLeEndTime() {
        return leEndTime;
    }

    public void setLeEndTime(String leEndTime) {
        this.leEndTime = leEndTime;
    }

    public String getLePics() {
        return lePics;
    }

    public void setLePics(String lePics) {
        this.lePics = lePics;
    }

    public String getLeCreateTime() {
        return leCreateTime;
    }

    public void setLeCreateTime(String leCreateTime) {
        this.leCreateTime = leCreateTime;
    }

    public int getLeState() {
        return leState;
    }

    public void setLeState(int leState) {
        this.leState = leState;
    }

    public String getLeStateTime() {
        return leStateTime;
    }

    public void setLeStateTime(String leStateTime) {
        this.leStateTime = leStateTime;
    }

    public String getLeRemark() {
        return leRemark;
    }

    public void setLeRemark(String leRemark) {
        this.leRemark = leRemark;
    }

    public int getLeTeacherId() {
        return leTeacherId;
    }

    public void setLeTeacherId(int leTeacherId) {
        this.leTeacherId = leTeacherId;
    }

    public int getLeClassId() {
        return leClassId;
    }

    public void setLeClassId(int leClassId) {
        this.leClassId = leClassId;
    }

    public int getLeParkId() {
        return leParkId;
    }

    public void setLeParkId(int leParkId) {
        this.leParkId = leParkId;
    }

    public String getLeClassName() {
        return leClassName;
    }

    public void setLeClassName(String leClassName) {
        this.leClassName = leClassName;
    }
}
