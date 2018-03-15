package com.bhxx.xs_family.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.DynamicsCheckAdapter;
import com.bhxx.xs_family.adapter.MyActAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.DynamicModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.entity.AlbumCheckOneEntity;
import com.bhxx.xs_family.entity.LookCheckStatusEntity;
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

public class DynamicsCheckItemFragment extends LazyLoadFragment {

    private int type;
    private PullToRefreshLayout my_act_pull;
    private PullableListView my_act_list;
    private RelativeLayout buttom;
    private TextView Approve;
    private TextView noApprove;
    private boolean isPrepared = false;
    private boolean hasLoadOnce = false;
    private DynamicsCheckAdapter adapter;
    private CheckBox checkbox_all;
    boolean nolistener = true;
    private int page = 1;
    private String classId;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Handler_Inject.injectFragment(this, view);
        EventBus.getDefault().register(this);
        if (getContentLayoutId() > 0) {
            this.checkbox_all = (CheckBox) view.findViewById(R.id.checkbox_all);
            this.my_act_pull = (PullToRefreshLayout) view.findViewById(R.id.my_act_pull);
            this.my_act_list = (PullableListView) view.findViewById(R.id.my_act_list);
            buttom = (RelativeLayout) view.findViewById(R.id.buttom);

            Approve = (TextView) view.findViewById(R.id.Approve);
            noApprove = (TextView) view.findViewById(R.id.noApprove);
            if (type == 1) {
                buttom.setVisibility(View.GONE);
            }
            initdata();
        }
        isPrepared = true;
        lazyLoad();
    }

    private void initdata() {
        checkbox_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (DynamicModel model : adapter.getDataList()) {
                        model.setChecked(1);
                    }
                } else {
                    for (DynamicModel model : adapter.getDataList()) {
                        if (nolistener == false) {
                            nolistener = true;
                            break;
                        }
                        model.setChecked(0);

                    }
                }
                adapter.notifyDataSetChanged();
            }
        });

        my_act_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                adapter = null;
                page = 1;
                initDynamicsList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initDynamicsList();
            }
        });

        Approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isApproveList("1");
            }
        });
        noApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isApproveList("2");
            }
        });
    }

    protected void onEventMainThread(AlbumCheckOneEntity entity) {
        switch (entity.getKey()) {
            case AlbumCheckOneEntity.ALBUM_CHECKED:
                checkbox_all.setChecked(true);
                break;
            case AlbumCheckOneEntity.ALBUM_NOT_CHECKED:
                nolistener = false;
                checkbox_all.setChecked(false);
                break;
        }
    }

    protected void onEventMainThread(LookCheckStatusEntity entity) {
        classId = entity.getKey() + "";
        adapter = null;
        page = 1;
        initDynamicsList();
    }

    private void isApproveList(String isapprove) {
        showProgressBar("加载中...");
        StringBuffer listcheckid = new StringBuffer();
        for (DynamicModel model : adapter.getDataList()) {
            if (model.getChecked() == 1 && !TextUtils.isEmpty(model.getDcId() + "")) {
                listcheckid.append(model.getDcId() + ",");
            }
        }
        if(listcheckid.length()==0){
            showToast("请先选择要审核的内容");
            dismissProgressBar();
            return;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("uId", App.app.getData("uId"));
        params.put("dcIds", listcheckid.toString());//勾选集合id
        params.put("auditingState", isapprove);// 1通过  2不通过
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.DYNAMICSCHECKAPPROVE + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.DYNAMICSCHECKAPPROVE, "DYNAMICSCHECKAPPROVE", params, new MyApproveCallback());
    }

    private class MyApproveCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("请求超时");
            dismissProgressBar();
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressBar();
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (bean.isSuccess()) {
                    adapter = null;
                    page = 1;
                    initDynamicsList();
                }
            }
        }
    }

    /**
     * 实例化
     *
     * @param type
     * @return
     */
    public static DynamicsCheckItemFragment getInstance(int type) {
        DynamicsCheckItemFragment fragment = new DynamicsCheckItemFragment();
        Bundle bd = new Bundle();
        bd.putInt("type", type);
        fragment.setArguments(bd);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.type = getArguments().getInt("type");
        }
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("请求失败");
            my_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            my_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            Log.i("Tag","response="+response);
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<DynamicModel> act = JsonUtils.getBean(response, CommonListBean.class, DynamicModel.class);
                if (act.getRows() != null && act.getRows().size() > 0) {
                    showContent();
                    if (adapter == null) {
                        adapter = new DynamicsCheckAdapter(act.getRows(), getActivity(), R.layout.dynamics_check_item, type);
                        my_act_list.setAdapter(adapter);
                    }else{
                        adapter.addDataListAtLast(act.getRows());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (page == 1) {
                        adapter = new DynamicsCheckAdapter(act.getRows(), getActivity(), R.layout.dynamics_check_item, type);
                        my_act_list.setAdapter(adapter);
                        checkbox_all.setChecked(false);
                    }
                }
            } else {
                showError();
            }
            my_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            my_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }


    }

    private void initDynamicsList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("uId", App.app.getData("uId"));
        if (!TextUtils.isEmpty(classId)) {
            params.put("dcClassId", classId);
        }
        params.put("auditingState", type + "");
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.DYNAMICSCHECK + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.DYNAMICSCHECK, "DYNAMICSCHECK", params, new MyStringCallback());
    }

    @Override
    protected void lazyLoad() {
        if (!isPrepared || hasLoadOnce || !isVisible) {
            return;
        }
        hasLoadOnce = true;
        showLoading();
        initDynamicsList();

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
        return R.layout.albumcheckone;
    }

    @Override
    protected int getNothingLayoutId() {
        return 0;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(getActivity());
        super.onDestroy();
    }
}
