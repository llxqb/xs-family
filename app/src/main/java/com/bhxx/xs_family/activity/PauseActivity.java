package com.bhxx.xs_family.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.StatusBar;
import com.bhxx.xs_family.utils.SystemInfo;
import com.bhxx.xs_family.values.GlobalValues;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.data.TempDatas;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;

import cn.jpush.android.api.JPushInterface;


public class PauseActivity extends BasicActivity {
    private Handler mHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pause);
        mHandler = new Handler();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(App.app.getData("uId"))) {
                    MainActivity.start(PauseActivity.this);

                    /**
                     * 如果是家长登录 登录监控sdk
                     */
                    String uRole = App.app.getData("uRole");
                    if (uRole.equals("2")) {
                        loginHkSdk();
                    }
                    finish();
                } else {
                    LoginActivity.start(PauseActivity.this);
                    finish();
                }
            }
        }, 2000);
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        //增加沉浸式状态栏
        new StatusBar(this).initState();
    }

    @Override
    protected void click(View view) {

    }

    @Override
    protected void onResume() {
        JPushInterface.onResume(PauseActivity.this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        JPushInterface.onPause(PauseActivity.this);
        super.onPause();
    }

    private void loginHkSdk() {
        final String url = App.app.getData("HkIp");
        String userName = App.app.getData("HkUsername");
        String password = App.app.getData("HkPassword");
//        LogUtils.i("url="+url+"userName="+userName+" password="+password);
        String macAddress = SystemInfo.getMacAddress();
        // 登录请求
        VMSNetSDK.getInstance().login(url, userName, password, macAddress);

        VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            @Override
            public void onFailure() {
                LogUtils.i("fail");
            }

            @Override
            public void loading() {
                LogUtils.i("loading");
            }

            @Override
            public void onSuccess(Object data) {
            }

        });
    }
}
