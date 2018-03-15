package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.UserModel;

import java.util.List;

/**
 * Created by bh1988034 on 2016/8/24.
 */
public class SearchUserAdapter extends CommonAdapter<UserModel>{
    public SearchUserAdapter(List<UserModel> dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder, UserModel data) {

        if(!TextUtils.isEmpty(data.getuName())){
            holder.setText(R.id.searchUser_name,data.getuName());
        }
        if(!TextUtils.isEmpty(data.getuPosition())){
            holder.setText(R.id.searchUser_duty,data.getuPosition());
        }
    }
}
