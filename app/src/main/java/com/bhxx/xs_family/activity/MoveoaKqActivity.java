package com.bhxx.xs_family.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.MoveoaFyAdapter;
import com.bhxx.xs_family.adapter.MoveoaKqAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CheckworkModel;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.ReimbursementModel;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.PullToRefreshLayout;
import com.bhxx.xs_family.views.PullableListView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_moveoa_kq)
public class MoveoaKqActivity extends BasicActivity {
    private int page = 1;
    MoveoaKqAdapter adapter;
    private String searchDate;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView moveoa_kq_back, moveoa_kq_calendars;
        PullToRefreshLayout moveoa_kq_pull;
        PullableListView moveoa_kq_list;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(MoveoaKqActivity.this);
        initActList();
        v.moveoa_kq_pull.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                page = 1;
                adapter = null;
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
        showProgressDialog(MoveoaKqActivity.this);
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("page", page + "");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("cwTeacherId", App.app.getData("uId"));
        if (!TextUtils.isEmpty(searchDate)) {
            params.put("searchDate", searchDate);
        }
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.TEACHER_MOVEOA_KQ + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.TEACHER_MOVEOA_KQ, "TEACHER_MOVEOA_KQ", params, new MyStringCallback());
    }


    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("请求失败");
            v.moveoa_kq_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.moveoa_kq_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonListBean<CheckworkModel> listbean = JsonUtils.getBean(response, CommonListBean.class, CheckworkModel.class);
                if (listbean.getRows() != null && listbean.getRows().size() > 0) {
                    if (adapter == null) {
                        adapter = new MoveoaKqAdapter(listbean.getRows(), MoveoaKqActivity.this, R.layout.moveoakq_item);
                        v.moveoa_kq_list.setAdapter(adapter);
                        showToast("加载成功");
                    } else {
                        adapter.addDataListAtLast(listbean.getRows());
                        adapter.notifyDataSetChanged();
                    }
                } else {
                    if (adapter == null) {
                        //选择 月份
                        adapter = new MoveoaKqAdapter(listbean.getRows(), MoveoaKqActivity.this, R.layout.moveoakq_item);
                        v.moveoa_kq_list.setAdapter(adapter);
                        showToast("无更多数据");
                    } else {
                        //下拉刷新
                        showToast("无更多数据");
                    }
                }
            }
            v.moveoa_kq_pull.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            v.moveoa_kq_pull.refreshFinish(PullToRefreshLayout.SUCCEED);
        }
    }

    @Override
    protected void click(View view) {

        switch (view.getId()) {
            case R.id.moveoa_kq_back:
                finish();
                break;
            case R.id.moveoa_kq_calendars:
                showCalendarDialog();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MoveoaKqActivity.class);
        context.startActivity(intent);
    }

    /**
     * 弹出日历
     */
    private void showCalendarDialog() {
        View view = LayoutInflater.from(MoveoaKqActivity.this).inflate(R.layout.food_menu_calendar, null);
        final Dialog log = new Dialog(MoveoaKqActivity.this, R.style.transparentFrameWindowStyle);
        MaterialCalendarView calendarView = (MaterialCalendarView) view.findViewById(R.id.calendarView);
        String[] week = {"日", "一", "二", "三", "四", "五", "六"};
        //设置头部一周的字
        calendarView.setWeekDayLabels(week);
        //设置点击的日期背景（默认为material绿风格）
        calendarView.setSelectionColor(getResources().getColor(R.color.app_grey));
        //获取当前日期 ，设置默认选中的当天日期
        calendarView.setSelectedDate(new Date(System.currentTimeMillis()));
        log.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = log.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示位置
        log.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        log.setCanceledOnTouchOutside(false);
        log.show();
        int measureWidth = getResources().getDisplayMetrics().widthPixels * 4 / 5;
        window.setLayout(measureWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                DateFormat FORMATTER = SimpleDateFormat.getDateInstance();
                searchDate = FORMATTER.format(date.getDate());
                searchDate = searchDate.substring(0, searchDate.length() - 1);
                searchDate = searchDate.replace("年", "-");
                searchDate = searchDate.replace("月", "-");
                page = 1;
                adapter = null;
                initActList();
                log.dismiss();
            }
        });
    }
}
