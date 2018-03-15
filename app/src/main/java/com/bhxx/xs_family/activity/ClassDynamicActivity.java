package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.DynamicAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.DynamicModel;
import com.bhxx.xs_family.entity.DynamicCollectEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_class_dynamic)
public class ClassDynamicActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private int page = 1;
    private DynamicAdapter adapter;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView class_dynamic_back;
        PullToRefreshLayout class_dynamic_pull;
        PullableListView class_dynamic_list;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(ClassDynamicActivity.this);
        EventBus.getDefault().register(this);
        update();
        v.class_dynamic_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                // 下拉刷新操作
                adapter = null;
                page = 1;
                update();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                // 上拉刷新操作
                page = page + 1;
                update();
            }
        });
    }

    protected void onEventMainThread(DynamicCollectEntity entity) {
        switch (entity.getKey()) {
            case DynamicCollectEntity.CANCEL_COLLECT:
                for (DynamicModel dynamicModel : adapter.getDataList()) {
                    if (dynamicModel.getDcId() == entity.getDynamicId()) {
                        if (dynamicModel.getDcIsCollect() == 1) {
                            dynamicModel.setDcIsCollect(0);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case DynamicCollectEntity.COLLECT:
                for (DynamicModel dynamicModel : adapter.getDataList()) {
                    if (dynamicModel.getDcId() == entity.getDynamicId()) {
                        if (dynamicModel.getDcIsCollect() == 0) {
                            dynamicModel.setDcIsCollect(1);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    /**
     * 获取班级动态信息
     */
    private void update() {
        showProgressDialog(ClassDynamicActivity.this, "加载中...");
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("uId", App.app.getData("uId"));
        params.put("dcClassId", App.app.getData("classId"));
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.APP_DYNAMIC + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.APP_DYNAMIC, "dynamic", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            v.class_dynamic_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.class_dynamic_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            CommonListBean<DynamicModel> dynamicmodel = JsonUtils.getBeans(response, CommonListBean.class, DynamicModel.class);
            if (dynamicmodel != null) {
                if (dynamicmodel.getSuccess()) {
                    if (dynamicmodel.getRows() != null) {
                        if (adapter == null) {
                            adapter = new DynamicAdapter(dynamicmodel.getRows(), ClassDynamicActivity.this, R.layout.class_dynamic_item);
                            v.class_dynamic_list.setAdapter(adapter);
                        } else {
                            adapter.addDataListAtLast(dynamicmodel.getRows());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }
            v.class_dynamic_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.class_dynamic_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

    }


    /**
     * 跳转班级动态页面
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, ClassDynamicActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.class_dynamic_back:
                finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
