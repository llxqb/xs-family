package com.bhxx.xs_family.beans;

import java.io.Serializable;

public class StudentModel implements Serializable {
    // 学生id
    private int stId;
    // 学生头像
    private String stHeadPic;
    // 注册id
    private String stRegistTime;
    // 备注
    private String stRemark;
    // 出生日期
    private String stBrithday;
    // 名字
    private String stName;
    // 性别
    private int stSex;
    // 所在班级id
    private int stClassesId;
    private String stParkName;
    private String stClassName;
    private int stAge;

    public String getStParkName() {
        return stParkName;
    }

    public void setStParkName(String stParkName) {
        this.stParkName = stParkName;
    }

    public int getStAge() {
        return stAge;
    }

    public void setStAge(int stAge) {
        this.stAge = stAge;
    }

//    public String getStClassName() {
//        return stClassName;
//    }
//
//    public void setStClassName(String stClassName) {
//        this.stClassName = stClassName;
//    }

    //班级对象
    private ClassModel appClass;

    public ClassModel getAppClass() {
        return appClass;
    }

    public void setAppClass(ClassModel appClass) {
        this.appClass = appClass;
    }

    public int getStId() {
        return stId;
    }

    public void setStId(int stId) {
        this.stId = stId;
    }

    public String getStHeadPic() {
        return stHeadPic;
    }

    public void setStHeadPic(String stHeadPic) {
        this.stHeadPic = stHeadPic;
    }

    public String getStRegistTime() {
        return stRegistTime;
    }

    public void setStRegistTime(String stRegistTime) {
        this.stRegistTime = stRegistTime;
    }

    public String getStRemark() {
        return stRemark;
    }

    public void setStRemark(String stRemark) {
        this.stRemark = stRemark;
    }

    public String getStBrithday() {
        return stBrithday;
    }

    public void setStBrithday(String stBrithday) {
        this.stBrithday = stBrithday;
    }

    public String getStName() {
        return stName;
    }

    public void setStName(String stName) {
        this.stName = stName;
    }

    public int getStSex() {
        return stSex;
    }

    public void setStSex(int stSex) {
        this.stSex = stSex;
    }

    public int getStClassesId() {
        return stClassesId;
    }

    public void setStClassesId(int stClassesId) {
        this.stClassesId = stClassesId;
    }
}
