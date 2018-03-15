package com.bhxx.xs_family.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.DateDialog.widget.DatePicker;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.MoveoaQjAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.LeaveModel;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;

import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;

@InjectLayer(R.layout.activity_moveoa_qj)
public class MoveoaQjActivity extends BasicActivity {
    MoveoaQjAdapter qjAdapter;
    private int page = 1;
    private String months;
    private static int broadcast = 0;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView moveoa_qj_back, moveoa_qj_add;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView moveoa_qj_lookmonths, moveoa_qj_currentMonths;
        PullToRefreshLayout moveoa_qj_pull;
        PullableListView moveoa_qj_list;

    }

    private void method() {
        v.moveoa_qj_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout moveoa_qj_details = (LinearLayout) view.findViewById(R.id.moveoa_qj_details);
                ImageView moveoa_qj_right = (ImageView) view.findViewById(R.id.moveoa_qj_right);
                if ((boolean) qjAdapter.getMapStatus().get(qjAdapter.getDataList().get(position).getLeId())) {
                    moveoa_qj_details.setVisibility(View.GONE);
                    qjAdapter.getMapStatus().put(qjAdapter.getDataList().get(position).getLeId(), false);
                    moveoa_qj_right.setImageResource(R.mipmap.user_workdrop_down2);
                } else {
                    moveoa_qj_details.setVisibility(View.VISIBLE);
                    qjAdapter.getMapStatus().put(qjAdapter.getDataList().get(position).getLeId(), true);
                    moveoa_qj_right.setImageResource(R.mipmap.user_workdropdown);
                }
            }
        });
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(MoveoaQjActivity.this);
        method();
        v.moveoa_qj_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                qjAdapter = null;
                initQjtList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initQjtList();
            }
        });
        initQjtList();
    }

    public void initQjtList() {
        showProgressDialog(MoveoaQjActivity.this);
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("leTeacherId", App.app.getData("uId"));
        if (!TextUtils.isEmpty(months)) {
            params.put("searchMoth", months);
        }
        params.put("page", page + "");
        String token = TokenUtils.getInstance().configParams(GlobalValues.MOVEOA_QJ + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.MOVEOA_QJ, "MOVEOA_QJ", params, new MyStringcallback());
    }


    private class MyStringcallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("请求失败");
            dismissProgressDialog();
            v.moveoa_qj_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.moveoa_qj_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<LeaveModel> listbean = JsonUtils.getBean(response, CommonListBean.class, LeaveModel.class);
                if (listbean.getRows() != null && listbean.getRows().size() > 0) {
                    if (qjAdapter == null) {
                        qjAdapter = new MoveoaQjAdapter(listbean.getRows(), MoveoaQjActivity.this);
                        v.moveoa_qj_list.setAdapter(qjAdapter);
                    } else {
                        qjAdapter.addDataListAtLast(listbean.getRows());
                        qjAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (qjAdapter == null) {
                        //选择 月份
                        qjAdapter = new MoveoaQjAdapter(listbean.getRows(), MoveoaQjActivity.this);
                        v.moveoa_qj_list.setAdapter(qjAdapter);
                        showToast("无更多数据");
                    } else {
                        //下拉刷新
                    }
                }
            }

            v.moveoa_qj_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.moveoa_qj_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }


    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.moveoa_qj_back:
                finish();
                break;
            case R.id.moveoa_qj_add:
                PublishMoveoaQjActivity.start(MoveoaQjActivity.this);
                break;
            case R.id.moveoa_qj_currentMonths:
                showProgressDialog(this);
                qjAdapter = null;
                String text = CheckUtils.getCurrentDay();
                months = text.substring(0, text.length() - 3);
                initQjtList();
                break;
            case R.id.moveoa_qj_lookmonths:
                dialogMonths();
                break;
        }
    }


    public static void start(Context context) {
        Intent intent = new Intent(context, MoveoaQjActivity.class);
        context.startActivity(intent);
    }

    private void dialogMonths() {
        DatePicker picker = new DatePicker(MoveoaQjActivity.this, DatePicker.YEAR_MONTH);
        picker.setRange(2016, 2025);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                months = year + "-" + month;
                qjAdapter = null;
                //按月查询
                page = 1;
                showProgressDialog(MoveoaQjActivity.this);
                initQjtList();
            }
        });
        picker.show();


    }

    @Override
    protected void onResume() {
        if (broadcast == 1) {
            qjAdapter = null;
            broadcast = 0;
            page = 1;
            initQjtList();
        }
        super.onResume();
    }

    public static void flashUI() {
        broadcast = 1;
    }


}
