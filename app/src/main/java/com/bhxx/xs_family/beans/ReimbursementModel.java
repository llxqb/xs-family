package com.bhxx.xs_family.beans;

import java.io.Serializable;

/**
 * Created by bh1988034 on 2016/8/18.
 * 报销费用实体
 */
public class ReimbursementModel implements Serializable{

    //报销id
    private int rbId;
    //报销单据
    private String rbBill;
    //报销创建时间
    private String rbCreateTime;
    //报销描述
    private String rbDesction;
    //报销金额
    private Double rbMoney;
    //报销备注
    private String rbRemark;
    //报销审核状态0已提交待审核1审核通过2审核不通过
    private int rbState;
    //审核状态变化时间
    private String rbStateTime;
    //报销人id
    private int rbTeacherId;
    //班級id
    private int rbClassId;
    //園所id
    private int rbParkId;
    //班級名
    private String rbClassName;
    //申请人
    private UserModel appUser;

    public UserModel getAppUser() {
        return appUser;
    }

    public void setAppUser(UserModel appUser) {
        this.appUser = appUser;
    }

    private int checked;

    public int getChecked() {
        return checked;
    }

    public void setChecked(int checked) {
        this.checked = checked;
    }

    public int getRbId() {
        return rbId;
    }

    public void setRbId(int rbId) {
        this.rbId = rbId;
    }

    public String getRbBill() {
        return rbBill;
    }

    public void setRbBill(String rbBill) {
        this.rbBill = rbBill;
    }

    public String getRbCreateTime() {
        return rbCreateTime;
    }

    public void setRbCreateTime(String rbCreateTime) {
        this.rbCreateTime = rbCreateTime;
    }

    public String getRbDesction() {
        return rbDesction;
    }

    public void setRbDesction(String rbDesction) {
        this.rbDesction = rbDesction;
    }

    public Double getRbMoney() {
        return rbMoney;
    }

    public void setRbMoney(Double rbMoney) {
        this.rbMoney = rbMoney;
    }

    public String getRbRemark() {
        return rbRemark;
    }

    public void setRbRemark(String rbRemark) {
        this.rbRemark = rbRemark;
    }

    public int getRbState() {
        return rbState;
    }

    public void setRbState(int rbState) {
        this.rbState = rbState;
    }

    public String getRbStateTime() {
        return rbStateTime;
    }

    public void setRbStateTime(String rbStateTime) {
        this.rbStateTime = rbStateTime;
    }

    public int getRbTeacherId() {
        return rbTeacherId;
    }

    public void setRbTeacherId(int rbTeacherId) {
        this.rbTeacherId = rbTeacherId;
    }

    public int getRbClassId() {
        return rbClassId;
    }

    public void setRbClassId(int rbClassId) {
        this.rbClassId = rbClassId;
    }

    public int getRbParkId() {
        return rbParkId;
    }

    public void setRbParkId(int rbParkId) {
        this.rbParkId = rbParkId;
    }

    public String getRbClassName() {
        return rbClassName;
    }

    public void setRbClassName(String rbClassName) {
        this.rbClassName = rbClassName;
    }

}
