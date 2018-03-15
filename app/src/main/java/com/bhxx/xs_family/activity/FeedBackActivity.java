package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import java.util.LinkedHashMap;

import okhttp3.Call;

/**
 * 意见反馈
 */
@InjectLayer(R.layout.activity_feedback)
public class FeedBackActivity extends BasicActivity {
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView feedback_back;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView feedback_submit;
        EditText feedback_content, feedback_email;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(FeedBackActivity.this);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.feedback_back:
                finish();
                break;
            case R.id.feedback_submit:
                if (TextUtils.isEmpty(v.feedback_content.getText().toString())) {
                    showToast("请填写你的意见");
                    return;
                }
                if (TextUtils.isEmpty(v.feedback_email.getText().toString())) {
                    showToast("请填写你的联系邮箱");
                    return;
                }
                if (!CheckUtils.isEmail(v.feedback_email.getText().toString())) {
                    showToast("邮箱格式不正确");
                    return;
                }
                startSubmit();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, FeedBackActivity.class);
        context.startActivity(intent);
    }

    private void startSubmit() {
        showProgressDialog(FeedBackActivity.this, "正在提交...");
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("feedBackId", App.app.getData("uId"));
        params.put("contactWay", v.feedback_email.getText().toString());
        params.put("descContent", v.feedback_content.getText().toString());
        String token = TokenUtils.getInstance().configParams(GlobalValues.FEED_BACK + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.FEED_BACK, "FEED_BACK", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                dismissProgressDialog();
                showToast("提交失败");
            }

            @Override
            public void onResponse(String response, int id) {
                dismissProgressDialog();
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        new AlertDialog.Builder(FeedBackActivity.this).setCancelable(false).setTitle("提示").setMessage("提交成功").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                            }
                        }).create().show();
                    } else {
                        showToast("提交失败");
                    }
                } else {
                    showToast("提交失败");
                }
            }
        });

    }
}
