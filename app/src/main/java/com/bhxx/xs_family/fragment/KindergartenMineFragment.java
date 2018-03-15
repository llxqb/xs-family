package com.bhxx.xs_family.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.AboutUsActivity;
import com.bhxx.xs_family.activity.ActionCheckActivity;
import com.bhxx.xs_family.activity.AlbumCheckActivity;
import com.bhxx.xs_family.activity.DynamicsCheckActivity;
import com.bhxx.xs_family.activity.FeedBackActivity;
import com.bhxx.xs_family.activity.LoginActivity;
import com.bhxx.xs_family.activity.MoveoaCheckActivity;
import com.bhxx.xs_family.activity.MsgSettingActivity;
import com.bhxx.xs_family.activity.UserCenterKindergartenActivity;
import com.bhxx.xs_family.activity.UserHelpActivity;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.db.dao.SysMessDao;
import com.bhxx.xs_family.entity.UploadPicEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;

import io.rong.imkit.RongIM;
import okhttp3.Call;

public class KindergartenMineFragment extends BaseFragment {
    @InjectAll
    private Views v;
    private UserModel user;
    private String uBrithday = "";

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        CircleImageView kindergarten_page_head_pic;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView kindergarten_exit_login;
        TextView kindergarten_page_user_name, kindergarten_page_remark;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        LinearLayout kindergarten_mine_album_layout, kindergarten_mine_act_layout, kindergarten_mine_dynamics_layout, kindergarten_mine_work_layout,
                kindergarten_mine_help_layout, kindergarten_mine_advice_layout, kindergarten_mine_setting_layout, kindergarten_mine_aboutus_layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_kindergarten_mine, null);
        Handler_Inject.injectFragment(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;
    }

    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void init() {
        if (!TextUtils.isEmpty(App.app.getData("uPic"))) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + App.app.getData("uPic"), v.kindergarten_page_head_pic, LoadImage.getHeadImgOptions());
        }
        v.kindergarten_page_remark.setText("园长");
        if (App.app.readObjData("user") != null) {
            user = (UserModel) App.app.readObjData("user");
            if (!TextUtils.isEmpty(user.getuName())) {
                v.kindergarten_page_user_name.setText(user.getuName());
            }
        }
    }

    protected void onEventMainThread(UploadPicEntity entity) {
        switch (entity.getKey()) {
            case UploadPicEntity.UPLOAD_KING:
                if (!TextUtils.isEmpty(App.app.getData("uPic"))) {
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + App.app.getData("uPic"), v.kindergarten_page_head_pic, LoadImage.getHeadImgOptions());
                }

                break;
            case UploadPicEntity.UPLOAD_KING_NAME:
                if (App.app.readObjData("user") != null) {
                    user = (UserModel) App.app.readObjData("user");
                    if (!TextUtils.isEmpty(user.getuName())) {
                        v.kindergarten_page_user_name.setText(user.getuName());
                    }
                }
                break;
        }
    }

    @Override
    protected void click(View view) {

        switch (view.getId()) {
            case R.id.kindergarten_page_head_pic:
                UserCenterKindergartenActivity.start(getActivity());
                break;
            case R.id.kindergarten_mine_album_layout:
                AlbumCheckActivity.start(getActivity());
                break;
            case R.id.kindergarten_mine_act_layout:
                ActionCheckActivity.start(getActivity());
                break;
            case R.id.kindergarten_mine_dynamics_layout:
                DynamicsCheckActivity.start(getActivity());
                break;
            case R.id.kindergarten_mine_work_layout:
                MoveoaCheckActivity.start(getActivity());
                break;
            case R.id.kindergarten_mine_help_layout:
                UserHelpActivity.start(getActivity());
                break;
            case R.id.kindergarten_mine_advice_layout:
                FeedBackActivity.start(getActivity());
                break;
            case R.id.kindergarten_mine_setting_layout:
                MsgSettingActivity.start(getActivity());
                break;
            case R.id.kindergarten_mine_aboutus_layout:
                AboutUsActivity.start(getActivity());
                break;
            case R.id.kindergarten_exit_login:
                new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("确定要退出登录?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginActivity.start(getActivity());
                        ActivityCollector.finishAll();
//                        App.app.clearDatabase("Cache");
//                        RongIM.getInstance().logout();
//                        SysMessDao.clearTable();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                break;
        }
    }

}
