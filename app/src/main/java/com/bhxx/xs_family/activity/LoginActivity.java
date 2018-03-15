package com.bhxx.xs_family.activity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.support.annotation.ColorRes;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.HkModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.db.dao.SysMessDao;
import com.bhxx.xs_family.fragment.TeacherMineFragment;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.StatusBar;
import com.bhxx.xs_family.utils.SystemInfo;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.Constants;
import com.hikvision.sdk.data.TempDatas;
import com.hikvision.sdk.net.bean.LoginData;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.LinkedHashMap;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Message;
import okhttp3.Call;

@InjectLayer(R.layout.activity_login)
public class LoginActivity extends BasicActivity {
    @InjectAll
    private Views v;
    public static boolean isForeground = false;

    private class Views {
        EditText login_user_et, login_pwd_et;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView forget_pwd;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button login_bt;
    }

    @Override
    protected void init() {
        LoginOut();
        App.app.clearDatabase("Cache");
        ActivityCollector.finishAll();
        ActivityCollector.addActivity(this);
        //增加沉浸式状态栏
        new StatusBar(this).initState();

//        SysMessDao.clearTable();
        if (TextUtils.isEmpty(App.app.getData("jgid"))) {
            jgts();
        }

        Intent it = getIntent();
        String from = "";
        if (it != null) {
            from = it.getStringExtra("from");
        }
        if (("hintlogin").equals(from)) {
            // 创建退出对话框
            final AlertDialog isExit = new AlertDialog.Builder(LoginActivity.this).create();
            isExit.setCancelable(false);
            isExit.setCanceledOnTouchOutside(false);
            // 设置对话框标题
            isExit.setTitle("系统提示");
            // 设置对话框消息
            isExit.setMessage("您的帐号已在另一台设备登录");
            // 添加选择按钮并注册监听
            isExit.setButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    isExit.dismiss();
                }
            });
            // 显示对话框
            isExit.show();
        }

    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.forget_pwd:
                FindPwdActivity.start(LoginActivity.this);
                break;
            case R.id.login_bt:
                if (TextUtils.isEmpty(v.login_user_et.getText().toString())) {
                    showToast("请输入手机号");
                    return;
                }
                if (TextUtils.isEmpty(v.login_pwd_et.getText().toString())) {
                    showToast("请输入密码");
                    return;
                }
                if (!CheckUtils.checkMobile(v.login_user_et.getText().toString())) {
                    showToast("手机号格式不正确");
                    return;
                }
                showProgressDialog(this);
                if (TextUtils.isEmpty(App.app.getData("jgid"))) {
                    dismissProgressDialog();
                    jgts();
                }
                login(v.login_user_et.getText().toString(), v.login_pwd_et.getText().toString());
                break;
        }
    }

    protected void jgts() {
        registerMessageReceiver(); // used for receive msg
        JPushInterface.init(this);

        String rid = JPushInterface.getRegistrationID(this);
        if (!rid.isEmpty()) {
            App.app.setData("jgid", rid);
        }
    }

    @Override
    protected void onResume() {
        isForeground = true;

        super.onResume();
    }

    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();
    }

    // for receive customer msg from jpush server
    private MessageReceiver mMessageReceiver;
    public static final String MESSAGE_RECEIVED_ACTION = "com.bhxx.xs_family.MESSAGE_RECEIVED_ACTION";
    public static final String KEY_TITLE = "title";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_EXTRAS = "extras";

    public void registerMessageReceiver() {
        mMessageReceiver = new MessageReceiver();
        IntentFilter filter = new IntentFilter();
        filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
        filter.addAction(MESSAGE_RECEIVED_ACTION);
        registerReceiver(mMessageReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMessageReceiver);
    }

    public class MessageReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (MESSAGE_RECEIVED_ACTION.equals(intent.getAction())) {
                String messge = intent.getStringExtra(KEY_MESSAGE);
                String extras = intent.getStringExtra(KEY_EXTRAS);
                StringBuilder showMsg = new StringBuilder();
                showMsg.append(KEY_MESSAGE + " : " + messge + "\n");
                if (!TextUtils.isEmpty(extras)) {
                    showMsg.append(KEY_EXTRAS + " : " + extras + "\n");
                }
            }
        }
    }

    /**
     * 跳转登录界面
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    /**
     * 登录
     *
     * @param mobile
     * @param pwd
     */
    private void login(String mobile, String pwd) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("uMobile", mobile);
        params.put("uPassWord", pwd);

        params.put("appRegistNo", App.app.getData("jgid"));
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.LOGIN + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.LOGIN, 1, "Login", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            switch (id){
                case 1:
                    showToast("登录失败");
                    break;
            }
            dismissProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 1:
                    if (!TextUtils.isEmpty(response)) {
                        dismissProgressDialog();
                        LogUtils.i("response=" + response);
                        CommonBean<UserModel> userBean = JsonUtils.getBean(response, CommonBean.class, UserModel.class);
                        if (userBean.isSuccess()) {
                            UserModel user = userBean.getRows();
                            if (user != null) {
                                App.app.setData("uId", user.getuId() + "");
                                App.app.setData("classId", user.getuClassId() + "");
                                App.app.setData("parkId", user.getuParkId() + "");
                                App.app.setData("mobile", user.getuMobile());
                                App.app.setData("uPic", user.getuHeadPic());
                                App.app.setData("uRole", user.getuRole() + "");
                                App.app.setData("rToken", user.getuRongYunTonken());
                                App.app.setData("uMonitorid", user.getmId() + "");
                                App.app.setData("uTitle", user.getMtitle() + "");
                                App.app.saveObjData("user", user);
//                                connect(user.getuRongYunTonken());
                                /**
                                 * 如果是家长登录 登录监控sdk
                                 */
                                if (user.getuRole() == 2) {
                                    loginACSdk();
                                }

                            }
                            showToast("登录成功");
                            MainActivity.start(LoginActivity.this);
                            finish();
                            ActivityCollector.finishAll();
                        } else {
                            showToast(userBean.getMessage());
                        }
                    } else {
                        showToast("登录失败");
                        dismissProgressDialog();
                    }
                    break;
                case 2:
                    CommonBean<HkModel> hksdkbean = JsonUtils.getBean(response, CommonBean.class, HkModel.class);
                    if (hksdkbean.isSuccess()) {
                        HkModel hkModel = hksdkbean.getRows();
                        if (hkModel != null) {
                            String userName = hkModel.getHkAdmin();
                            String password = hkModel.getHKPassWord();
                            String url = "https://" + hkModel.getHKIP() + ":" + hkModel.getHKPort();

                            App.app.setData("HkUsername", userName);
                            App.app.setData("HkPassword", password);
                            App.app.setData("HkIp", url);

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
                                    LogUtils.i("onSuccess");
                                }

                            });
                        }
                    }

                    break;
            }

        }


    }

    private void loginACSdk() {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        String token = TokenUtils.getInstance().configParams(GlobalValues.HKSDK_LOGIN + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.HKSDK_LOGIN, 2, "HKSDKLogin", params, new MyStringCallback());
    }

    /**
     * 建立与融云服务器的连接
     *
     * @param token
     */
    private void connect(String token) {
        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {
            /**
             * IMKit SDK调用第二步,建立与服务器的连接
             */
            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                /**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */
                @Override
                public void onTokenIncorrect() {
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.d("LoginActivity", "--onSuccess" + userid);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }

    /**
     * 调用 退出接口 清空账号登录信息 保证一个账号登陆一次
     */
    private void LoginOut() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        if(!TextUtils.isEmpty(App.app.getData("uId"))){
            params.put("uId", App.app.getData("uId"));
            String taoken = TokenUtils.getInstance().configParams(GlobalValues.LOGINOUT + "?", params);
            params.put("taoken", taoken);
            MyOkHttp.postMap(GlobalValues.LOGINOUT, 3, "LOGINOUT", params, new MyStringCallback());
        }
    }


}
