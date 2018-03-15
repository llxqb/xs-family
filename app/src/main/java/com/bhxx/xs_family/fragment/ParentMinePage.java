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
import android.widget.Toast;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.AboutUsActivity;
import com.bhxx.xs_family.activity.LoginActivity;
import com.bhxx.xs_family.activity.MsgSettingActivity;
import com.bhxx.xs_family.activity.MyActActivity;
import com.bhxx.xs_family.activity.MyCollectActivity;
import com.bhxx.xs_family.activity.FeedBackActivity;
import com.bhxx.xs_family.activity.UserCenterActivity;
import com.bhxx.xs_family.activity.UserHelpActivity;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ClassModel;
import com.bhxx.xs_family.beans.StudentModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.db.dao.SysMessDao;
import com.bhxx.xs_family.entity.UploadPicEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.Constants;
import com.hikvision.sdk.data.TempDatas;
import com.hikvision.sdk.net.bean.LoginData;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;

import io.rong.imkit.RongIM;
import okhttp3.Call;

public class ParentMinePage extends BaseFragment {
    @InjectAll
    private Views v;
    private UserModel user;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        CircleImageView parent_page_head_pic;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView parent_exit_login;
        TextView parent_page_user_name, parent_page_remark;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        LinearLayout parent_mine_act_layout, parent_mine_collect_layout, parent_mine_help_layout, parent_mine_advice_layout, parent_mine_setting_layout, parent_mine_about_layout;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.parent_mine_page, null);
        Handler_Inject.injectFragment(this, rootView);
        EventBus.getDefault().register(this);
        return rootView;

    }

    @Override
    protected void init() {
        initValue();
    }

    private void initValue() {
        if (!TextUtils.isEmpty(App.app.getData("uPic"))) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + App.app.getData("uPic"), v.parent_page_head_pic, LoadImage.getHeadImgOptions());
        }
        if (App.app.readObjData("user") != null) {
            user = (UserModel) App.app.readObjData("user");
            if (!TextUtils.isEmpty(user.getuName())) {
                v.parent_page_user_name.setText(user.getuName());
            }
            StudentModel student = user.getAppStudent();
            if (student != null) {
                ClassModel classModel = student.getAppClass();
                if(!TextUtils.isEmpty(classModel.getClName())&& !TextUtils.isEmpty(student.getStName())){
                    v.parent_page_remark.setText(classModel.getClName() + "/" + student.getStName());
                }else{
                    v.parent_page_remark.setText("未获取到");
                }
//                if (!TextUtils.isEmpty(user.getuClassName()) && !TextUtils.isEmpty(student.getStName())) {
//                    v.parent_page_remark.setText(user.getuClassName() + "/" + student.getStName());
//                } else {
//                    v.parent_page_remark.setText("未获取到");
//                }
            }
        }
    }

    protected void onEventMainThread(UploadPicEntity entity) {
        switch (entity.getKey()) {
            case UploadPicEntity.UPLOAD_PARENT:
                if (!TextUtils.isEmpty(App.app.getData("uPic"))) {
                    ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + App.app.getData("uPic"), v.parent_page_head_pic, LoadImage.getHeadImgOptions());
                }
                break;
            case UploadPicEntity.UPLOAD_PARENT_NAME:
                if (App.app.readObjData("user") != null) {
                    user = (UserModel) App.app.readObjData("user");
                    if (!TextUtils.isEmpty(user.getuName())) {
                        v.parent_page_user_name.setText(user.getuName());
                    }
                }
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.parent_page_head_pic:
                UserCenterActivity.start(getActivity());
                break;
            case R.id.parent_mine_act_layout:
                MyActActivity.start(getActivity());
                break;
            case R.id.parent_mine_collect_layout:
                MyCollectActivity.start(getActivity());
                break;
            case R.id.parent_mine_help_layout:
                UserHelpActivity.start(getActivity());
                break;
            case R.id.parent_mine_advice_layout:
                FeedBackActivity.start(getActivity());
                break;
            case R.id.parent_mine_setting_layout:
                MsgSettingActivity.start(getActivity());
                break;
            case R.id.parent_mine_about_layout:
                AboutUsActivity.start(getActivity());
                break;
            case R.id.parent_exit_login:
                new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("确定要退出登录?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginActivity.start(getActivity());
                        ActivityCollector.finishAll();
//                        App.app.clearDatabase("Cache");
//                        SysMessDao.clearTable();
                        RongIM.getInstance().logout();
                        //注销 监控
                        VMSNetSDK.getInstance().logout();
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
