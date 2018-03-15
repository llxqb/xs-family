package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.ClassDetailsActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.LittleClassModel;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

public class LittleClassAdapter extends CommonAdapter<LittleClassModel> {
    public LittleClassAdapter(List<LittleClassModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, final LittleClassModel data) {
        holder.setText(R.id.little_class_item_look_num, data.getPtLookCount() + "");
        holder.setText(R.id.little_class_item_collect_num, data.getPtCollectCount() + "");
        if (!TextUtils.isEmpty(data.getPtMainPic())) {

            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + data.getPtMainPic(), (ImageView) holder.getView(R.id.little_class_item_img), LoadImage.getDefaultOptions());
        }
        if (!TextUtils.isEmpty(data.getPtTitle())) {
            holder.setText(R.id.little_class_item_title, data.getPtTitle());
        }
        if (!TextUtils.isEmpty(data.getPtDesction())) {
            String xStr = Html.fromHtml(data.getPtDesction()).toString();
            holder.setText(R.id.little_class_item_description,xStr);
        }
        if (data.getPtIsCollect() == 1) {
            holder.setImageResource(R.id.little_class_is_collect, R.mipmap.album_collect_pre);
        } else {
            holder.setImageResource(R.id.little_class_is_collect, R.mipmap.album_collect);
        }
        holder.setOnClickListener(R.id.little_class_item_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClassDetailsActivity.start(context, data);
            }
        });
        holder.setOnClickListener(R.id.little_class_item_collect_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.app.getData("uRole").equals("2")) {
                    collect(data, (ImageView) (holder.getView(R.id.little_class_is_collect)));
                }else{
                    showToast("仅家长有收藏权限");
                }
            }
        });
    }

    /**
     * 收藏方法
     *
     * @param data
     * @param collectView
     */
    private void collect(final LittleClassModel data, final ImageView collectView) {
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("clHomeLeaderId", App.app.getData("uId"));
        params.put("clCollectedId", data.getPtId() + "");
        params.put("clCollectedType", GlobalValues.COLLECT_CLASS);
        String token = TokenUtils.getInstance().configParams(GlobalValues.SOME_COLLECT_CANClE + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.SOME_COLLECT_CANClE, "CLASS_COLLECT", params, new CommonCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToast("操作失败");
            }

            @Override
            public void onResponse(String response, int id) {
                if (!TextUtils.isEmpty(response)) {
                    CommonBean<String> bean = JsonUtils.getBean(response, CommonBean.class, String.class);
                    if (bean != null && bean.isSuccess()) {
                        if (data.getPtIsCollect() == 1) {
                            collectView.setImageResource(R.mipmap.album_collect);
                            setIsCollect(data.getPtId(), 0);
                            changeCollectNum(data.getPtId(), 0);
                            notifyDataSetChanged();
                        } else {
                            collectView.setImageResource(R.mipmap.album_collect_pre);
                            setIsCollect(data.getPtId(), 1);
                            changeCollectNum(data.getPtId(), 1);
                            notifyDataSetChanged();
                        }
                    } else {
                        showToast(bean.getMessage());
                    }

                }
            }
        });
    }

    /**
     * 设置数据
     *
     * @param classId   小课堂id
     * @param isCollect 0未收藏 1收藏
     */
    private void setIsCollect(int classId, int isCollect) {
        for (LittleClassModel littleClassModel : getDataList()) {
            if (littleClassModel.getPtId() == classId) {
                littleClassModel.setPtIsCollect(isCollect);
            }
        }
    }

    /**
     * 改变收藏数量
     *
     * @param classId
     * @param done
     */
    private void changeCollectNum(int classId, int done) {
        for (LittleClassModel littleClassModel : getDataList()) {
            if (littleClassModel.getPtId() == classId) {
                if (done == 0) {
                    littleClassModel.setPtCollectCount(littleClassModel.getPtCollectCount() - 1);
                } else {
                    littleClassModel.setPtCollectCount(littleClassModel.getPtCollectCount() + 1);
                }
            }
        }
    }
}
