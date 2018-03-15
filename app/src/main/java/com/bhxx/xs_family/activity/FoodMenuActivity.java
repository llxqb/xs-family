package com.bhxx.xs_family.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.FoodMenuAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.FoodRecipe;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.ExpandGridView;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

import okhttp3.Call;

/**
 * 食谱
 */
@InjectLayer(R.layout.activity_food_menu)
public class FoodMenuActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private FoodMenuAdapter adapter1,adapter2;
    private FoodRecipe foodRecipe = null;
    private final int LOAD = 0;
    private final int LIKE = 1;
    private final int NOLIKE = 2;
    private int likenumber = 0;
    private int nolikenumber = 0;
    private String dates = "";

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView food_menu_back, food_menu_calendar;
        ExpandGridView food_menu_gv,food_menu_gv_dianxin;
        TextView food_menu_desc,food_menu_desc_dianxin, food_against_num, food_like_num;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageButton food_against_bt, food_like_bt;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(FoodMenuActivity.this);
        dates = CheckUtils.getCurrentDay();
        update(dates);
    }


    /**
     * 获取食谱信息
     */
    private void update(String time) {
        showProgressDialog(FoodMenuActivity.this, "加载中...");
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("repTime", time);
        params.put("reParkId", App.app.getData("parkId"));
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.APP_RECIPE + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.APP_RECIPE, LOAD, "recipe", params, new MyStringCallback());

    }

    private class MyStringCallback extends CommonCallback {

        @Override
        public void onError(Call call, Exception e, int id) {
            switch (id) {
                case LOAD:
                    dismissProgressDialog();
                    break;
                case LIKE:
                    break;
                case NOLIKE:
                    break;
            }
        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case LOAD:
                    dismissProgressDialog();
                    CommonBean<FoodRecipe> bean = JsonUtils.getBean(response, CommonBean.class, FoodRecipe.class);
                    if (bean.isSuccess()) {
                        if (bean.getRows() != null) {
                            foodRecipe = bean.getRows();
                            if (adapter1 == null) {
                                adapter1 = new FoodMenuAdapter(bean.getRows().getAppDishes(), FoodMenuActivity.this, R.layout.foodmenu_grid_item);
                                v.food_menu_gv.setAdapter(adapter1);
                            }
                            if (adapter2 == null) {
                                adapter2 = new FoodMenuAdapter(bean.getRows().getAppDessert(), FoodMenuActivity.this, R.layout.foodmenu_grid_item);
                                v.food_menu_gv_dianxin.setAdapter(adapter2);
                            }




                            // 食谱点赞人数
                            if (!TextUtils.isEmpty(bean.getRows().getRepLikeCount() + "")) {
                                likenumber = bean.getRows().getRepLikeCount();
                                v.food_like_num.setText(likenumber + "");
                            } else {
                                v.food_like_num.setText("0");
                            }
                            // 食谱反对人数
                            if (!TextUtils.isEmpty(bean.getRows().getRepUnLikeCount() + "")) {
                                nolikenumber = bean.getRows().getRepUnLikeCount();
                                v.food_against_num.setText(nolikenumber + "");
                            } else {
                                v.food_against_num.setText("0");
                            }

                            if(bean.getRows().getAppDishes().size()==0){
                                v.food_menu_desc.setText("暂无午餐信息");
                            }else{
                                if (!TextUtils.isEmpty(bean.getRows().getRepDesction())) {
                                    v.food_menu_desc.setText(bean.getRows().getRepDesction());
                                }
                            }
                            if(bean.getRows().getAppDessert().size()==0){
                                v.food_menu_desc_dianxin.setText("暂无点心信息");
                            }else{
//                                if (!TextUtils.isEmpty(bean.getRows().getRepDesction())) {
//                                    v.food_menu_desc_dianxin.setText(bean.getRows().getRepDesction());
//                                }
                            }
                        } else {

                            adapter1 = null;
                            adapter1 = new FoodMenuAdapter(null, FoodMenuActivity.this, R.layout.foodmenu_grid_item);
                            v.food_menu_gv.setAdapter(adapter1);

                            adapter2 = null;
                            adapter2 = new FoodMenuAdapter(null, FoodMenuActivity.this, R.layout.foodmenu_grid_item);
                            v.food_menu_gv_dianxin.setAdapter(adapter2);
                        }
                    }
                    break;
                case LIKE:
                    CommonBean like = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (like != null) {
                        if (like.isSuccess()) {
                            update(dates);
                        }
                    }
                    break;
                case NOLIKE:
                    CommonBean nolike = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (nolike != null) {
                        if (nolike.isSuccess()) {
                            update(dates);
                        }
                    }
                    break;
            }
        }
    }


    /**
     * 跳转食谱页面
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, FoodMenuActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.food_menu_back:
                finish();
                break;
            case R.id.food_menu_calendar:
                showCalendarDialog();
                break;
            //食谱不喜欢
            case R.id.food_against_bt:
                if (!TextUtils.isEmpty(dates)) {
                    showProgressDialog(FoodMenuActivity.this, "加载中...");
                    if (foodRecipe != null) {
                        LinkedHashMap<String, String> params = new LinkedHashMap<>();
                        params.put("ulUserId", App.app.getData("uId"));
                        params.put("ulClickedId", foodRecipe.getRepId() + "");
                        params.put("ulType", "0");
                        String taoken = TokenUtils.getInstance().configParams(GlobalValues.APP_UNLIKE + "?", params);
                        params.put("taoken", taoken);
                        MyOkHttp.postMap(GlobalValues.APP_UNLIKE, NOLIKE, "umlike", params, new MyStringCallback());
                    }
                }
                break;
            //食谱喜欢
            case R.id.food_like_bt:
                if (!TextUtils.isEmpty(dates)) {
                    showProgressDialog(FoodMenuActivity.this, "加载中...");
                    if (foodRecipe != null) {
                        LinkedHashMap<String, String> params = new LinkedHashMap<>();
                        params.put("clUserId", App.app.getData("uId"));
                        params.put("clClickedId", foodRecipe.getRepId() + "");
                        params.put("clType", "1");
                        String taoken = TokenUtils.getInstance().configParams(GlobalValues.APP_CLICK + "?", params);
                        params.put("taoken", taoken);
                        MyOkHttp.postMap(GlobalValues.APP_CLICK, LIKE, "click", params, new MyStringCallback());
                    }
                }
                break;
        }
    }

    /**
     * 弹出日历
     */
    private void showCalendarDialog() {
        View view = LayoutInflater.from(FoodMenuActivity.this).inflate(R.layout.food_menu_calendar, null);
        final Dialog log = new Dialog(FoodMenuActivity.this, R.style.transparentFrameWindowStyle);
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
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                update(formatter.format(date.getDate()));
                dates = formatter.format(date.getDate());
                log.dismiss();
            }
        });


    }
}
