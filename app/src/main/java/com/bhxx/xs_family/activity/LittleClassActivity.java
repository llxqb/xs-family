package com.bhxx.xs_family.activity;
/**
 * 小课堂
 */
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
import com.bhxx.xs_family.adapter.LittleClassAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.LittleClassModel;
import com.bhxx.xs_family.entity.LittleClassEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_little_class)
public class LittleClassActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private int page = 1;
    private LittleClassAdapter adapter;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView little_class_back;
        PullToRefreshLayout little_class_pull;
        PullableListView little_class_list;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(LittleClassActivity.this);
        initValue();
        EventBus.getDefault().register(this);
        v.little_class_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                adapter = null;
                initValue();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initValue();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    protected void onEventMainThread(LittleClassEntity entity) {
        switch (entity.getKey()) {
            case LittleClassEntity.CANCEL_COLLECT:
                for (LittleClassModel classModel : adapter.getDataList()) {
                    if (classModel.getPtId() == entity.getLittleClassId()) {
                        if (classModel.getPtIsCollect() == 1) {
                            classModel.setPtCollectCount(classModel.getPtCollectCount() - 1);
                            classModel.setPtIsCollect(0);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                break;
            case LittleClassEntity.COLLECT:
                for (LittleClassModel classModel : adapter.getDataList()) {
                    if (classModel.getPtId() == entity.getLittleClassId()) {
                        if (classModel.getPtIsCollect() == 0) {
                            classModel.setPtCollectCount(classModel.getPtCollectCount() + 1);
                            classModel.setPtIsCollect(1);
                        }
                    }
                }
                adapter.notifyDataSetChanged();
                break;
        }
    }

    private void initValue() {
        showProgressDialog(LittleClassActivity.this, "加载中...");

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("uId", App.app.getData("uId"));
        String token = TokenUtils.getInstance().configParams(GlobalValues.QUERY_LITTLE_CLASS + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.QUERY_LITTLE_CLASS, "QUERY_LITTLE", params, new MyStringCallback());

    }

    /**
     * 小课堂跳转
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, LittleClassActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.little_class_back:
                finish();
                break;
        }
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            v.little_class_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.little_class_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
            if (page == 1) {
                showToast("加载失败");
            }
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<LittleClassModel> beans = JsonUtils.getBeans(response, CommonListBean.class, LittleClassModel.class);
                if (beans != null) {
                    if (beans.getSuccess()) {
                        if (beans.getRows() != null && beans.getRows().size() > 0) {
                            if (adapter == null) {
                                adapter = new LittleClassAdapter(beans.getRows(), LittleClassActivity.this, R.layout.little_class_item);
                                v.little_class_list.setAdapter(adapter);
                            } else {
                                adapter.addDataListAtLast(beans.getRows());
                                adapter.notifyDataSetChanged();
                            }
                        } else {
                            if (page == 1) {
                                showToast("暂无内容");
                            }
                        }
                    } else {
                        if (page == 1) {
                            showToast("加载失败");
                        }
                    }
                }
            } else {
                if (page == 1) {
                    showToast("加载失败");
                }
            }
            v.little_class_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.little_class_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }
}
