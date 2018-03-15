package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.ScheduleAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.beans.LeaveModel;
import com.bhxx.xs_family.beans.ScheduleModel;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import okhttp3.Call;

@InjectLayer(R.layout.activity_other_schedule)
public class OtherScheduleActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private TextView schedule_title;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView other_schedule_back;
        ListView other_schedule_list;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(OtherScheduleActivity.this);
        View head = LayoutInflater.from(OtherScheduleActivity.this).inflate(R.layout.rest_schedule_head, null);
        schedule_title = (TextView) head.findViewById(R.id.schedule_title);
        v.other_schedule_list.addHeaderView(head);
        initScheduleList();
    }

    private void initScheduleList() {
        showProgressDialog(OtherScheduleActivity.this, "加载中...");
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("classId", App.app.getData("classId"));
        params.put("parkId", App.app.getData("parkId"));
        String token = TokenUtils.getInstance().configParams(GlobalValues.REST_SCHEDULE + "?", params);
        params.put("taoken", token);

        MyOkHttp.postMap(GlobalValues.REST_SCHEDULE, "other", params, new MyStringCallback());
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.other_schedule_back:
                finish();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, OtherScheduleActivity.class);
        context.startActivity(intent);
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
//                            CommonListBean<ScheduleModel> listbean = JsonUtils.getBean(response, CommonListBean.class, ScheduleModel.class);
//                            if(ja_0 != null){
//                                schedule_title.setText("小班");
//                            }
//                            if(ja_1 != null){
//                                schedule_title.setText("中班");
//                            }
//                            if(ja_2 != null){
//                                schedule_title.setText("大班");
//                            }
//                            if(ja_3 != null){
//                                schedule_title.setText("托班");
//                            }
//                            ScheduleAdapter adapter = new ScheduleAdapter(listbean.getRows(), OtherScheduleActivity.this, R.layout.rest_schedule_item);
//                            v.other_schedule_list.setAdapter(adapter);

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
                                schedule_title.setText("小班");
                                ScheduleAdapter adapter = new ScheduleAdapter(data_0, OtherScheduleActivity.this, R.layout.rest_schedule_item);
                                v.other_schedule_list.setAdapter(adapter);
                                return;
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
                                schedule_title.setText("中班");
                                ScheduleAdapter adapter = new ScheduleAdapter(data_1, OtherScheduleActivity.this, R.layout.rest_schedule_item);
                                v.other_schedule_list.setAdapter(adapter);
                                return;
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
                                schedule_title.setText("大班");
                                ScheduleAdapter adapter = new ScheduleAdapter(data_2, OtherScheduleActivity.this, R.layout.rest_schedule_item);
                                v.other_schedule_list.setAdapter(adapter);
                                return;
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
                                schedule_title.setText("托班");
                                ScheduleAdapter adapter = new ScheduleAdapter(data_3, OtherScheduleActivity.this, R.layout.rest_schedule_item);
                                v.other_schedule_list.setAdapter(adapter);
                                return;
                            }
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
