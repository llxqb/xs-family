package com.bhxx.xs_family.beans;

import java.io.Serializable;

public class UserModel implements Serializable {
    //用户id
    private int uId;
    //用户名
    private String uName;
    //用户手机号
    private String uMobile;
    //用户登入密码
    private String uPassWord;
    //用户头像
    private String uHeadPic;
    //用户出生日期
    private String uBrithday;
    //用户所在城市
    private String uCity;
    //用户性别0女1男
    private int uSex;
    //用户角色0园长1老师2家长
    private int uRole;
    //用户职位
    private String uPosition;
    //园所id
    private int uParkId;
    //班级id
    private int uClassId;
    //用户注册时间
    private String uRegistTime;
    //用户备注
    private String uRemark;
    //家长所属学生
    private int uStudentId;
    //用户年龄
    private int uAge;
    private int uIsDelete;
    //园所名
    private String uParkName;
    //班级名
    private String uClassName;
    //家长关联的学生信息
    private StudentModel appStudent;
    private String uRongYunTonken;

//    private String sysCode;//海康sysCode，加载监听模块
    private String mId;//海康监控id用来获取播放信息
    private String mtitle;//海康监控标题

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getMtitle() {
        return mtitle;
    }

    public void setMtitle(String mtitle) {
        this.mtitle = mtitle;
    }

    public String getuRongYunTonken() {
        return uRongYunTonken;
    }

    public void setuRongYunTonken(String uRongYunTonken) {
        this.uRongYunTonken = uRongYunTonken;
    }

    public int getuIsDelete() {
        return uIsDelete;
    }

    public String getuParkName() {
        return uParkName;
    }

    public void setuParkName(String uParkName) {
        this.uParkName = uParkName;
    }

    public String getuClassName() {
        return uClassName;
    }

    public void setuClassName(String uClassName) {
        this.uClassName = uClassName;
    }

    public void setuIsDelete(int uIsDelete) {
        this.uIsDelete = uIsDelete;
    }

    public StudentModel getAppStudent() {
        return appStudent;
    }

    public void setAppStudent(StudentModel appStudent) {
        this.appStudent = appStudent;
    }

    public int getuId() {
        return uId;
    }

    public void setuId(int uId) {
        this.uId = uId;
    }

    public String getuName() {
        return uName;
    }

    public void setuName(String uName) {
        this.uName = uName;
    }

    public String getuMobile() {
        return uMobile;
    }

    public void setuMobile(String uMobile) {
        this.uMobile = uMobile;
    }

    public String getuPassWord() {
        return uPassWord;
    }

    public void setuPassWord(String uPassWord) {
        this.uPassWord = uPassWord;
    }

    public String getuBrithday() {
        return uBrithday;
    }

    public void setuBrithday(String uBrithday) {
        this.uBrithday = uBrithday;
    }

    public String getuHeadPic() {
        return uHeadPic;
    }

    public void setuHeadPic(String uHeadPic) {
        this.uHeadPic = uHeadPic;
    }

    public int getuRole() {
        return uRole;
    }

    public void setuRole(int uRole) {
        this.uRole = uRole;
    }

    public String getuPosition() {
        return uPosition;
    }

    public void setuPosition(String uPosition) {
        this.uPosition = uPosition;
    }

    public int getuSex() {
        return uSex;
    }

    public void setuSex(int uSex) {
        this.uSex = uSex;
    }

    public String getuCity() {
        return uCity;
    }

    public void setuCity(String uCity) {
        this.uCity = uCity;
    }

    public int getuClassId() {
        return uClassId;
    }

    public void setuClassId(int uClassId) {
        this.uClassId = uClassId;
    }

    public String getuRegistTime() {
        return uRegistTime;
    }

    public void setuRegistTime(String uRegistTime) {
        this.uRegistTime = uRegistTime;
    }

    public String getuRemark() {
        return uRemark;
    }

    public void setuRemark(String uRemark) {
        this.uRemark = uRemark;
    }

    public int getuStudentId() {
        return uStudentId;
    }

    public void setuStudentId(int uStudentId) {
        this.uStudentId = uStudentId;
    }

    public int getuAge() {
        return uAge;
    }

    public void setuAge(int uAge) {
        this.uAge = uAge;
    }

    public int getuParkId() {
        return uParkId;
    }

    public void setuParkId(int uParkId) {
        this.uParkId = uParkId;
    }
}
