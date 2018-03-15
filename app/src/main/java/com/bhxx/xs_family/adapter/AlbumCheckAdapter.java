package com.bhxx.xs_family.adapter;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.ImageDisplayActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.AlbumModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.entity.AlbumCheckOneEntity;
import com.bhxx.xs_family.entity.AlbumCheckThreeEntity;
import com.bhxx.xs_family.entity.AlbumCheckTwoEntity;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

public class AlbumCheckAdapter extends CommonAdapter<AlbumModel> {

    private int type;
    public AlbumCheckAdapter(List<AlbumModel> dataList, Context context,int type) {
        super(dataList, context, new AlbumCheckItemSupport());
        this.type = type;
    }

    @Override
    public void convert(final ViewHolder holder, final AlbumModel data) {

        switch (data.getAbState()){
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

        if(type == 0 || type == 2){holder.setVisibility(R.id.album_user_state, View.GONE);}
        if(type ==1){holder.setVisibility(R.id.checkbox, View.GONE);}

        holder.setOnCheckedChangeListener(R.id.checkbox, new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setChecked(data.getAbId(), 1);
                    if (getIsAllChecked()) {
                        if(type ==0){
                            EventBus.getDefault().post(new AlbumCheckOneEntity(AlbumCheckOneEntity.ALBUM_CHECKED));
                        }if(type ==2){
                            EventBus.getDefault().post(new AlbumCheckThreeEntity(AlbumCheckThreeEntity.ALBUM_CHECKED));
                        }
                    }
                } else {
                    if (getIsAllChecked()) {
                        EventBus.getDefault().post(new AlbumCheckOneEntity(AlbumCheckOneEntity.ALBUM_NOT_CHECKED));
                    }
                    setChecked(data.getAbId(), 0);
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
                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+user.getuHeadPic(), (CircleImageView) holder.getView(R.id.album_user_pic),LoadImage.getHeadImgOptions());
            }
            if (!TextUtils.isEmpty(user.getuName())) {
                holder.setText(R.id.album_user_name, user.getuName());
            }
        }
        if (!TextUtils.isEmpty(data.getAbCraeteTime())) {
            holder.setText(R.id.album_create_time, data.getAbCraeteTime() + "小时前");
        }
        holder.setText(R.id.album_user_class, data.getAbClassName());
        if (!TextUtils.isEmpty(data.getAbDesction())) {
            holder.setText(R.id.album_content, data.getAbDesction());
        }
        switch (holder.getLayoutId()) {
            case R.layout.album_check_no_pic_item:
                break;
            case R.layout.album_check_single_pic_item:
                if (!TextUtils.isEmpty(data.getAbPics())) {
                    final String[] single = data.getAbPics().split(";");
                    if (!TextUtils.isEmpty(single[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+data.getAbPics(), (ImageView) holder.getView(R.id.album_single_img), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.album_single_img, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ArrayList<String> singUrl = new ArrayList<String>();
                                singUrl.add(single[0]);
                                ImageDisplayActivity.start(context, singUrl, 0);
                            }
                        });
                    }
                }
                break;
            case R.layout.album_check_double_pic_item:
                if (!TextUtils.isEmpty(data.getAbPics())) {
                    final String[] doublePic = data.getAbPics().split(";");
                    final ArrayList<String> doubleUrl = new ArrayList<String>();
                    doubleUrl.add(doublePic[0]);
                    doubleUrl.add(doublePic[1]);
                    if (!TextUtils.isEmpty(doublePic[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+doublePic[0], (ImageView) holder.getView(R.id.album_double_img_1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.album_double_img_1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+doublePic[1], (ImageView) holder.getView(R.id.album_double_img_2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.album_double_img_2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 1);
                            }
                        });
                    }
                }
                break;
            case R.layout.album_check_list_pics_item:
                if (!TextUtils.isEmpty(data.getAbPics())) {
                    String[] pics = data.getAbPics().split(";");
                    final ArrayList<String> listPic = new ArrayList<String>();
                    for (int i = 0; i < pics.length; i++) {
                        listPic.add(pics[i]);
                    }
                    holder.setAdapter(R.id.album_pic_gv, new CommonAdapter<String>(listPic, context, R.layout.album_pics_item) {
                        @Override
                        public void convert(ViewHolder holders, String img) {
                            if (!TextUtils.isEmpty(img)) {
                                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+img, (ImageView) holders.getView(R.id.album_pics_img), LoadImage.getDefaultOptions());
                            }
                        }
                    });

                    holder.setOnItemClickListener(R.id.album_pic_gv, new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ImageDisplayActivity.start(context, listPic, position);
                        }
                    });
                }
                break;
        }
    }


    /**
     * 展示删除相册弹窗
     */
    private void showDeleteDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.album_delete_dialog, null);
        final Dialog log = new Dialog(context, R.style.transparentFrameWindowStyle);
        Button album_delete_cancle = (Button) view.findViewById(R.id.album_delete_cancle);
        Button album_delete_confirm = (Button) view.findViewById(R.id.album_delete_confirm);
        log.setContentView(view, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        Window window = log.getWindow();
        WindowManager.LayoutParams wl = window.getAttributes();
        // 设置显示位置
        log.onWindowAttributesChanged(wl);
        // 设置点击外围解散
        log.setCanceledOnTouchOutside(false);
        log.show();
        int measureWidth = context.getResources().getDisplayMetrics().widthPixels * 4 / 5;
        window.setLayout(measureWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
        album_delete_cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
            }
        });
        album_delete_confirm.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                log.dismiss();
            }
        });
    }

    private boolean getIsAllChecked() {
        for (AlbumModel model : getDataList()) {
            if (model.getChecked() == 0) {
                return false;
            }
        }
        return true;
    }

    private void setChecked(int albumId, int checked) {
        for (AlbumModel model : getDataList()) {
            if (model.getAbId() == albumId) {
                model.setChecked(checked);
            }
        }
    }
}
