package com.bhxx.xs_family.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioGroup;

import com.android.pc.ioc.event.EventBus;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.entity.PlayLiveEntity;
import com.bhxx.xs_family.fragment.MonitorFragment;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.LogUtils;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.ConstantLiveSDK;
import com.hikvision.sdk.consts.Constants;
import com.hikvision.sdk.live.LiveControl;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.DeviceInfo;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.widget.CustomRect;
import com.hikvision.sdk.widget.CustomSurfaceView;


public class PlayLiveFullScreenActivity extends BasicActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, SurfaceHolder.Callback, LiveControl.LiveCallBack {

    private static final String TAG = "LiveActivity";
    /**
     * 监控点
     */
    private Camera camera = null;
    /**
     * 监控点详细信息
     */
    private CameraInfo cameraInfo = new CameraInfo();
    /**
     * 监控点关联的监控设备信息
     */
    private DeviceInfo deviceInfo = null;
    /**
     * sdk实例
     */
    private VMSNetSDK mVMSNetSDK = null;

    /**
     * 视图更新处理助手
     */
    private Handler mHandler = null;
    /**
     * 预览控件
     */
    private CustomSurfaceView mSurfaceView = null;
    /**
     * 设备账户名
     */
    private String username = null;
    /**
     * 设备登录密码
     */
    private String password = null;
    /**
     * 控制层对象
     */
    private LiveControl mLiveControl = null;
    /**
     * 码流类型
     */
    private int mStreamType = ConstantLiveSDK.MAIN_HING_STREAM;

    /**
     * 标记是否在播放
     *
     * @param savedInstanceState
     * true 为播放  false为没播放
     */
    private boolean isplay = false;//
    private int livetype;//获取哪个播放视频资源   (布局)
    private int livevalue;//获取哪个播放视频资源  (mid)
    private long exitTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.play_full_screen);
        mSurfaceView = (CustomSurfaceView) findViewById(R.id.surfaceView);
        initData();
        loadData();
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
    }


    @Override
    protected void click(View view) {

    }


    @Override
    public void onClick(View v) {
        Log.i(TAG, "you click view:" + v.getContentDescription());
        switch (v.getId()) {

        }
    }


    /**
     * start play video
     */
    private void clickStartBtn(CustomSurfaceView mSurfaceView) {
        showProgressDialog(PlayLiveFullScreenActivity.this, "加载中...");
        String liveUrl = VMSNetSDK.getInstance().getPlayUrl(cameraInfo, mStreamType);
        mLiveControl.setLiveParams(liveUrl, null == username ? "" : username, null == password ? "" : password);
        if (LiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
            mLiveControl.stop();
        }

        if (LiveControl.LIVE_INIT == mLiveControl.getLiveState()) {
            mLiveControl.startLive(mSurfaceView);
        }
    }

    /**
     * stop play video
     */
    private void clickStopBtn(CustomSurfaceView mSurfaceView) {
        if (null != mLiveControl) {
            mLiveControl.stop();
        }
        mSurfaceView.setBackgroundResource(R.mipmap.monitorlogo);
//        mSurfaceView.setBackgroundColor(Color.parseColor("#717E9E"));
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        if (null != mLiveControl) {
            mLiveControl.stop();
        }
    }


    /**
     * 视图更新处理器
     */
    class ViewHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.Live.getCameraInfo:
//                    showToast(R.string.loading_camera_info);
                    break;
                case Constants.Live.getCameraInfo_Success:
                    dismissProgressDialog();
                    getDeviceInfo();
                    break;
                case Constants.Live.getCameraInfo_failure:
                    dismissProgressDialog();
                    showToast(R.string.loading_camera_info_failure);
                    break;
                case Constants.Live.getDeviceInfo:
//                    showToast(R.string.loading_device_info);
                    break;
                case Constants.Live.getDeviceInfo_Success:
                    dismissProgressDialog();
                    username = deviceInfo.getUserName();
                    password = deviceInfo.getPassword();

//                    //进来默认播放
                    clickStartBtn(mSurfaceView);
                    LogUtils.i("device infomation : username:" + username + "  password" + password);
                    break;
                case Constants.Live.getDeviceInfo_failure:
                    dismissProgressDialog();
                    showToast(R.string.loading_device_info_failure);
                    break;

                // 视频控制层回调的消息
                case ConstantLiveSDK.RTSP_FAIL:
                    showToast(R.string.rtsp_fail);
                    if (null != mLiveControl) {
                        mLiveControl.stop();
                    }
                    break;
                case ConstantLiveSDK.RTSP_SUCCESS:
//                    showToast(R.string.rtsp_success);
                    break;
                case ConstantLiveSDK.STOP_SUCCESS:
                    isplay = false;
                    //停止成功
//                    showToast(R.string.live_stop_success);
                    break;
                case ConstantLiveSDK.START_OPEN_FAILED:
                    showToast(R.string.start_open_failed);
                    break;
                case ConstantLiveSDK.PLAY_DISPLAY_SUCCESS:
                    isplay = true;
                    //播放成功
//                    showToast(R.string.play_display_success);
                    progressDialog.dismiss();
                    mSurfaceView.setBackgroundColor(Color.parseColor("#00ffffff"));
                    break;


                default:
                    break;
            }
        }
    }

    /**
     * init activity data
     */
    private void initData() {

        livetype =  Integer.parseInt(getIntent().getStringExtra("livetype"));
        livevalue =  Integer.parseInt(getIntent().getStringExtra("livevalue"));

        SubResourceNodeBean bean = new SubResourceNodeBean();
        //id=1, name=1F小一班活动室, nodeType=3, pid=1, isOnline=0, sysCode=ccd2b16740de4152bc96b0336a52ee8b

        bean.setId(livevalue);
        /**
         * 根据监控id查找监控信息
         */
        camera = VMSNetSDK.getInstance().initCameraInfo((SubResourceNodeBean) bean);

        mVMSNetSDK = VMSNetSDK.getInstance();
        mHandler = new ViewHandler();
        mLiveControl = new LiveControl();
        mLiveControl.setLiveCallBack(this);

        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitApp();
            }
        });
        /**
         * 默认开启电子放大
         */
//        mSurfaceView.setOnZoomListener(new CustomSurfaceView.OnZoomListener() {
//
//            @Override
//            public void onZoomChange(CustomRect original, CustomRect current) {
//
//                //点击事件
////                if (isplay) {
////                    clickStopBtn(mSurfaceView);
////                } else {
//////                    clickStartBtn(mSurfaceView);
////                }
//                exitApp();
//
//            }
//        });
    }

    private void exitApp() {
        // 判断2次点击事件时间
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            showToast("再点一次退出全屏");
            exitTime = System.currentTimeMillis();
        } else {
            toFinish(livetype);
        }
    }
    @Override
    public void onMessageCallback(int message) {
        sendMessageCase(message);
    }

    /**
     * 发送消息
     *
     * @param i void
     * @since V1.0
     */
    private void sendMessageCase(int i) {
        if (null != mHandler) {
            mHandler.sendEmptyMessage(i);
        }
    }

    /**
     * init activity value
     */

    /**
     * 打开预览
     */
    private void loadData() {
        getCameraInfo();

    }

    /**
     * 获取监控点详细信息
     */
    private void getCameraInfo() {
        if (null == camera) {
            Log.e(TAG, "getCameraInfo==>>camera is null");
            return;
        }
        mHandler.sendEmptyMessage(Constants.Live.getCameraInfo);
        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(Constants.Live.getCameraInfo_failure);

            }

            @Override
            public void loading() {
                mHandler.sendEmptyMessage(Constants.Live.getCameraInfo);

            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof CameraInfo) {
                    cameraInfo = (CameraInfo) data;
                    mHandler.sendEmptyMessage(Constants.Live.getCameraInfo_Success);
                }
            }

        });
        boolean flag = mVMSNetSDK.getCameraInfo(camera);
    }

    /**
     * 获取设备信息
     */

    private void getDeviceInfo() {
        if (null == cameraInfo) {
            Log.e(TAG, "getDeviceInfo==>>cameraInfo is null");
            return;
        }

        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_failure);

            }

            @Override
            public void loading() {
                mHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);

            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof DeviceInfo) {
                    deviceInfo = (DeviceInfo) data;
                    mHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_Success);
                }
            }

        });

        boolean flag = mVMSNetSDK.getDeviceInfo(cameraInfo.getDeviceID());
    }

    /* (non-Javadoc)
* @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
*/
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        //
//        switch (group.getId()) {
//            case R.id.radioGroup:
//                getStreamType(group);
//                break;
//
//            case R.id.radioGroup_ptz:
//                getPtzcommand(group);
//                break;
//
//            default:
//                break;
//        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK || event.getAction() == KeyEvent.ACTION_DOWN){
            toFinish(livetype);
        }
        return false;
    }

    private void toFinish(int livetype){
        EventBus.getDefault().post(new PlayLiveEntity(livetype));
        finish();
    }
}
