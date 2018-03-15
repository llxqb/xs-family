package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.UserHelpAdapter;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.UserHelpModel;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import java.util.LinkedHashMap;

import okhttp3.Call;

@InjectLayer(R.layout.activity_user_help)
public class UserHelpActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private UserHelpAdapter adapter;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView user_help_back;
        ListView user_help_lv;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(UserHelpActivity.this);
        getHelpList();
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.user_help_back:
                finish();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, UserHelpActivity.class);
        context.startActivity(intent);
    }

    private void getHelpList() {

        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("page", "1");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        String token = TokenUtils.getInstance().configParams(GlobalValues.USER_HELP + "?", params);
        params.put("taoken", token);

        MyOkHttp.postMap(GlobalValues.USER_HELP, "USER_HELP", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("加载失败");
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<UserHelpModel> beans = JsonUtils.getBeans(response, CommonListBean.class, UserHelpModel.class);
                if (beans.getSuccess()) {
                    if (beans.getRows() != null && beans.getRows().size() > 0) {
                        adapter = new UserHelpAdapter(beans.getRows(), UserHelpActivity.this, R.layout.user_help_item);
                        v.user_help_lv.setAdapter(adapter);
                    }
                }
            }
        }
    }
}
