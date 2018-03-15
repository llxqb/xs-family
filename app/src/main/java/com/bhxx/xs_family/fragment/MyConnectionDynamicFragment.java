package com.bhxx.xs_family.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.DynamicAdapter;
import com.bhxx.xs_family.adapter.LittleClassAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.DynamicModel;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class MyConnectionDynamicFragment extends BaseFragment {
    private DynamicAdapter adapter;
    private int page = 1;
    String classId;

    @InjectAll
    private Views v;

    private class Views {
        PullableListView my_act_list;
        PullToRefreshLayout my_act_pull;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.albumchecktwo, null);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    protected void init() {

        v.my_act_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                adapter = null;
                page = 1;
                initMyConnectionDynamicList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initMyConnectionDynamicList();
            }
        });
        initMyConnectionDynamicList();
    }


    private void initMyConnectionDynamicList() {
        showProgressDialog(getActivity());
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("clHomeLeaderId", App.app.getData("uId"));
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.PARENT_CONNECTION_DYNAMIC + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.PARENT_CONNECTION_DYNAMIC, "PARENT_CONNECTION_DYNAMIC", params, new MyLoadCallback());
    }

    private class MyLoadCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast("加载失败");
            v.my_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.my_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<DynamicModel> listBean = JsonUtils.getBean(response, CommonListBean.class, DynamicModel.class);
                if (listBean.getRows() != null && listBean.getRows().size() > 0) {
                    if (adapter == null) {
                        adapter = new DynamicAdapter(listBean.getRows(), getActivity(), R.layout.class_dynamic_item);
                        v.my_act_list.setAdapter(adapter);
                    } else {
                        adapter.addDataListAtLast(listBean.getRows());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (page == 1) {
                        adapter = new DynamicAdapter(null, getActivity(), R.layout.class_dynamic_item);
                        v.my_act_list.setAdapter(adapter);
                    }
                }

            }
            v.my_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.my_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }

    @Override
    protected void click(View view) {
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
