package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.DynamicModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.entity.AlbumCheckOneEntity;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class DynamicsCheckAdapter extends CommonAdapter<DynamicModel> {
    int type;
    public DynamicsCheckAdapter(List<DynamicModel> dataList, Context context, int layoutId,int type) {
        super(dataList, context,layoutId);
        this.type = type;
    }

    @Override
    public void convert(final ViewHolder holder, final DynamicModel data) {

        if(type == 0){
            holder.setVisible(R.id.album_user_state,false);
        }
        if(type == 1){
            holder.setVisible(R.id.checkbox,false);
        }
        switch (data.getDcState()){
            case 0:
                holder.setText(R.id.album_user_state, "未审批");
                holder.setBackgroundRes(R.id.album_user_state, R.drawable.round_grey_bg);
                holder.setTextColorRes(R.id.album_user_state,R.color.text_little_black);
                break;
            case 1:
                holder.setText(R.id.album_user_state, "已通过");
                holder.setBackgroundRes(R.id.album_user_state, R.drawable.round_yellow_bg);
                holder.setTextColorRes(R.id.album_user_state,R.color.yellow);
                break;
            case 2:
                holder.setText(R.id.album_user_state, "未通过");

                holder.setBackgroundRes(R.id.album_user_state, R.drawable.round_grey_bg);
                holder.setTextColorRes(R.id.album_user_state,R.color.text_little_black);
                break;
        }

        holder.setOnCheckedChangeListener(R.id.checkbox, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setChecked(data.getDcId(), 1);
                    if (getIsAllChecked()) {
                        EventBus.getDefault().post(new AlbumCheckOneEntity(AlbumCheckOneEntity.ALBUM_CHECKED));
                    }
                } else {
                    if (getIsAllChecked()) {
                        EventBus.getDefault().post(new AlbumCheckOneEntity(AlbumCheckOneEntity.ALBUM_NOT_CHECKED));
                    }
                    setChecked(data.getDcId(), 0);
                }
            }
        });

        if (data.getChecked() == 1) {
            holder.setChecked(R.id.checkbox, true);
        } else {
            holder.setChecked(R.id.checkbox, false);
        }



        if (data.getAbPublisher() != null) {
            UserModel user = data.getAbPublisher();
            if (!TextUtils.isEmpty(user.getuHeadPic())) {
                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+user.getuHeadPic(), (CircleImageView) holder.getView(R.id.album_user_pic), LoadImage.getHeadImgOptions());
            }
            if (!TextUtils.isEmpty(user.getuName())) {
                holder.setText(R.id.album_user_name, user.getuName());
            }
        }

        if (!TextUtils.isEmpty(data.getDcCreateTime())) {
            holder.setText(R.id.album_create_time, data.getDcCreateTime());
        }
        holder.setText(R.id.album_user_class, data.getDcClassName());
        if (!TextUtils.isEmpty(data.getDcPics())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+data.getDcMainPic(), (ImageView) holder.getView(R.id.dynamics_check_pic), LoadImage.getDefaultOptions());
        }
        if (!TextUtils.isEmpty(data.getDcTitle())) {
            holder.setText(R.id.dynamics_check_title, data.getDcTitle());
        }
        if (!TextUtils.isEmpty(data.getDcDesction())) {
            holder.setText(R.id.dynamics_check_desction, data.getDcDesction());
        }
    }

    private boolean getIsAllChecked() {
        for (DynamicModel model : getDataList()) {
            if (model.getChecked() == 0) {
                return false;
            }
        }
        return true;
    }

    private void setChecked(int albumId, int checked) {
        for (DynamicModel model : getDataList()) {
            if (model.getDcId() == albumId) {
                model.setChecked(checked);
            }
        }
    }

}
