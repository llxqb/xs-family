package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.ScheduleModel;

import java.util.List;

public class ScheduleAdapter extends CommonAdapter<ScheduleModel> {
    public ScheduleAdapter(List<ScheduleModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, ScheduleModel data) {
        if (!TextUtils.isEmpty(data.getSdTime())) {
            holder.setText(R.id.rest_schedule_time,data.getSdTime());
        }

        if (!TextUtils.isEmpty(data.getSdContent())) {
            holder.setText(R.id.rest_schedule_project,data.getSdContent());
        }
    }
}
