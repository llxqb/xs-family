package com.bhxx.xs_family.utils;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.bhxx.xs_family.application.App;

import java.io.File;
import java.io.FileInputStream;

/**
 * Created by bh1988034 on 2016/9/26.
 */
public class SystemInfo {
    /**
     * 获取登录设备mac地址
     *
     * @return
     */
    public static String getMacAddress() {
        WifiManager wm = (WifiManager) App.app.getSystemService(Context.WIFI_SERVICE);
        String mac = wm.getConnectionInfo().getMacAddress();
        return mac == null ? "" : mac;
    }

}
