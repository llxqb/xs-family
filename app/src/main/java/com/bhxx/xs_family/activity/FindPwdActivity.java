package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.TimeButton;

import java.util.LinkedHashMap;

import okhttp3.Call;

@InjectLayer(R.layout.activity_find_pwd)
public class FindPwdActivity extends BasicActivity {
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView forget_back;
        EditText find_pwd_phone, find_pwd_code, find_pwd_new;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button change_pwd_bt;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TimeButton code_bt;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(FindPwdActivity.this);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.forget_back:
                finish();
                break;
            case R.id.change_pwd_bt:
                if (TextUtils.isEmpty(v.find_pwd_phone.getText().toString())) {
                    showToast("请填写手机号");
                    return;
                }
                if (!CheckUtils.checkMobile(v.find_pwd_phone.getText().toString())) {
                    showToast("手机格式不正确");
                    return;
                }
                if (TextUtils.isEmpty(v.find_pwd_code.getText().toString())) {
                    showToast("请填写验证码");
                    return;
                }
                if (TextUtils.isEmpty(v.find_pwd_new.getText().toString())) {
                    showToast("请设置新密码");
                    return;
                }
                checkCode(v.find_pwd_phone.getText().toString(), v.find_pwd_code.getText().toString());
                break;
            case R.id.code_bt:
                if (TextUtils.isEmpty(v.find_pwd_phone.getText().toString())) {
                    showToast("请填写手机号");
                    return;
                }
                if (!CheckUtils.checkMobile(v.find_pwd_phone.getText().toString())) {
                    showToast("手机格式不正确");
                    return;
                }
                v.code_bt.setRun(true);
                getCode(v.find_pwd_phone.getText().toString());
                break;
        }
    }

    /**
     * 忘记密码跳转
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, FindPwdActivity.class);
        context.startActivity(intent);
    }

    /**
     * 获取验证码
     *
     * @param phoneNum
     */
    private void getCode(String phoneNum) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("uMobile", phoneNum);
        params.put("validType", "1");
        String token = TokenUtils.getInstance().configParams(GlobalValues.MOBILE_CODE + "?", params);
        params.put("taoken", token);

        MyOkHttp.postMap(GlobalValues.MOBILE_CODE, "GET_CODE", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("获取验证码失败,请稍后重试");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                } else {
                    showToast("获取验证码失败,请稍后重试");
                }
            }
        });
    }

    /**
     * 校验验证码
     *
     * @param phone
     * @param code
     */
    private void checkCode(String phone, String code) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("uMobile", phone);
        params.put("validCode", code);
        String token = TokenUtils.getInstance().configParams(GlobalValues.CHECK_CODE + "?", params);
        params.put("taoken", token);

        MyOkHttp.postMap(GlobalValues.CHECK_CODE, "CHECK_CODE", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("验证码校验失败请稍后重试");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        updatePwd();
                    } else {
                        showToast(bean.getMessage());
                    }
                }
            }
        });
    }

    /**
     * 修改密码
     */
    private void updatePwd() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("uMobile", v.find_pwd_phone.getText().toString());
        params.put("volidCode", v.find_pwd_code.getText().toString());
        params.put("uPassWord", v.find_pwd_new.getText().toString());

        String token = TokenUtils.getInstance().configParams(GlobalValues.FORGET_PWD + "?", params);
        params.put("taoken", token);

        MyOkHttp.postMap(GlobalValues.FORGET_PWD, "FORGET_PWD", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("修改失败请稍后重试");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        showToast("修改成功");
                        finish();
                        LoginActivity.start(FindPwdActivity.this);
                        ActivityCollector.finishAll();
                    } else {
                        showToast("修改成功");
                    }
                }
            }
        });
    }
}
