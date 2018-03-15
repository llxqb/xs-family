package com.bhxx.xs_family.fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.ActionCheckAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.entity.AlbumCheckOneEntity;
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

public class ActionCheckItemOneFragment extends BaseFragment {
    private ActionCheckAdapter adapter;
    private boolean nolistener = true;
    private int page = 1;
    private String classId;

    @InjectAll
    private Views v;

    private class Views {
        PullToRefreshLayout my_act_pull;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView Approve, noApprove;
        CheckBox checkbox_all;
        PullableListView my_act_list;
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
                    for (ActModel model : adapter.getDataList()) {
                        model.setChecked(1);
                    }
                } else {
                    for (ActModel model : adapter.getDataList()) {
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
                initActionCheckList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initActionCheckList();
            }
        });
        initActionCheckList();
    }

    protected void onEventMainThread(AlbumCheckOneEntity entity) {
        switch (entity.getKey()) {
            case AlbumCheckOneEntity.ALBUM_CHECKED:
                v.checkbox_all.setChecked(true);
                break;
            case AlbumCheckOneEntity.ALBUM_NOT_CHECKED:
                nolistener = false;
                v.checkbox_all.setChecked(false);
                break;
        }
    }

    protected void onEventMainThread(LookCheckStatusEntity entity) {
        classId = entity.getKey() + "";
        adapter = null;
        page = 1;
        initActionCheckList();
    }

    private void initActionCheckList() {
        showProgressDialog(getActivity());
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("uId", App.app.getData("uId"));
        if (!TextUtils.isEmpty(classId)) {
            params.put("acClassId", classId);//班级ID
        }
        params.put("auditingState", "0");//默认全部  0待审批  1已审批  2删除审核
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.ACTIONCHECK + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.ACTIONCHECK, "ACTIONCHECK", params, new MyLoadCallback());
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
                CommonListBean<ActModel> listBean = JsonUtils.getBean(response, CommonListBean.class, ActModel.class);
                if (listBean.getRows() != null && listBean.getRows().size() > 0) {
                    if (adapter == null) {
                        adapter = new ActionCheckAdapter(listBean.getRows(), getActivity(), R.layout.action_check_item, 0);
                        v.my_act_list.setAdapter(adapter);
                    } else {
                        adapter.addDataListAtLast(listBean.getRows());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    showToast("暂无数据");
                    if (page == 1) {
                        adapter = new ActionCheckAdapter(listBean.getRows(), getActivity(), R.layout.action_check_item, 0);
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
                isApproveList("1", "");
                break;
            case R.id.noApprove:
                showDialog();
                break;
        }
    }

    private void isApproveList(String isapprove, String nocheckreson) {
        showProgressDialog(getActivity());
        StringBuffer listcheckid = new StringBuffer();
        for (ActModel model : adapter.getDataList()) {
            if (model.getChecked() == 1 && !TextUtils.isEmpty(model.getAcId() + "")) {
                listcheckid.append(model.getAcId() + ",");
            }
        }
        if (listcheckid.length() == 0) {
            showToast("请先选择要审核的内容");
            dismissProgressDialog();
            return;
        }
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("uId", App.app.getData("uId"));
        params.put("acIds", listcheckid.toString());//勾选集合id
        params.put("auditingState", isapprove);// 1通过  2不通过
        if (!TextUtils.isEmpty(nocheckreson)) {
            params.put("auditingRemark", nocheckreson);//不审核理由
        }
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.ACTIONCHECKAPPROVE + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.ACTIONCHECKAPPROVE, "ACTIONCHECKAPPROVE", params, new MyApproveCallback());
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
                    showToast("审核通过");
                    adapter = null;
                    page = 1;
                    initActionCheckList();
                }
            }
        }
    }

    private void showDialog() {
        // 获取Dialog布局
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.nocheckreson_dialog, null);
        WindowManager windowManager = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth((int) 0.8 * display.getWidth());
        view.setMinimumHeight(display.getHeight() / 2);

        // 获取自定义Dialog布局中的控件
        final EditText tvNoCheckReason = (EditText) view.findViewById(R.id.tvNoCheckReason);
        Button sure = (Button) view.findViewById(R.id.sure);
        Button cancel = (Button) view.findViewById(R.id.cancel);

        // 定义Dialog布局和参数
        final Dialog dialog = new Dialog(getActivity(), R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        dialog.show();


        sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(tvNoCheckReason.getText().toString())) {
                    showToast("请填写理由");
                } else {
                    isApproveList("2", tvNoCheckReason.getText().toString());
                    dialog.dismiss();
                }

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
