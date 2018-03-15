package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ImageView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class MyActAdapter extends CommonAdapter<ActModel> {
    int type ;
    public MyActAdapter(List<ActModel> dataList, Context context, int layoutId,int type) {
        super(dataList, context, layoutId);
        this.type = type;
    }

    @Override
    public void convert(ViewHolder holder, ActModel data) {
        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + data.getAcMainPic(), (ImageView) holder.getView(R.id.my_act_item_pic), LoadImage.getDefaultOptions());
        if (!TextUtils.isEmpty(data.getAcTitle())) {
            holder.setText(R.id.my_act_item_title, data.getAcTitle());
        }
        if (!TextUtils.isEmpty(data.getAcDesction())) {
            holder.setText(R.id.my_act_item_description, data.getAcDesction());
        }
        //活动审批状态0已提交未审批   1审批通过  2审批不通过
        switch (type) {//data.getActivityState()
            case 0:
                holder.setText(R.id.my_act_item_type, "报名中");
                holder.setBackgroundRes(R.id.my_act_item_type, R.drawable.round_yellow_bg);
                holder.setTextColorRes(R.id.my_act_item_type, R.color.yellow);
                break;
            case 12:
                holder.setText(R.id.my_act_item_type, "已结束");
                holder.setBackgroundRes(R.id.my_act_item_type, R.drawable.round_yellow_bg);
                holder.setTextColorRes(R.id.my_act_item_type, R.color.yellow);
                break;
            case 2:
                holder.setText(R.id.my_act_item_type, "审核中");
                holder.setBackgroundRes(R.id.my_act_item_type, R.drawable.round_yellow_bg);
                holder.setTextColorRes(R.id.my_act_item_type, R.color.yellow);
                break;
            case 3:
                holder.setText(R.id.my_act_item_type, "未通过");
                holder.setBackgroundRes(R.id.my_act_item_type, R.drawable.round_yellow_bg);
                holder.setTextColorRes(R.id.my_act_item_type, R.color.yellow);
                break;
        }
        holder.setText(R.id.my_act_item_num, (data.getAcCount() - data.getAcSurCount()) + "人");
        if (!TextUtils.isEmpty(data.getAcEndSigeUpTime())) {
            holder.setText(R.id.my_act_item_time, "截止日期：" + data.getAcEndSigeUpTime());
        }
    }
}
