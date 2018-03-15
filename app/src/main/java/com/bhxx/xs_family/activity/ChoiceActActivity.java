package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.ChoiceActAdapter;
import com.bhxx.xs_family.adapter.HomeActivityAdapter;
import com.bhxx.xs_family.adapter.PopWindowAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.entity.ApplyActEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.support.v7.widget.LinearLayoutCompat.LayoutParams;

import okhttp3.Call;

@InjectLayer(R.layout.activity_choice_act)
public class ChoiceActActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private ChoiceActAdapter actAdapter;
    private int page = 1;
    private PopupWindow popupwindow= null;
    private int typevalues = -1;
    private List<String> datas;
    private List<Integer> data;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView choice_act_back, choice_act_more;
        PullToRefreshLayout choice_act_pull;
        PullableListView choice_act_list;
    }

    @Override
    protected void init() {
        getvalues();
        ActivityCollector.addActivity(ChoiceActActivity.this);
        EventBus.getDefault().register(this);
        initActList(typevalues);
        v.choice_act_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                actAdapter = null;
                initActList(typevalues);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initActList(typevalues);
            }
        });
    }

    private void getvalues() {
        datas = new ArrayList<>();
        datas.add("全部");
        if (App.app.getData("uRole").equals("1")) {
            datas.add("我发布");
        }
        datas.add("已报名");
        datas.add("已开始");
        datas.add("已结束");
        datas.add("未开始");

        data = new ArrayList<>();
        data.add(-1);
        if (App.app.getData("uRole").equals("1")) {
            data.add(0);
        }
        data.add(1);
        data.add(3);
        data.add(2);
        data.add(4);
    }

    protected void onEventMainThread(ApplyActEntity entity) {
        for (ActModel model : actAdapter.getDataList()) {
            if (model.getActivityState() == entity.getActId()) {
                if (model.getActivityState() == 0) {
                    model.setActivityState(1);
                }
            }
        }
        actAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void initActList(int type) {
        showProgressDialog(ChoiceActActivity.this, "加载中...");
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("uId", App.app.getData("uId"));
        params.put("acConditionState", type + "");
        String token = TokenUtils.getInstance().configParams(GlobalValues.APP_ACT + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.APP_ACT, "ACT", params, new MyStringCallback());
    }

    /**
     * 跳转至活动精选
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, ChoiceActActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.choice_act_back:
                finish();
                break;
            case R.id.choice_act_more:
                if (popupwindow != null && popupwindow.isShowing()) {
                    popupwindow.dismiss();
                    return;
                } else {
                    initmPopupWindowView();
                    popupwindow.showAsDropDown(view, 0, 5);
                }
                break;
        }
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            v.choice_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.choice_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<ActModel> acts = JsonUtils.getBeans(response, CommonListBean.class, ActModel.class);
                if (acts.getSuccess()) {
                    if (acts.getRows() != null && acts.getRows().size() > 0) {
                        if (actAdapter == null) {
                            actAdapter = new ChoiceActAdapter(acts.getRows(), ChoiceActActivity.this, R.layout.choice_act_item);
                            v.choice_act_list.setAdapter(actAdapter);
                        } else {
                            actAdapter.addDataListAtLast(acts.getRows());
                            actAdapter.notifyDataSetChanged();
                        }
                    } else {
                        if (page == 1) {
                            showToast("暂无内容");
                        }
                    }
                }
            }
            v.choice_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.choice_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }

    public void initmPopupWindowView() {

        ListView pop_listview;
        // // 获取自定义布局文件pop.xml的视图
        View customView = getLayoutInflater().inflate(R.layout.popupwindow, null, false);
        // 创建PopupWindow实例,200,150分别是宽度和高度

        pop_listview = (ListView) customView.findViewById(R.id.pop_listview);

        PopWindowAdapter popWindowAdapter = new PopWindowAdapter(this, datas);
        pop_listview.setAdapter(popWindowAdapter);

        popupwindow = new PopupWindow(customView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
//        popupwindow.setAnimationStyle(R.style.dialogstyle);
        pop_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                typevalues = data.get(i);
                page = 1;
                actAdapter = null;
                initActList(typevalues);
                popupwindow.dismiss();
            }
        });
    }

    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏popupwindow
             */
            popupwindow.dismiss();
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        if(popupwindow!=null){
            popupwindow.dismiss();
        }
        super.onPause();
    }
}
