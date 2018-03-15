package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.DynamicDetailsActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.DynamicModel;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class DynamicAdapter extends CommonAdapter<DynamicModel> {
    public DynamicAdapter(List<DynamicModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final DynamicModel data) {
        if (!TextUtils.isEmpty(data.getDcTitle())) {
            holder.setText(R.id.class_dynamic_title, data.getDcTitle());
        }
        if (!TextUtils.isEmpty(data.getDcMainPic())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + data.getDcMainPic(), (ImageView) holder.getView(R.id.class_dynamic_img), LoadImage.getDefaultOptions());
        }
        if (!TextUtils.isEmpty(data.getDcClassName())) {
            holder.setText(R.id.class_dynamic_type, data.getDcClassName());
        }
        if (!TextUtils.isEmpty(data.getDcPublisherName())) {
            holder.setText(R.id.class_dynamic_teacher, data.getDcPublisherName());
        }
        if (!TextUtils.isEmpty(data.getDcCreateTime())) {
            holder.setText(R.id.class_dynamic_time, data.getDcCreateTime());
        }
        holder.setOnClickListener(R.id.dynamic_item_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DynamicDetailsActivity.start(context, data);
            }
        });
    }
}
