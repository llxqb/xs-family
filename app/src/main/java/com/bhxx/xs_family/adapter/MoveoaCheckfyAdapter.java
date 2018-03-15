package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.ImageDisplayActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.ReimbursementModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.entity.AlbumCheckOneEntity;
import com.bhxx.xs_family.entity.AlbumCheckThreeEntity;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveoaCheckfyAdapter extends CommonAdapter<ReimbursementModel> {
    Map<Integer, Boolean> isCheckMap =  new HashMap<Integer, Boolean>();
    String check;

    public MoveoaCheckfyAdapter(List<ReimbursementModel> dataList, Context context, int layoutId,String check) {
        super(dataList, context,layoutId);
        this.check = check;
    }

    @Override
    public void convert(final ViewHolder holder, final ReimbursementModel data) {
        if(check.equals("nocheck")){
            holder.setVisible(R.id.album_user_state,false);
            holder.setOnCheckedChangeListener(R.id.checkbox, new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        setChecked(data.getRbId(),1);
                        if(getIsAllChecked()){
                            EventBus.getDefault().post(new AlbumCheckThreeEntity(AlbumCheckThreeEntity.ALBUM_CHECKED));
                        }
                    }else{
                        if(getIsAllChecked()){
                            EventBus.getDefault().post(new AlbumCheckThreeEntity(AlbumCheckThreeEntity.ALBUM_NOT_CHECKED));
                        }
                        setChecked(data.getRbId(),0);
                    }
                }
            });

            if(data.getChecked()==0){
                holder.setChecked(R.id.checkbox,false);
            }else{
                holder.setChecked(R.id.checkbox,true);
            }
        }else{
            holder.setVisibility(R.id.checkbox, View.GONE);
            holder.setVisible(R.id.album_user_state,true);
        }

        switch (data.getRbState()){
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

        if(!TextUtils.isEmpty(data.getRbBill())){
            holder.setVisible(R.id.moveoa_fy_img_layout,true);
            final String[] aa = data.getRbBill().split(";");
            switch (aa.length){
                case 1:
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[0], (ImageView) holder.getView(R.id.moveoa_fy_img1), LoadImage.getDefaultOptions());
                    holder.setVisibility(R.id.moveoa_fy_img1,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img2,View.INVISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img3,View.INVISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img4,View.INVISIBLE);
                    holder.setOnClickListener(R.id.moveoa_fy_img1, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ArrayList<String> singUrl = new ArrayList<String>();
                            singUrl.add(aa[0]);
                            ImageDisplayActivity.start(context, singUrl, 0);
                        }
                    });
                    break;
                case 2:
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[0], (ImageView) holder.getView(R.id.moveoa_fy_img1), LoadImage.getDefaultOptions());
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[1], (ImageView) holder.getView(R.id.moveoa_fy_img2), LoadImage.getDefaultOptions());
                    holder.setVisibility(R.id.moveoa_fy_img1,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img2,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img3,View.INVISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img4,View.INVISIBLE);
                    final ArrayList<String> doubleUrl = new ArrayList<String>();
                    doubleUrl.add(aa[0]);
                    doubleUrl.add(aa[1]);
                    if (!TextUtils.isEmpty(aa[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[0], (ImageView) holder.getView(R.id.moveoa_fy_img1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(aa[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[1], (ImageView) holder.getView(R.id.moveoa_fy_img2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 1);
                            }
                        });
                    }
                    break;
                case 3:
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[0], (ImageView) holder.getView(R.id.moveoa_fy_img1), LoadImage.getDefaultOptions());
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[1], (ImageView) holder.getView(R.id.moveoa_fy_img2), LoadImage.getDefaultOptions());
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[2], (ImageView) holder.getView(R.id.moveoa_fy_img3), LoadImage.getDefaultOptions());
                    holder.setVisibility(R.id.moveoa_fy_img1,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img2,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img3,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img4,View.INVISIBLE);
                    final ArrayList<String> threeUrl = new ArrayList<String>();
                    threeUrl.add(aa[0]);
                    threeUrl.add(aa[1]);
                    threeUrl.add(aa[2]);
                    if (!TextUtils.isEmpty(aa[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[0], (ImageView) holder.getView(R.id.moveoa_fy_img1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, threeUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(aa[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[1], (ImageView) holder.getView(R.id.moveoa_fy_img2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, threeUrl, 1);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(aa[2])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[2], (ImageView) holder.getView(R.id.moveoa_fy_img3), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img3, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, threeUrl, 2);
                            }
                        });
                    }
                    break;
                case 4:
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[0], (ImageView) holder.getView(R.id.moveoa_fy_img1), LoadImage.getDefaultOptions());
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[1], (ImageView) holder.getView(R.id.moveoa_fy_img2), LoadImage.getDefaultOptions());
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[2], (ImageView) holder.getView(R.id.moveoa_fy_img3), LoadImage.getDefaultOptions());
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+aa[3], (ImageView) holder.getView(R.id.moveoa_fy_img4), LoadImage.getDefaultOptions());
                    holder.setVisibility(R.id.moveoa_fy_img1,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img2,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img3,View.VISIBLE);
                    holder.setVisibility(R.id.moveoa_fy_img4,View.VISIBLE);
                    final ArrayList<String> fourUrl = new ArrayList<String>();
                    fourUrl.add(aa[0]);
                    fourUrl.add(aa[1]);
                    fourUrl.add(aa[2]);
                    fourUrl.add(aa[3]);
                    if (!TextUtils.isEmpty(aa[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[0], (ImageView) holder.getView(R.id.moveoa_fy_img1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, fourUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(aa[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[1], (ImageView) holder.getView(R.id.moveoa_fy_img2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, fourUrl, 1);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(aa[2])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[2], (ImageView) holder.getView(R.id.moveoa_fy_img3), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img3, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, fourUrl, 2);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(aa[3])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + aa[3], (ImageView) holder.getView(R.id.moveoa_fy_img4), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_fy_img4, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, fourUrl, 3);
                            }
                        });
                    }
                    break;
            }
        }else{
            holder.setVisible(R.id.moveoa_fy_img_layout,false);
        }

        if (data.getAppUser() != null) {
            UserModel user = data.getAppUser();
            if (!TextUtils.isEmpty(user.getuHeadPic())) {
                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+user.getuHeadPic(), (CircleImageView) holder.getView(R.id.album_user_pic),LoadImage.getHeadImgOptions());
            }
            if (!TextUtils.isEmpty(user.getuName())) {
                holder.setText(R.id.album_user_name, user.getuName());
            }
        }

        if (!TextUtils.isEmpty(data.getRbCreateTime())) {
            holder.setText(R.id.album_create_time, data.getRbCreateTime());
        }
        holder.setText(R.id.album_user_class, data.getRbClassName());
        if (!TextUtils.isEmpty(data.getRbDesction())) {
            holder.setText(R.id.moveoafy_check_content, data.getRbDesction());
        }
//        if (!TextUtils.isEmpty(data.getTtType())) {
//            holder.setText(R.id.moveoawp_check_type, data.getTtType());
//        }
        if (!TextUtils.isEmpty(data.getRbMoney()+"")) {
            holder.setText(R.id.moveoafy_check_count, data.getRbMoney()+"元");
        }
    }

    private  void setChecked(int position,int checked){
        for(ReimbursementModel model:getDataList()){
            if(model.getRbId()==position){
                model.setChecked(checked);
            }
        }
        notifyDataSetChanged();
    }

    private boolean getIsAllChecked(){
        for (ReimbursementModel model:getDataList()){
            if(model.getChecked()==0){
                return false;
            }
        }
        return true;
    }


}
