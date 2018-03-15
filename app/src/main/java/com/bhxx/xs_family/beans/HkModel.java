package com.bhxx.xs_family.beans;

import java.io.Serializable;

/**
 * Created by bihua on 2016/10/20.
 * 海康监控sdk实体类
 */
public class HkModel implements Serializable{
    //HkAdmin   HkPassword  HkIp   HkPort   HkSysCode  5个字段
    public String HkAdmin;
    public String HKPassWord;
    public String HKIP;
    public String HKPort;//端口

    public String getHkAdmin() {
        return HkAdmin;
    }

    public void setHkAdmin(String hkAdmin) {
        HkAdmin = hkAdmin;
    }

    public String getHKPassWord() {
        return HKPassWord;
    }

    public void setHKPassWord(String HKPassWord) {
        this.HKPassWord = HKPassWord;
    }

    public String getHKIP() {
        return HKIP;
    }

    public void setHKIP(String HKIP) {
        this.HKIP = HKIP;
    }

    public String getHKPort() {
        return HKPort;
    }

    public void setHKPort(String HKPort) {
        this.HKPort = HKPort;
    }
}
