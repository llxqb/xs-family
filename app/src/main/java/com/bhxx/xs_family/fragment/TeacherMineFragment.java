package com.bhxx.xs_family.fragment;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.AboutUsActivity;
import com.bhxx.xs_family.activity.ActionManagerActivity;
import com.bhxx.xs_family.activity.FeedBackActivity;
import com.bhxx.xs_family.activity.LoginActivity;
import com.bhxx.xs_family.activity.MoveOAActivity;
import com.bhxx.xs_family.activity.MsgSettingActivity;
import com.bhxx.xs_family.activity.MyActActivity;
import com.bhxx.xs_family.activity.PublishAlbumActivity;
import com.bhxx.xs_family.activity.PublishClassDynamicActivity;
import com.bhxx.xs_family.activity.TeacherActicity;
import com.bhxx.xs_family.activity.UserHelpActivity;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CheckworkModel;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.db.dao.SysMessDao;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.LinkedHashMap;

import cn.jpush.android.api.JPushInterface;
import io.rong.imkit.RongIM;
import okhttp3.Call;

public class TeacherMineFragment extends BaseFragment {
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = new AMapLocationClientOption();
    @InjectAll
    private Views v;
    private UserModel user;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        CircleImageView teacher_page_head_pic;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView teacher_exit_login;
        TextView teacher_page_user_name, teacher_page_remark, iscard;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        LinearLayout teacher_mine_card_layout, teacher_mine_act_layout, teacher_mine_dynamics_layout, teacher_mine_album_layout, teacher_mine_work_layout, teacher_mine_help_layout, teacher_mine_advice_layout, teacher_mine_setting_layout, teacher_mine_aboutus_layout;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.teacher_mine_page, null);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    protected void init() {
        //判断当前用户是否打卡
        initValue();
        //初始化定位
        initLocation();

        String cardTime = App.app.getData("cardTime");
        if (!TextUtils.isEmpty(cardTime)) {
            v.iscard.setText("上次打卡时间：" + cardTime);
        }

    }

    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(getActivity().getApplicationContext());
        //设置定位参数
        locationClient.setLocationOption(getDefaultOption());
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是ture
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        return mOption;
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (null != amapLocation) {
                if (amapLocation.getErrorCode() == 0) {
                    //解析定位结果
                    String result = amapLocation.getAddress();
                    //可在其中解析amapLocation获取相应内容。
                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("cwTeacherId", App.app.getData("uId"));
                    params.put("cwPlace", result);
                    String taoken = TokenUtils.getInstance().configParams(GlobalValues.TEACHER_KQ_CARD + "?", params);
                    params.put("taoken", taoken);
                    MyOkHttp.postMap(GlobalValues.TEACHER_KQ_CARD,1, "TEACHER_KQ_CARD", params, new MyStringCallback());
                } else {
                    showToast("定位失败");
                }
                stopLocation();
            } else {
                showToast("定位失败");
            }
        }
    };

    private void initValue() {
        if (!TextUtils.isEmpty(App.app.getData("uPic"))) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + App.app.getData("uPic"), v.teacher_page_head_pic, LoadImage.getHeadImgOptions());
        }
        if (App.app.readObjData("user") != null) {
            user = (UserModel) App.app.readObjData("user");
            if (!TextUtils.isEmpty(user.getuName())) {
                v.teacher_page_user_name.setText(user.getuName());
            }
            if (!TextUtils.isEmpty(user.getuClassName()) && !TextUtils.isEmpty(user.getuPosition())) {
                v.teacher_page_remark.setText(user.getuClassName() + "/" + user.getuPosition());
            } else {
                v.teacher_page_remark.setText("");
            }
        }
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.teacher_page_head_pic:
                TeacherActicity.start(getActivity());
                break;
            case R.id.teacher_mine_card_layout:
                checkPermission();
                //打卡
                break;
            case R.id.teacher_mine_act_layout:
                ActionManagerActivity.start(getActivity());
                break;
            case R.id.teacher_mine_dynamics_layout:
                PublishClassDynamicActivity.start(getActivity());
                break;
            case R.id.teacher_mine_album_layout:
                PublishAlbumActivity.start(getActivity());
                break;
            case R.id.teacher_mine_work_layout:
                MoveOAActivity.start(getActivity());
                break;
            case R.id.teacher_mine_help_layout:
                UserHelpActivity.start(getActivity());
                break;
            case R.id.teacher_mine_advice_layout:
                FeedBackActivity.start(getActivity());
                break;
            case R.id.teacher_mine_setting_layout:
                MsgSettingActivity.start(getActivity());
                break;
            case R.id.teacher_mine_aboutus_layout:
                AboutUsActivity.start(getActivity());
                break;
            case R.id.teacher_exit_login:
                new AlertDialog.Builder(getActivity()).setTitle("提示").setMessage("确定要退出登录?").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        LoginActivity.start(getActivity());
                        ActivityCollector.finishAll();
//                        App.app.clearDatabase("Cache");
//                        SysMessDao.clearTable();
                        RongIM.getInstance().logout();
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


    private void ClockInCard() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("提示");
        builder.setMessage("是否打卡"); //设置内容
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() { //设置确定按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startLocation();
                dialog.dismiss(); //关闭dialog
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() { //设置取消按钮
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //参数都设置完成了，创建并显示出来
        builder.create().show();

        //        .
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("打卡失败");
        }

        @Override
        public void onResponse(String response, int id) {
            LogUtils.i("response="+response);
            switch (id){
                case 1:
                    if (!TextUtils.isEmpty(response)) {
                        CommonBean<CheckworkModel> listbean = JsonUtils.getBean(response, CommonBean.class, CheckworkModel.class);
                        if (listbean.isSuccess()) {
                            showToast("打卡成功");
                            App.app.setData("cardTime", CheckUtils.getCurrentTime());
                            v.iscard.setText("已打卡：" + CheckUtils.getCurrentTime());
                        } else {
                            showToast("打卡失败");
                        }
                    } else {
                        showToast("打卡失败");
                    }
                    break;
                case 2:
                    break;
            }

        }
    }

    /**
     * 开始定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void startLocation() {
        //根据控件的选择，重新设置定位参数
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 停止定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void stopLocation() {
        // 停止定位
        locationClient.stopLocation();
    }

    @Override
    public void onDestroy() {
        destroyLocation();
        super.onDestroy();
    }

    /**
     * 销毁定位
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private void destroyLocation() {
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }

    protected void checkPermission() {
        //版本判断
        if (Build.VERSION.SDK_INT >= 23) {
            //减少是否拥有权限
            int checkPermissionResult = getContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION);
            if (checkPermissionResult != PackageManager.PERMISSION_GRANTED) {
                //弹出对话框接收权限
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 100);
                return;
            } else {
                //获取到权限
                ClockInCard();
            }
        } else {
            //获取到权限
            ClockInCard();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //获取到权限
                ClockInCard();
            } else {
                //没有获取到权限
            }
        }
    }



}
