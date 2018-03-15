package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.SystemMessageBean;

import java.util.List;
public class SysMessAdapter extends CommonAdapter<SystemMessageBean> {
    public SysMessAdapter(List<SystemMessageBean> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, SystemMessageBean data) {
        if (!TextUtils.isEmpty(data.getContent())) {
            holder.setText(R.id.sys_mess_content, data.getContent());
        }
    }
}
