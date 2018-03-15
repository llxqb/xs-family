package com.bhxx.xs_family.beans;

import java.io.Serializable;

public class ClassModel implements Serializable {
    //班级id
    private int cId;
    //班级名
    private String clName;
    //班级所在院所id
    private int cParkId;
    //班级备注
    private String cRemark;
    //班级创建时间
    private String cCreateTime;
    //班级类型0小班1中班2大班
    private int clType;
    //监控点id多个id用;分割(用于调用监控设备)
    private String cCameraId;

    public int getcId() {
        return cId;
    }

    public void setcId(int cId) {
        this.cId = cId;
    }

    public String getcCameraId() {
        return cCameraId;
    }

    public void setcCameraId(String cCameraId) {
        this.cCameraId = cCameraId;
    }

    public int getClType() {
        return clType;
    }

    public void setClType(int clType) {
        this.clType = clType;
    }

    public String getcCreateTime() {
        return cCreateTime;
    }

    public void setcCreateTime(String cCreateTime) {
        this.cCreateTime = cCreateTime;
    }

    public String getcRemark() {
        return cRemark;
    }

    public void setcRemark(String cRemark) {
        this.cRemark = cRemark;
    }

    public int getcParkId() {
        return cParkId;
    }

    public void setcParkId(int cParkId) {
        this.cParkId = cParkId;
    }

    public String getClName() {
        return clName;
    }

    public void setClName(String clName) {
        this.clName = clName;
    }
}
