package com.bhxx.xs_family.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.MyActAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.beans.CommonListBean;
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

public class MyActItemFragment extends LazyLoadFragment {

    private int type;
    private PullToRefreshLayout my_act_pull;
    private PullableListView my_act_list;
    private boolean isPrepared = false;
    private boolean hasLoadOnce = false;
    private MyActAdapter adapter;
    private int page = 1;
    private static int broadcast=0;

    @Override
    public void onViewCreated(View view,  Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Handler_Inject.injectFragment(this, view);
        if (getContentLayoutId() > 0) {
            this.my_act_pull = (PullToRefreshLayout) view.findViewById(R.id.my_act_pull);
            this.my_act_list = (PullableListView) view.findViewById(R.id.my_act_list);
            initdata();
        }
        isPrepared = true;
        lazyLoad();
    }

    private void initdata() {
        my_act_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                adapter = null;
                page = 1;
                initActList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initActList();
            }
        });
    }

    private void initActList() {
        //0未开始  1一开始  2审核中   3未通过审核
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("acPublisherId", App.app.getData("uId"));
        params.put("searchState", String.valueOf(type));
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.ACT_MANAGER + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.ACT_MANAGER, "ActNoStart", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("连接超时");
            my_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            my_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<ActModel> act = JsonUtils.getBean(response, CommonListBean.class, ActModel.class);
                if (act.getSuccess()) {
                    if (act.getRows() != null && act.getRows().size() > 0) {
                        if (adapter == null) {
                            adapter = new MyActAdapter(act.getRows(), getActivity(), R.layout.my_act_item,type);
                            my_act_list.setAdapter(adapter);
                        } else {
                            adapter.addDataListAtLast(act.getRows());
                            my_act_list.setAdapter(adapter);
                        }
                    }
                }
            }
            my_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            my_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }

    private class MyLoadCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("连接超时");
            showError();
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<ActModel> act = JsonUtils.getBean(response, CommonListBean.class, ActModel.class);
                if (act.getSuccess()) {
                    if (act.getRows() != null && act.getRows().size() > 0) {
                        showContent();
                        if (adapter == null) {
                            adapter = new MyActAdapter(act.getRows(), getActivity(), R.layout.my_act_item,type);
                            my_act_list.setAdapter(adapter);
                        }
                    } else {
                        showContent();
                    }
                } else {
                    showContent();
                }
            } else {
                showError();
            }
        }
    }

    /**
     * 我的活动实例化
     *
     * @param type
     * @return
     */
    public static MyActItemFragment getInstance(int type) {
        MyActItemFragment fragment = new MyActItemFragment();
        Bundle bd = new Bundle();
        bd.putInt("type", type);
        fragment.setArguments(bd);
        return fragment;
    }

    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.type = getArguments().getInt("type");
        }
    }

    @Override
    public void onResume() {
        if(broadcast ==1){
            adapter = null;
            initActList();
        }
        super.onResume();
    }

    public static void flashui(){
        broadcast = 1;
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || hasLoadOnce || !isVisible) {
            return;
        }
        hasLoadOnce = true;
        showLoading();

        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("acPublisherId",App.app.getData("uId"));
        params.put("searchState", String.valueOf(type));
        params.put("page", page+"");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.ACT_MANAGER + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.ACT_MANAGER, "ActNoStart", params, new MyLoadCallback());
    }

    @Override
    protected int getLoadingLayoutId() {
        return R.layout.loading_layout;
    }

    @Override
    protected int getErrorLayoutId() {
        return 0;
    }

    @Override
    protected int getContentLayoutId() {
        return R.layout.my_act_list_layout;
    }

    @Override
    protected int getNothingLayoutId() {
        return 0;
    }
}
