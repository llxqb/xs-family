package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.LittleClassModel;
import com.bhxx.xs_family.entity.LittleClassEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.zhy.http.okhttp.callback.Callback;
import com.zhy.http.okhttp.callback.FileCallBack;

import java.io.File;
import java.util.LinkedHashMap;

import okhttp3.Call;
import okhttp3.Response;

@InjectLayer(R.layout.activity_class_details)
public class ClassDetailsActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private LittleClassModel data;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView class_details_back, class_details_collect;
        ProgressBar pb;
        WebView little_class_web;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(ClassDetailsActivity.this);
        if (!App.app.getData("uRole").equals("2")) {
            v.class_details_collect.setVisibility(View.GONE);
        }
        v.pb.setMax(100);
        v.little_class_web.getSettings().setJavaScriptEnabled(true);
        v.little_class_web.getSettings().setSupportZoom(false);
//        v.little_class_web.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//        v.little_class_web.getSettings().setUseWideViewPort(true);
        v.little_class_web.getSettings().setBuiltInZoomControls(false);
        v.little_class_web.getSettings().setDisplayZoomControls(false);
//        v.little_class_web.getSettings().setLoadWithOverviewMode(true);
//        v.little_class_web.requestFocus();
        v.little_class_web.setWebChromeClient(new WebChromeViewClient());

        if (getIntent() != null) {
            data = (LittleClassModel) getIntent().getSerializableExtra("class");
            if (data != null) {
                if (data.getPtIsCollect() == 1) {
                    v.class_details_collect.setImageResource(R.mipmap.album_collect_pre);
                } else {
                    v.class_details_collect.setImageResource(R.mipmap.album_collect);
                }
                v.little_class_web.loadUrl(GlobalValues.LITTLE_CLASS_URL + data.getPtId());
            }

        }
    }

    public static void start(Context context, LittleClassModel little) {
        Intent intent = new Intent(context, ClassDetailsActivity.class);
        intent.putExtra("class", little);
        context.startActivity(intent);
    }

    private class WebChromeViewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            v.pb.setProgress(newProgress);
            if (newProgress == 100) {
                v.pb.setVisibility(View.GONE);
            }
            super.onProgressChanged(view, newProgress);
        }

    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.class_details_back:
                finish();
                break;
            case R.id.class_details_collect:
                collect();
                break;
        }
    }

    private void collect() {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("clHomeLeaderId", App.app.getData("uId"));
        params.put("clCollectedId", data.getPtId() + "");
        params.put("clCollectedType", GlobalValues.COLLECT_CLASS);
        String token = TokenUtils.getInstance().configParams(GlobalValues.SOME_COLLECT_CANClE + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.SOME_COLLECT_CANClE, "CLASS_COLLECT", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("操作失败");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        if (data.getPtIsCollect() == 1) {
                            v.class_details_collect.setImageResource(R.mipmap.album_collect);
                            EventBus.getDefault().post(new LittleClassEntity(data.getPtId(), LittleClassEntity.CANCEL_COLLECT));
                        } else {
                            v.class_details_collect.setImageResource(R.mipmap.album_collect_pre);
                            EventBus.getDefault().post(new LittleClassEntity(data.getPtId(), LittleClassEntity.COLLECT));
                        }
                    } else {
                        showToast(bean.getMessage());
                    }
                }
            }
        });

    }

}
