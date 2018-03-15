
package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.DynamicModel;
import com.bhxx.xs_family.entity.DynamicCollectEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.HorizontalListView;
import com.bhxx.xs_family.views.MyListView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_dynamic_details)
public class DynamicDetailsActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private DynamicModel dynamic;
    private boolean isCollect;
    private ArrayList<String> picList = new ArrayList<>();

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView dynamic_details_back, dynamic_details_coll;
        TextView dynamic_details_title, dynamic_details_time, dynamic_details_look_num, dynamic_details_desc;
        HorizontalListView dynamic_details_list_img;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(DynamicDetailsActivity.this);
        if (!App.app.getData("uRole").equals("2")) {
            v.dynamic_details_coll.setVisibility(View.GONE);
        }
        if (getIntent() != null) {
            dynamic = (DynamicModel) getIntent().getSerializableExtra("dynamic");
            if (dynamic != null) {
                initView(dynamic);
            }
        }
        v.dynamic_details_list_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageDisplayActivity.start(DynamicDetailsActivity.this, picList, position);
            }
        });
    }

    private void initView(DynamicModel data) {
        if (!TextUtils.isEmpty(data.getDcMainPic())) {
            picList.add(data.getDcMainPic());
        }
        if (!TextUtils.isEmpty(data.getDcTitle())) {
            v.dynamic_details_title.setText(data.getDcTitle());
        }
        if (!TextUtils.isEmpty(data.getDcCreateTime())) {
            v.dynamic_details_time.setText(data.getDcCreateTime());
        }
        v.dynamic_details_look_num.setText(data.getDcLookCount() + "");
        if (!TextUtils.isEmpty(data.getDcDesction())) {
            v.dynamic_details_desc.setText(data.getDcDesction());
        }
        if (data.getDcIsCollect() == 0) {
            v.dynamic_details_coll.setImageResource(R.mipmap.rightdet_collect);
            isCollect = false;
        } else {
            v.dynamic_details_coll.setImageResource(R.mipmap.rightdet_collect_pre);
            isCollect = true;
        }

        if (!TextUtils.isEmpty(data.getDcPics())) {
            String[] pics = data.getDcPics().split(";");

            for (String pic : pics) {
                picList.add(pic);
            }
            v.dynamic_details_list_img.setAdapter(new DynamicPicsAdapter(picList, DynamicDetailsActivity.this, R.layout.dynamic_details_img_item));
        }
    }

    /**
     * 跳转至班级动态详情
     *
     * @param context
     * @param dynamic
     */
    public static void start(Context context, DynamicModel dynamic) {
        Intent intent = new Intent(context, DynamicDetailsActivity.class);
        intent.putExtra("dynamic", dynamic);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.dynamic_details_back:
                finish();
                break;
            case R.id.dynamic_details_coll:
                collect();
                break;
        }
    }

    private void collect() {
        showProgressDialog(DynamicDetailsActivity.this);
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("clHomeLeaderId", App.app.getData("uId"));
        params.put("clCollectedId", dynamic.getDcId() + "");
        params.put("clCollectedType", GlobalValues.COLLECT_DYNAMIC);
        String token = TokenUtils.getInstance().configParams(GlobalValues.SOME_COLLECT_CANClE + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.SOME_COLLECT_CANClE, "COLLECT_DYNAMIC", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast("操作失败");
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (bean != null && bean.isSuccess()) {
                    if (isCollect == false) {
                        v.dynamic_details_coll.setImageResource(R.mipmap.rightdet_collect_pre);
                        showToast("收藏成功");
                        isCollect = true;
                        EventBus.getDefault().post(new DynamicCollectEntity(dynamic.getDcId(), DynamicCollectEntity.COLLECT));
                    } else {
                        v.dynamic_details_coll.setImageResource(R.mipmap.rightdet_collect);
                        showToast("取消收藏");
                        isCollect = false;
                        EventBus.getDefault().post(new DynamicCollectEntity(dynamic.getDcId(), DynamicCollectEntity.CANCEL_COLLECT));
                    }
                }
            } else {
                showToast("操作失败");
            }
        }
    }

    private class DynamicPicsAdapter extends CommonAdapter<String> {
        @Override
        public void convert(ViewHolder holder, String data) {
            if (!TextUtils.isEmpty(data)) {
                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + data, (ImageView) holder.getView(R.id.dynamic_details_item_img), LoadImage.getDefaultOptions());
            }
        }

        public DynamicPicsAdapter(List<String> dataList, Context context, int layoutId) {
            super(dataList, context, layoutId);
        }
    }
}
