package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.ActDetailsActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class HomeActivityAdapter extends CommonAdapter<ActModel> {
    public HomeActivityAdapter(List<ActModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, final ActModel data) {
        if (!TextUtils.isEmpty(data.getAcTitle())) {
            holder.setText(R.id.parent_home_list_name, data.getAcTitle());
        }
        if (!TextUtils.isEmpty(data.getAcMainPic())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + data.getAcMainPic(), (ImageView) holder.getView(R.id.parent_home_list_img), LoadImage.getDefaultOptions());
        }
        if (!TextUtils.isEmpty(data.getAcEndSigeUpTime())) {
            holder.setText(R.id.parent_home_time, data.getAcEndSigeUpTime());
        }
        switch (data.getActivityState()) {
            case 0:
                holder.setImageResource(R.id.parent_home_list_state, R.mipmap.home_activity_bm);
                break;
            case 1:
                break;
            case 2:
                break;
            case 3:
                holder.setImageResource(R.id.parent_home_list_state, R.mipmap.home_activity_js);
                break;
        }
        holder.setText(R.id.parent_home_num, (data.getAcCount() - data.getAcSurCount()) + "人已报名");
        holder.setOnClickListener(R.id.home_act_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActDetailsActivity.start(context, data);
            }
        });
    }
}
