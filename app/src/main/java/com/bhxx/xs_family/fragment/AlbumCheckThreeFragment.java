package com.bhxx.xs_family.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.AlbumCheckAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.AlbumModel;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.entity.AlbumCheckThreeEntity;
import com.bhxx.xs_family.entity.AlbumCheckTwoEntity;
import com.bhxx.xs_family.entity.LookCheckStatusEntity;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class AlbumCheckThreeFragment extends BaseFragment {
    private AlbumCheckAdapter adapter;
    private boolean nolistener = true;
    private int page = 1;
    private String classId;

    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView Approve, noApprove;
        CheckBox checkbox_all;
        PullToRefreshLayout my_act_pull;
        PullableListView my_act_list;
        RelativeLayout buttom;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.albumcheckone, null);
        Handler_Inject.injectFragment(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    @Override
    protected void init() {
        v.checkbox_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (AlbumModel model : adapter.getDataList()) {
                        model.setChecked(1);
                    }
                } else {
                    for (AlbumModel model : adapter.getDataList()) {
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

        v.my_act_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                adapter = null;
                page = 1;
                initAlbumCheckList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initAlbumCheckList();
            }
        });
        initAlbumCheckList();
    }


    protected void onEventMainThread(AlbumCheckThreeEntity entity) {

        switch (entity.getKey()) {
            case AlbumCheckThreeEntity.ALBUM_CHECKED:
                v.checkbox_all.setChecked(true);
                break;
            case AlbumCheckThreeEntity.ALBUM_NOT_CHECKED:
                nolistener = false;
                v.checkbox_all.setChecked(false);
                break;
        }
    }

    protected void onEventMainThread(LookCheckStatusEntity entity) {
        classId = entity.getKey() + "";
        adapter = null;
        page = 1;
        initAlbumCheckList();
    }


    private void initAlbumCheckList() {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("uId", App.app.getData("uId"));
        if (!TextUtils.isEmpty(classId)) {
            params.put("abClassId", classId);//班级ID
        }
        params.put("auditingState", "2");//默认全部  0待审批  1已审批  2删除审核
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.ALBUMCHECK + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.ALBUMCHECK, "ALBUMCHECK", params, new MyLoadCallback());
    }

    private class MyLoadCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("加载失败");
            v.my_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.my_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            Log.i("Tag","response="+response);
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<AlbumModel> listBean = JsonUtils.getBean(response, CommonListBean.class, AlbumModel.class);
                if (listBean.getRows() != null && listBean.getRows().size() > 0) {
                    if (adapter == null) {
                        adapter = new AlbumCheckAdapter(listBean.getRows(), getActivity(), 2);
                        v.my_act_list.setAdapter(adapter);
                    }else{
                        adapter.addDataListAtLast(listBean.getRows());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (page == 1) {
                        adapter = new AlbumCheckAdapter(listBean.getRows(), getActivity(), 2);
                        v.my_act_list.setAdapter(adapter);
                        v.checkbox_all.setChecked(false);
                    }
                }
            }
            v.my_act_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.my_act_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.Approve:
                isApproveList("1");
                break;
            case R.id.noApprove:
                isApproveList("2");
                break;
        }
    }

    private void isApproveList(String isapprove) {
        showProgressDialog(getActivity());
        StringBuffer listcheckid = new StringBuffer();
        for (AlbumModel model : adapter.getDataList()) {
            if (model.getChecked() == 1 && !TextUtils.isEmpty(model.getAbId() + "")) {
                listcheckid.append(model.getAbId() + ",");
            }
        }
        if(listcheckid.length()==0){
            showToast("请先选择要审核的内容");
            dismissProgressDialog();
            return;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("uId", App.app.getData("uId"));
        params.put("abIds", listcheckid.toString());//勾选集合id
        params.put("auditingState", isapprove);// 1通过  2不通过
        params.put("auditingType", "1");//0相册审核  1相册删除审核
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.ALBUMCHECKAPPROVE + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.ALBUMCHECKAPPROVE, "ALBUMCHECKAPPROVE", params, new MyApproveCallback());
    }

    private class MyApproveCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast("请求超时");
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (bean.isSuccess()) {
                    showToast("操作成功");
                    adapter = null;
                    page = 1;
                    initAlbumCheckList();
                }
            }
        }
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
