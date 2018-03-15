package com.bhxx.xs_family.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.ImageDisplayActivity;
import com.bhxx.xs_family.adapter.common.CommonAdapter;
import com.bhxx.xs_family.adapter.common.ViewHolder;
import com.bhxx.xs_family.beans.LeaveModel;
import com.bhxx.xs_family.beans.ReimbursementModel;
import com.bhxx.xs_family.beans.TeachingtoolModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MoveoaWpAdapter extends CommonAdapter<TeachingtoolModel> {
    Map<Integer, Boolean> map = new HashMap<Integer, Boolean>();

    public MoveoaWpAdapter(List<TeachingtoolModel> dataList, Context context) {
        super(dataList, context, new MoveoaWpItemSupport());
        for (TeachingtoolModel model : getDataList()) {
            map.put(model.getTtId(), false);
        }
    }

    @Override
    public void addDataListAtLast(List<TeachingtoolModel> dataList) {
        super.addDataListAtLast(dataList);
        for (TeachingtoolModel model : getDataList()) {
            map.put(model.getTtId(), false);
        }
    }

    public Map getMapStatus() {
        return map;
    }

    @Override
    public void convert(final ViewHolder holder, final TeachingtoolModel data) {

        if (data.getAppUser() != null) {
            UserModel user = new UserModel();
            if (!TextUtils.isEmpty(user.getuName())) {
                holder.setText(R.id.moveoa_wp_user, "申领人：" + user.getuName());
            }
        }
        if (!TextUtils.isEmpty(data.getTtCreateTime())) {
            holder.setText(R.id.moveoa_wp_createtime, data.getTtCreateTime());
        }

        if (!TextUtils.isEmpty(data.getTtDesction())) {
            holder.setText(R.id.moveoa_wp_desction, data.getTtDesction());
        }
        if (!TextUtils.isEmpty(data.getTtType())) {
            holder.setText(R.id.moveoa_wp_type, data.getTtType());
        }
        if (!TextUtils.isEmpty(data.getTtCount() + "")) {
            holder.setText(R.id.moveoa_wp_count, data.getTtCount() + "");
        }

        holder.setText(R.id.submitTime1,data.getTtCreateTime());
        holder.setText(R.id.submitTime2,data.getTtCreateTime());
        holder.setText(R.id.submitTime3,data.getTtStateTime());

        if (!TextUtils.isEmpty(data.getTtState() + "")) {
            if (data.getTtState() == 0) {
                holder.setText(R.id.moveoa_wp_state, "已提交");
                holder.setTextColorRes(R.id.moveoa_wp_state, R.color.yellow);
                holder.setBackgroundRes(R.id.moveoa_wp_state, R.drawable.round_yellow_bg);

                holder.setVisibility(R.id.submitlayout3,View.INVISIBLE);
                holder.setVisibility(R.id.submitline,View.INVISIBLE);
            }
            if (data.getTtState() == 1) {
                holder.setText(R.id.moveoa_wp_state, "已批准");
                holder.setTextColorRes(R.id.moveoa_wp_state, R.color.yellow);
                holder.setBackgroundRes(R.id.moveoa_wp_state, R.drawable.round_yellow_bg);

                holder.setText(R.id.submitapprove,"已批准");
            }
            if (data.getTtState() == 2) {
                holder.setText(R.id.moveoa_wp_state, "未通过");
                holder.setTextColorRes(R.id.moveoa_wp_state, R.color.black);
                holder.setBackgroundRes(R.id.moveoa_wp_state, R.drawable.round_grey_bg);

                holder.setText(R.id.submitapprove,"未通过");
            }
        }

        if (map.get(data.getTtId())) {
            holder.setVisibility(R.id.moveoa_wp_details, View.VISIBLE);
        } else {
            holder.setVisibility(R.id.moveoa_wp_details, View.GONE);
        }

        switch (holder.getLayoutId()) {
            case R.layout.moveoawp_no_pic_item:
                break;
            case R.layout.moveoawp_one_pic_item:
                if (!TextUtils.isEmpty(data.getTtPics())) {
                    final String[] single = data.getTtPics().split(";");
                    if (!TextUtils.isEmpty(single[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + data.getTtPics(), (ImageView) holder.getView(R.id.moveoa_wp_img1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img1, new View.OnClickListener() {
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
            case R.layout.moveoawp_two_pic_item:
                if (!TextUtils.isEmpty(data.getTtPics())) {
                    final String[] doublePic = data.getTtPics().split(";");
                    final ArrayList<String> doubleUrl = new ArrayList<String>();
                    doubleUrl.add(doublePic[0]);
                    doubleUrl.add(doublePic[1]);
                    if (!TextUtils.isEmpty(doublePic[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[0], (ImageView) holder.getView(R.id.moveoa_wp_img1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[1], (ImageView) holder.getView(R.id.moveoa_wp_img2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, doubleUrl, 1);
                            }
                        });
                    }
                }
                break;
            case R.layout.moveoawp_three_pic_item:
                if (!TextUtils.isEmpty(data.getTtPics())) {
                    final String[] doublePic = data.getTtPics().split(";");
                    final ArrayList<String> threeUrl = new ArrayList<String>();
                    threeUrl.add(doublePic[0]);
                    threeUrl.add(doublePic[1]);
                    threeUrl.add(doublePic[2]);
                    if (!TextUtils.isEmpty(doublePic[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[0], (ImageView) holder.getView(R.id.moveoa_wp_img1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, threeUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[1], (ImageView) holder.getView(R.id.moveoa_wp_img2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, threeUrl, 1);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[2])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[2], (ImageView) holder.getView(R.id.moveoa_wp_img3), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img3, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, threeUrl, 2);
                            }
                        });
                    }
                }
                break;
            case R.layout.moveoawp_four_pic_item:
                if (!TextUtils.isEmpty(data.getTtPics())) {
                    final String[] doublePic = data.getTtPics().split(";");
                    final ArrayList<String> fourUrl = new ArrayList<String>();
                    fourUrl.add(doublePic[0]);
                    fourUrl.add(doublePic[1]);
                    fourUrl.add(doublePic[2]);
                    fourUrl.add(doublePic[3]);
                    if (!TextUtils.isEmpty(doublePic[0])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[0], (ImageView) holder.getView(R.id.moveoa_wp_img1), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img1, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, fourUrl, 0);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[1])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[1], (ImageView) holder.getView(R.id.moveoa_wp_img2), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img2, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, fourUrl, 1);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[2])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[2], (ImageView) holder.getView(R.id.moveoa_wp_img3), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img3, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, fourUrl, 2);
                            }
                        });
                    }
                    if (!TextUtils.isEmpty(doublePic[3])) {
                        ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + doublePic[3], (ImageView) holder.getView(R.id.moveoa_wp_img4), LoadImage.getDefaultOptions());
                        holder.setOnClickListener(R.id.moveoa_wp_img4, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ImageDisplayActivity.start(context, fourUrl, 3);
                            }
                        });
                    }
                }
                break;
        }

    }

}
