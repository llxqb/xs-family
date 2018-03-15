package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.TeacherInfoActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class AddressBookAdapter extends CommonAdapter<UserModel> {
    public AddressBookAdapter(List dataList, Context context, int layoutId) {
        super(dataList, context, layoutId);
    }

    @Override
    public void convert(ViewHolder holder,final UserModel data) {
        if (!TextUtils.isEmpty(data.getuHeadPic())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+data.getuHeadPic(), (CircleImageView) holder.getView(R.id.address_book_user_pic), LoadImage.getHeadImgOptions());
        }
        if (!TextUtils.isEmpty(data.getuName())) {
            holder.setText(R.id.address_book_user_name, data.getuName());
        }
        holder.setOnClickListener(R.id.address_book_layout, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TeacherInfoActivity.start(context,data);
            }
        });
    }
}
