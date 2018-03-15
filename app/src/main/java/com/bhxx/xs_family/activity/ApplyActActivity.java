package com.bhxx.xs_family.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.StudentModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.entity.ApplyActEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import java.util.LinkedHashMap;

import okhttp3.Call;

@InjectLayer(R.layout.activity_apply_act)
public class ApplyActActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private String actId;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView apply_act_back;
        TextView apply_act_kid_name, apply_act_kid_class;
        EditText apply_act_name, apply_act_relationship, apply_act_phone, apply_act_email, apply_act_remark;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button act_apply_bt;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(ApplyActActivity.this);
        v.act_apply_bt.setText("提交报名");
        if (getIntent() != null) {
            this.actId = getIntent().getStringExtra("actId");
        }
        if (App.app.readObjData("user") != null) {
            UserModel user = (UserModel) App.app.readObjData("user");
            StudentModel student = user.getAppStudent();

            if (student != null) {
                if (!TextUtils.isEmpty(student.getStName())) {
                    v.apply_act_kid_name.setText(student.getStName());
                }

                if (!TextUtils.isEmpty(student.getAppClass().getClName())) {
                    v.apply_act_kid_class.setText(student.getAppClass().getClName());
                }
            }
        }
    }

    /**
     * 跳转报名
     *
     * @param context
     */
    public static void start(Context context, int actId) {
        Intent intent = new Intent(context, ApplyActActivity.class);
        intent.putExtra("actId", actId + "");
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.apply_act_back:
                finish();
                break;
            case R.id.act_apply_bt:
                if (TextUtils.isEmpty(v.apply_act_name.getText().toString())) {
                    showToast("请填写报名人姓名");
                    return;
                }
                if (TextUtils.isEmpty(v.apply_act_relationship.getText().toString())) {
                    showToast("请填写与孩子关系");
                    return;
                }
                if (TextUtils.isEmpty(v.apply_act_phone.getText().toString())) {
                    showToast("请填写联系方式");
                    return;
                } else {
                    if (!CheckUtils.checkMobile(v.apply_act_phone.getText().toString())) {
                        showToast("请检查联系方式是否有误");
                        return;
                    }
                }
                if (TextUtils.isEmpty(v.apply_act_email.getText().toString())) {
                    showToast("请填写联系邮箱");
                    return;
                } else {
                    if (!CheckUtils.isEmail(v.apply_act_email.getText().toString())) {
                        showToast("请检查邮箱是否有误");
                        return;
                    }
                }
                startApply();
                break;
        }
    }

    /**
     * 报名成功弹窗
     */
    private void showSuccess() {
        View view = LayoutInflater.from(ApplyActActivity.this).inflate(R.layout.apply_act_success, null);
        final Dialog log = new Dialog(ApplyActActivity.this, R.style.transparentFrameWindowStyle);
        ImageView apply_success_close = (ImageView) view.findViewById(R.id.apply_success_close);
        TextView apply_success_ok = (TextView) view.findViewById(R.id.apply_success_ok);
        log.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = log.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示位置
        log.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        log.setCanceledOnTouchOutside(false);
        log.setCancelable(false);
        log.show();
        int measureWidth = getResources().getDisplayMetrics().widthPixels * 4 / 5;
        window.setLayout(measureWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        apply_success_close.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
                finish();
            }
        });
        apply_success_ok.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
                finish();
            }
        });
    }

    /**
     * 报名
     */
    private void startApply() {
        showProgressDialog(ApplyActActivity.this, "报名中...");

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();

        params.put("aoActivityId", this.actId);
        params.put("aoHomeLeaderId", App.app.getData("uId"));
        params.put("aoContactName", v.apply_act_name.getText().toString());
        params.put("aoContactMobile", v.apply_act_phone.getText().toString());
        params.put("aoContactEmail", v.apply_act_email.getText().toString());
        if (!TextUtils.isEmpty(v.apply_act_remark.getText().toString())) {
            params.put("aoRemark", v.apply_act_remark.getText().toString());
        }
        params.put("aoRelationType", v.apply_act_relationship.getText().toString());
        params.put("aoStudentName", v.apply_act_kid_name.getText().toString());
        params.put("aoClassName", v.apply_act_kid_class.getText().toString());

        String token = TokenUtils.getInstance().configParams(GlobalValues.APPLY_ACT + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.APPLY_ACT, "APPLY_ACT", params, new MyStringCallback());

    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast("error");
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            LogUtils.i("response=" + response);
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> result = JsonUtils.getBean(response, CommonBean.class, String.class);
                if ( result != null && result.isSuccess()) {
                    showSuccess();
                    EventBus.getDefault().post(new ApplyActEntity(Integer.parseInt(actId)));
                } else {
                    showToast(result.getMessage());
                }
            } else {
                showToast("报名失败");
            }
        }
    }

}
