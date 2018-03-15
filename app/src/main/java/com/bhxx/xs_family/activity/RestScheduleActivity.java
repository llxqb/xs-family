package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ScheduleModel;
import com.bhxx.xs_family.fragment.ScheduleItemFragment;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

/**
 * 作息表
 */
@InjectLayer(R.layout.activity_rest_schedule)
public class RestScheduleActivity extends BasicActivity {
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView rest_schedule_back;
        TabLayout rest_schedule_tablayout;
        ViewPager rest_schedule_pager;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(RestScheduleActivity.this);
        v.rest_schedule_pager.setOffscreenPageLimit(3);

        getScheduleDate();
    }

    /**
     * 作息表跳转
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, RestScheduleActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.rest_schedule_back:
                finish();
                break;
        }
    }

    private class MyPagerAdapter extends FragmentPagerAdapter {
        private final String[] titles = {"小班", "中班", "大班","托班"};
        private List<Fragment> frag = new ArrayList<Fragment>();

        public MyPagerAdapter(FragmentManager fm, ArrayList<ScheduleModel> data_0,
                              ArrayList<ScheduleModel> data_1, ArrayList<ScheduleModel> data_2,ArrayList<ScheduleModel> data_3) {
            super(fm);
            frag.add(ScheduleItemFragment.getInstance(data_0, 0));
            frag.add(ScheduleItemFragment.getInstance(data_1, 1));
            frag.add(ScheduleItemFragment.getInstance(data_2, 2));
            frag.add(ScheduleItemFragment.getInstance(data_3, 3));
        }

        @Override
        public Fragment getItem(int position) {
            return frag.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    /**
     * 获取作息表
     */
    private void getScheduleDate() {
        showProgressDialog(RestScheduleActivity.this, "加载中");
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("classId", App.app.getData("classId"));
        params.put("parkId", App.app.getData("parkId"));
        String token = TokenUtils.getInstance().configParams(GlobalValues.REST_SCHEDULE + "?", params);
        params.put("taoken", token);

        MyOkHttp.postMap(GlobalValues.REST_SCHEDULE, "QUERY_SCHEDULE", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast("加载失败");
        }

        @Override
        public void onResponse(String response, int id) {
            Log.i("Tag","response="+response);
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                try {
                    JSONObject jo = new JSONObject(response);
                    boolean success = jo.getBoolean("success");
                    String rows = jo.has("rows") ? jo.getString("rows") : "";
                    if (success) {
                        if (!TextUtils.isEmpty(rows)) {
                            JSONObject jo2 = new JSONObject(rows);
                            JSONArray ja_0 = jo2.has("0") ? jo2.getJSONArray("0") : null;
                            JSONArray ja_1 = jo2.has("1") ? jo2.getJSONArray("1") : null;
                            JSONArray ja_2 = jo2.has("2") ? jo2.getJSONArray("2") : null;
                            JSONArray ja_3 = jo2.has("3") ? jo2.getJSONArray("3") : null;
                            ArrayList<ScheduleModel> data_0 = new ArrayList<>();
                            ArrayList<ScheduleModel> data_1 = new ArrayList<>();
                            ArrayList<ScheduleModel> data_2 = new ArrayList<>();
                            ArrayList<ScheduleModel> data_3 = new ArrayList<>();


                            if (ja_0 != null && ja_0.length() > 0) {
                                for (int i = 0; i < ja_0.length(); i++) {
                                    JSONObject jo_0 = ja_0.getJSONObject(i);
                                    int sdId = jo_0.has("sdId") ? jo_0.getInt("sdId") : 0;
                                    int sdParkId = jo_0.has("sdParkId") ? jo_0.getInt("sdParkId") : 0;
                                    String sdTime = jo_0.has("sdTime") ? jo_0.getString("sdTime") : "";
                                    String sdContent = jo_0.has("sdContent") ? jo_0.getString("sdContent") : "";
                                    String sdClassType = jo_0.has("sdClassType") ? jo_0.getString("sdClassType") : "";
                                    String sdParkName = jo_0.has("sdParkName") ? jo_0.getString("sdParkName") : "";
                                    ScheduleModel model = new ScheduleModel(sdId, sdTime, sdContent, sdClassType, sdParkId, sdParkName);

                                    data_0.add(model);
                                }
                            }
                            if (ja_1 != null && ja_1.length() > 0) {
                                for (int i = 0; i < ja_1.length(); i++) {
                                    JSONObject jo_1 = ja_1.getJSONObject(i);
                                    int sdId = jo_1.has("sdId") ? jo_1.getInt("sdId") : 0;
                                    int sdParkId = jo_1.has("sdParkId") ? jo_1.getInt("sdParkId") : 0;
                                    String sdTime = jo_1.has("sdTime") ? jo_1.getString("sdTime") : "";
                                    String sdContent = jo_1.has("sdContent") ? jo_1.getString("sdContent") : "";
                                    String sdClassType = jo_1.has("sdClassType") ? jo_1.getString("sdClassType") : "";
                                    String sdParkName = jo_1.has("sdParkName") ? jo_1.getString("sdParkName") : "";
                                    ScheduleModel model = new ScheduleModel(sdId, sdTime, sdContent, sdClassType, sdParkId, sdParkName);

                                    data_1.add(model);
                                }
                            }
                            if (ja_2 != null && ja_2.length() > 0) {
                                for (int i = 0; i < ja_2.length(); i++) {
                                    JSONObject jo_2 = ja_2.getJSONObject(i);
                                    int sdId = jo_2.has("sdId") ? jo_2.getInt("sdId") : 2;
                                    int sdParkId = jo_2.has("sdParkId") ? jo_2.getInt("sdParkId") : 2;
                                    String sdTime = jo_2.has("sdTime") ? jo_2.getString("sdTime") : "";
                                    String sdContent = jo_2.has("sdContent") ? jo_2.getString("sdContent") : "";
                                    String sdClassType = jo_2.has("sdClassType") ? jo_2.getString("sdClassType") : "";
                                    String sdParkName = jo_2.has("sdParkName") ? jo_2.getString("sdParkName") : "";
                                    ScheduleModel model = new ScheduleModel(sdId, sdTime, sdContent, sdClassType, sdParkId, sdParkName);

                                    data_2.add(model);
                                }
                            }
                            if (ja_3 != null && ja_3.length() > 0) {
                                for (int i = 0; i < ja_3.length(); i++) {
                                    JSONObject jo_3 = ja_3.getJSONObject(i);
                                    int sdId = jo_3.has("sdId") ? jo_3.getInt("sdId") : 2;
                                    int sdParkId = jo_3.has("sdParkId") ? jo_3.getInt("sdParkId") : 2;
                                    String sdTime = jo_3.has("sdTime") ? jo_3.getString("sdTime") : "";
                                    String sdContent = jo_3.has("sdContent") ? jo_3.getString("sdContent") : "";
                                    String sdClassType = jo_3.has("sdClassType") ? jo_3.getString("sdClassType") : "";
                                    String sdParkName = jo_3.has("sdParkName") ? jo_3.getString("sdParkName") : "";
                                    ScheduleModel model = new ScheduleModel(sdId, sdTime, sdContent, sdClassType, sdParkId, sdParkName);

                                    data_3.add(model);
                                }
                            }
                            v.rest_schedule_pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager(), data_0, data_1, data_2,data_3));
                            v.rest_schedule_tablayout.setupWithViewPager(v.rest_schedule_pager);
                        }
                    } else {
                        showToast("暂无内容");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                showToast("加载失败");
            }
        }
    }
}
