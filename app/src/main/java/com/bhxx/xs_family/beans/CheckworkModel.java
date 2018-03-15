package com.bhxx.xs_family.beans;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by bh1988034 on 2016/8/16.
 * 考勤实体
 */
public class CheckworkModel implements Serializable {
    //考勤id
    private int cwId;
    //考勤创建时间
    private String cwCreateTime;
    //考勤地址
    private String cwPlace;
    //考勤老师id
    private int cwTeacherId;

    //考勤图片
    private int cwPic;

    public int getCwId() {
        return cwId;
    }

    public void setCwId(int cwId) {
        this.cwId = cwId;
    }

    public String getCwCreateTime() {
        return cwCreateTime;
    }

    public void setCwCreateTime(String cwCreateTime) {
        this.cwCreateTime = cwCreateTime;
    }

    public String getCwPlace() {
        return cwPlace;
    }

    public void setCwPlace(String cwPlace) {
        this.cwPlace = cwPlace;
    }

    public int getCwTeacherId() {
        return cwTeacherId;
    }

    public void setCwTeacherId(int cwTeacherId) {
        this.cwTeacherId = cwTeacherId;
    }

    public int getCwPic() {
        return cwPic;
    }

    public void setCwPic(int cwPic) {
        this.cwPic = cwPic;
    }
}
