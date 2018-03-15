package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.UserHelpModel;

import java.util.List;

public class UserHelpAdapter extends CommonAdapter<UserHelpModel> {

    public UserHelpAdapter(List<UserHelpModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(final ViewHolder holder, UserHelpModel data) {
        if (!TextUtils.isEmpty(data.getUhQuestion())) {
            holder.setText(R.id.user_help_item_title, data.getUhQuestion());
        }
        if (!TextUtils.isEmpty(data.getUhAnswer())) {
            holder.setText(R.id.user_help_item_content, data.getUhAnswer());
        }

        holder.setOnClickListener(R.id.user_help_item_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (((TextView) holder.getView(R.id.user_help_item_content)).getVisibility() == View.GONE) {
                    holder.setVisible(R.id.user_help_item_content, true);
                } else if (((TextView) holder.getView(R.id.user_help_item_content)).getVisibility() == View.VISIBLE) {
                    holder.setVisible(R.id.user_help_item_content, false);
                }
            }
        });
    }
}
