package com.bhxx.xs_family.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.bhxx.xs_family.adapter.MoveoaFyAdapter;
import com.bhxx.xs_family.adapter.MoveoaWpAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.ReimbursementModel;
import com.bhxx.xs_family.beans.TeachingtoolModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_move_oa_fy)
public class MoveOaFyActivity extends BasicActivity {
    private List<ReimbursementModel> list ;
    private MoveoaFyAdapter fyAdapter;
    private int page = 1;
    private String months;
    private static int broadcast=0;
    @InjectAll
    private  Views v;
    private class Views{
        @InjectBinder(listeners = {OnClick.class},method = "click")
        ImageView moveoa_fy_back,moveoa_fy_add;
        @InjectBinder(listeners = {OnClick.class},method = "click")
        RelativeLayout moveoa_fy_lookmonths;
        PullToRefreshLayout moveoa_fy_pull;
        PullableListView moveoa_fy_list;
        @InjectBinder(listeners = {OnClick.class},method = "click")
        TextView moveoa_fy_currentMonths;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        initFyList();

        v.moveoa_fy_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                fyAdapter = null;
                initFyList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
                page = page + 1;
                initFyList();
            }
        });

        v.moveoa_fy_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout moveoa_fy_details = (LinearLayout) view.findViewById(R.id.moveoa_fy_details);
                ImageView moveoa_fy_right = (ImageView) view.findViewById(R.id.moveoa_fy_right);
                if((boolean)fyAdapter.getMapStatus().get(fyAdapter.getDataList().get(position).getRbId())){
                    moveoa_fy_details.setVisibility(View.GONE);
                    fyAdapter.getMapStatus().put(fyAdapter.getDataList().get(position).getRbId(),false);
                    moveoa_fy_right.setImageResource(R.mipmap.user_workdrop_down2);
                }else{
                    moveoa_fy_details.setVisibility(View.VISIBLE);
                    fyAdapter.getMapStatus().put(fyAdapter.getDataList().get(position).getRbId(),true);
                    moveoa_fy_right.setImageResource(R.mipmap.user_workdropdown);
                }
            }
        });

    }

    private void initFyList(){
        showProgressDialog(MoveOaFyActivity.this);
        LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
        params.put("rbTeacherId", App.app.getData("uId"));
        params.put("page",page+"");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        if(!TextUtils.isEmpty(months)){
            params.put("searchMoth",months);
        }
        String taoken =  TokenUtils.getInstance().configParams(GlobalValues.MOVEOA_FY+"?",params);
        params.put("taoken",taoken);
        MyOkHttp.postMap(GlobalValues.MOVEOA_FY,"MOVEOA_FY",params,new MyStringcallback());
    }

    private class MyStringcallback extends CommonCallback{
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("请求失败");
            dismissProgressDialog();
            v.moveoa_fy_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.moveoa_fy_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<ReimbursementModel> listbean = JsonUtils.getBean(response, CommonListBean.class, ReimbursementModel.class);
                if (listbean.getRows() != null && listbean.getRows().size() > 0) {
                    if (fyAdapter == null) {
                        fyAdapter = new MoveoaFyAdapter(listbean.getRows(), MoveOaFyActivity.this);
                        v.moveoa_fy_list.setAdapter(fyAdapter);
                        showToast("加载成功");
                    } else {
                        fyAdapter.addDataListAtLast(listbean.getRows());
                        fyAdapter.notifyDataSetChanged();
                    }
                }else{
                    if(fyAdapter==null){
                        fyAdapter = new MoveoaFyAdapter(listbean.getRows(), MoveOaFyActivity.this);
                        v.moveoa_fy_list.setAdapter(fyAdapter);
                        showToast("无更多数据");
                    }else{
                        showToast("无更多数据");
                    }
                }
            }
            v.moveoa_fy_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.moveoa_fy_pull.refreshFinish(PullToRefreshLayout.SUCCEED);

        }
    }


    @Override
    protected void click(View view) {

        switch (view.getId()){
            case R.id.moveoa_fy_back:
                finish();
                break;
            case R.id.moveoa_fy_add:
                PublishMoveoaFyActivity.start(MoveOaFyActivity.this);
                break;
            case R.id.moveoa_fy_currentMonths:
                fyAdapter = null;
                showProgressDialog(this);
                String text = CheckUtils.getCurrentDay();
                months = text.substring(0, text.length() - 3);
                initFyList();
                break;
            case R.id.moveoa_fy_lookmonths:
                dialogMonths();
                break;
        }
    }

    public static void start(Context context){
        Intent intent = new Intent(context,MoveOaFyActivity.class);
        context.startActivity(intent);
    }


    private void dialogMonths() {
        DatePicker picker = new DatePicker(MoveOaFyActivity.this, DatePicker.YEAR_MONTH);
        picker.setRange(2016, 2025);
        picker.setOnDatePickListener(new DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                months = year + "-" + month;
                fyAdapter = null;
                //按月查询
                page = 1;
                showProgressDialog(MoveOaFyActivity.this);
                initFyList();
            }
        });
        picker.show();
    }


    public static void flashUI(){
        broadcast = 1;
    }

    @Override
    protected void onResume() {
        if(broadcast==1){
            fyAdapter = null;
            page = 1;
            broadcast = 0;
            initFyList();
        }
        super.onResume();
    }

}
