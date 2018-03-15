package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.entity.AlbumCheckOneEntity;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class ActionCheckAdapter extends CommonAdapter<ActModel> {

    int type;
    public ActionCheckAdapter(List<ActModel> dataList, Context context, int layoutId ,int type) {
        super(dataList, context, layoutId);
        this.type = type ;
    }

    @Override
    public void convert(final ViewHolder holder, final ActModel data) {

        if(type == 0){
            holder.setVisible(R.id.album_user_state,false);
        }
        if(type == 1){
            holder.setVisible(R.id.checkbox,false);
        }
//        //活动审批状态0已提交未审批 1审批通过  2审批不通过
        switch (data.getAcState()){
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
                    setChecked(data.getAcId(), 1);
                    if (getIsAllChecked()) {
                        EventBus.getDefault().post(new AlbumCheckOneEntity(AlbumCheckOneEntity.ALBUM_CHECKED));
                    }
                } else {
                    if (getIsAllChecked()) {
                        EventBus.getDefault().post(new AlbumCheckOneEntity(AlbumCheckOneEntity.ALBUM_NOT_CHECKED));
                    }
                    setChecked(data.getAcId(), 0);
                }
            }
        });

        if(data.getChecked()==1){
            holder.setChecked(R.id.checkbox,true);
        }else {
            holder.setChecked(R.id.checkbox,false);
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
        if (!TextUtils.isEmpty(data.getAcCreateTime())) {
            holder.setText(R.id.album_create_time, data.getAcCreateTime());
        }
        holder.setText(R.id.album_user_class, data.getAcClassName());

        if (!TextUtils.isEmpty(data.getAcPics())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+data.getAcMainPic(), (ImageView) holder.getView(R.id.action_check_pic), LoadImage.getDefaultOptions());
        }

        if (!TextUtils.isEmpty(data.getAcTitle())) {
            holder.setText(R.id.action_check_title, data.getAcTitle());
        }
        if (!TextUtils.isEmpty(data.getAcDesction())) {
            holder.setText(R.id.action_check_desction, data.getAcDesction());
        }
        if (!TextUtils.isEmpty(data.getAcEndTime())) {
            holder.setText(R.id.action_check_endtime, data.getAcEndTime());
        }
    }

    private void setChecked(int actionid, int checked) {
        for (ActModel act : getDataList()) {
            if (act.getAcId() == actionid) {
                act.setChecked(checked);
            }
        }
    }

    private boolean getIsAllChecked() {
        for (ActModel act : getDataList()) {
            if (act.getChecked() == 0) {
                return false;
            }
        }
        return true;
    }

}
