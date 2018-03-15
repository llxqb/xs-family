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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.MoveoaWpAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonListBean;
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

@InjectLayer(R.layout.activity_move_oa_wp)
public class MoveOaWpActivity extends BasicActivity {
    MoveoaWpAdapter wpAdapter;
    int page=1;
    private String months;
    private static int broadcast =0;
    @InjectAll
    private  Views v;
    private class Views{
        @InjectBinder(listeners = {OnClick.class},method = "click")
        ImageView moveoa_wp_back,moveoa_wp_add;
        @InjectBinder(listeners = {OnClick.class},method = "click")
        RelativeLayout moveoa_wp_lookmonths;
        @InjectBinder(listeners = {OnClick.class},method = "click")
        TextView moveoa_wp_currentMonths;
        PullToRefreshLayout moveoa_wp_pull;
        PullableListView moveoa_wp_list;

    }
    @Override
    protected void init() {
        ActivityCollector.addActivity(MoveOaWpActivity.this);
        initWptList();
        v.moveoa_wp_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                wpAdapter = null;
                initWptList();
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
                page = page + 1;
                initWptList();
            }
        });

        v.moveoa_wp_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RelativeLayout moveoa_wp_details = (RelativeLayout) view.findViewById(R.id.moveoa_wp_details);
                ImageView moveoa_wp_right = (ImageView) view.findViewById(R.id.moveoa_wp_right);
               if((boolean)wpAdapter.getMapStatus().get(wpAdapter.getDataList().get(position).getTtId())) {
                    moveoa_wp_details.setVisibility(View.GONE);
                    wpAdapter.getMapStatus().put(wpAdapter.getDataList().get(position).getTtId(),false);
                   moveoa_wp_right.setImageResource(R.mipmap.user_workdrop_down2);
                }else{
                   moveoa_wp_details.setVisibility(View.VISIBLE);
                   wpAdapter.getMapStatus().put(wpAdapter.getDataList().get(position).getTtId(),true);
                   moveoa_wp_right.setImageResource(R.mipmap.user_workdropdown);
               }
            }
        });
    }

    private void initWptList(){
        showProgressDialog(MoveOaWpActivity.this);
        LinkedHashMap<String,String> params = new LinkedHashMap<String, String>();
        params.put("ttTeacherId", App.app.getData("uId"));
        params.put("page",page+"");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        if(!TextUtils.isEmpty(months)){
            params.put("searchMoth",months);
        }
       String taoken =  TokenUtils.getInstance().configParams(GlobalValues.MOVEOA_WP+"?",params);
        params.put("taoken",taoken);
        MyOkHttp.postMap(GlobalValues.MOVEOA_WP,"MOVEOA_WP",params,new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback{
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("请求失败");
            v.moveoa_wp_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.moveoa_wp_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<TeachingtoolModel> listbean = JsonUtils.getBean(response, CommonListBean.class, TeachingtoolModel.class);
                if (listbean.getRows() != null && listbean.getRows().size() > 0) {
                    if (wpAdapter == null) {
                        wpAdapter = new MoveoaWpAdapter(listbean.getRows(), MoveOaWpActivity.this);
                        v.moveoa_wp_list.setAdapter(wpAdapter);
                        showToast("加载成功");
                    } else {
                        wpAdapter.addDataListAtLast(listbean.getRows());
                        wpAdapter.notifyDataSetChanged();
                    }
                }else{
                    if(wpAdapter==null){
                        wpAdapter = new MoveoaWpAdapter(listbean.getRows(), MoveOaWpActivity.this);
                        v.moveoa_wp_list.setAdapter(wpAdapter);
                        showToast("无更多数据");
                    }else{
                        showToast("无更多数据");
                    }
                }
            }
            v.moveoa_wp_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.moveoa_wp_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }



    @Override
    protected void click(View view) {

        switch (view.getId()){
            case R.id.moveoa_wp_back:
                finish();
                break;
            case R.id.moveoa_wp_add:
                PublishMoveoaWpActivity.start(MoveOaWpActivity.this);
                break;
            case R.id.moveoa_wp_lookmonths:
                dialogMonths();
                break;
            case R.id.moveoa_wp_currentMonths:
                wpAdapter = null;
                showProgressDialog(this);
                String text = CheckUtils.getCurrentDay();
                months = text.substring(0, text.length() - 3);
                initWptList();
                break;
        }
    }

    public static  void start(Context context){
        Intent intent = new Intent(context,MoveOaWpActivity.class);
        context.startActivity(intent);
    }


    private void dialogMonths() {
        com.bhxx.xs_family.DateDialog.widget.DatePicker picker = new com.bhxx.xs_family.DateDialog.widget.DatePicker(MoveOaWpActivity.this, com.bhxx.xs_family.DateDialog.widget.DatePicker.YEAR_MONTH);
        picker.setRange(2016, 2025);
        picker.setOnDatePickListener(new com.bhxx.xs_family.DateDialog.widget.DatePicker.OnYearMonthPickListener() {
            @Override
            public void onDatePicked(String year, String month) {
                Log.i("Tag",year + "-" + month);
                months = year + "-" + month;
                wpAdapter = null;
                //按月查询
                page = 1;
                showProgressDialog(MoveOaWpActivity.this);
                initWptList();
            }
        });
        picker.show();
    }



    @Override
    protected void onResume() {
        if(broadcast==1){
            broadcast = 0;
            wpAdapter = null;
            page =1;
            initWptList();
        }
        super.onResume();
    }

    public static void flashUI(){
        broadcast = 1;
    }

}
