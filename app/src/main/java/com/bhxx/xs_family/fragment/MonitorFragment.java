package com.bhxx.xs_family.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.PlayLiveFullScreenActivity;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.entity.PlayLiveEntity;
import com.bhxx.xs_family.utils.LogUtils;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.ConstantLiveSDK;
import com.hikvision.sdk.consts.Constants;
import com.hikvision.sdk.live.LiveControl;
import com.hikvision.sdk.live.PCRect;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.DeviceInfo;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.widget.CustomRect;
import com.hikvision.sdk.widget.CustomSurfaceView;

import java.util.ArrayList;
import java.util.List;

public class MonitorFragment extends BaseFragment implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, SurfaceHolder.Callback, LiveControl.LiveCallBack {

    private static final String TAG = "LiveActivity";
    /**
     * 监控点
     */
    private Camera camera = null;
//    private Camera camera2= null;
//    private Camera camera3= null;
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
    private CustomSurfaceView mSurfaceView1 = null;
    private CustomSurfaceView mSurfaceView2 = null;
    private CustomSurfaceView mSurfaceView3 = null;
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
     * 进度条
     */
    private ProgressBar progressBar = null;

    //暂停 开始 图片按钮
    private TextView playtv, playtv2, playtv3;
    private TextView fullscreentv, fullscreentv2, fullscreentv3;
    private ImageView playImg,playImg2,playImg3;
    private Intent intent;
    //全屏布局
    private LinearLayout fullscreenlin,fullscreenlin2,fullscreenlin3;
    //播放布局
    private LinearLayout playlin,playlin2,playlin3;
    private int type=-1;//表示播放的第几个视频  livetype主要指eventbus数据
    private List<Integer> list;
    private List listmTitle;
    private int livevalue;
    private TextView liveTitle1,liveTitle2,liveTitle3;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //monitor_fragment
        View rootView = inflater.inflate(R.layout.ac_live, container, false);

        initView(rootView);
        initData();
        loadData();
        Handler_Inject.injectFragment(this, rootView);
        EventBus.getDefault().register(this);

        return rootView;
    }

    @Override
    protected void init() {

    }


    @Override
    protected void click(View view) {
    }

    @Override
    public void onClick(View v) {
        Log.i(TAG, "you click view:" + v.getContentDescription());
        switch (v.getId()) {

            case R.id.fullscreenlin:
                if(list.size()>=1){
                    livevalue = list.get(0);
                    stopalllive();
                    intent = new Intent(getActivity(),PlayLiveFullScreenActivity.class);
                    intent.putExtra("livetype","1");
                    intent.putExtra("livevalue",livevalue+"");
                    startActivity(intent);
                }else{
                    showToast("暂无数据");
                }
                break;
            case R.id.fullscreenlin2:
                if(list.size()>=2){
                    livevalue = list.get(1);
                    stopalllive();
                    intent = new Intent(getActivity(),PlayLiveFullScreenActivity.class);
                    intent.putExtra("livetype","2");
                    intent.putExtra("livevalue",livevalue+"");
                    startActivity(intent);
                }else{
                    showToast("暂无数据");
                }
                break;
            case R.id.fullscreenlin3:
                if(list.size()>=3){
                    livevalue = list.get(2);
                    stopalllive();
                    intent = new Intent(getActivity(),PlayLiveFullScreenActivity.class);
                    intent.putExtra("livetype","3");
                    intent.putExtra("livevalue",livevalue+"");
                    startActivity(intent);
                }else{
                    showToast("暂无数据");
                }
                break;
            case R.id.playlin:
                if (playtv.getText().equals("播放")) {
                    if(list.size()>=1){
                        livevalue = list.get(0);
                        stopalllive();
                        type = 1;
                        clickStartBtn(mSurfaceView1,livevalue);
                        playImg.setImageResource(R.mipmap.stopbtn);
                        playtv.setText("停止");
                    }else{
                        showToast("暂无数据");
                    }
                } else if (playtv.getText().equals("停止")) {
                    clickStopBtn(mSurfaceView1);
                    playImg.setImageResource(R.mipmap.playbtn);
                    playtv.setText("播放");
                }
                break;
            case R.id.playlin2:
                if (playtv2.getText().equals("播放")) {
                    if(list.size()>=2){
                        livevalue = list.get(1);
                        stopalllive();
                        type = 2;
                        clickStartBtn(mSurfaceView2,livevalue);
                        playImg2.setImageResource(R.mipmap.stopbtn);
                        playtv2.setText("停止");

                    }else{
                        showToast("暂无数据");
                    }
                } else if (playtv2.getText().equals("停止")) {
                    clickStopBtn(mSurfaceView2);
                    playImg2.setImageResource(R.mipmap.playbtn);
                    playtv2.setText("播放");
                }
                break;
            case R.id.playlin3:
                if (playtv3.getText().equals("播放")) {
                    if(list.size()>=3){
                        livevalue = list.get(2);
                        stopalllive();
                        type = 3;
                        clickStartBtn(mSurfaceView3,livevalue);
                        playImg3.setImageResource(R.mipmap.stopbtn);
                        playtv3.setText("停止");
                    }else{
                        showToast("暂无数据");
                    }
                } else if (playtv3.getText().equals("停止")) {
                    clickStopBtn(mSurfaceView3);
                    playImg3.setImageResource(R.mipmap.playbtn);
                    playtv3.setText("播放");
                }
                break;
            default:
                break;
        }
    }


    /**
     * start play video
     */
    private void clickStartBtn(CustomSurfaceView mSurfaceView,int type) {

        showProgressDialog(getActivity(),"启动中...");
        SubResourceNodeBean bean = new SubResourceNodeBean();
        bean.setId(type);
        camera = VMSNetSDK.getInstance().initCameraInfo((SubResourceNodeBean) bean);

        getCameraInfo();

    }

    /**
     * stop play video
     */
    private void clickStopBtn(CustomSurfaceView mSurfaceView) {
        type = -1;
        if (null != mLiveControl) {
            mLiveControl.stop();
//            mSurfaceView.setBackgroundResource(R.mipmap.liveplaybg);
        }
//        mSurfaceView.setBackgroundColor(Color.parseColor("#717E9E"));
        mSurfaceView.setBackgroundResource(R.mipmap.monitorlogo);
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

                    if(type!=-1){
                        String liveUrl = VMSNetSDK.getInstance().getPlayUrl(cameraInfo, mStreamType);
                        mLiveControl.setLiveParams(liveUrl, null == username ? "" : username, null == password ? "" : password);
                        if (LiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
                            mLiveControl.stop();
                        }
                        if (LiveControl.LIVE_INIT == mLiveControl.getLiveState()) {
                            switch (type){
                                case 1:
                                    mLiveControl.startLive(mSurfaceView1);
                                    break;
                                case 2:
                                    mLiveControl.startLive(mSurfaceView2);
                                    break;
                                case 3:
                                    mLiveControl.startLive(mSurfaceView3);
                                    break;
                            }
                        }
                    }
                    getDeviceInfo();
                    break;
                case Constants.Live.getCameraInfo_failure:
                    dismissProgressDialog();
                    showToast(R.string.loading_camera_info_failure);
                    break;
                case Constants.Live.getDeviceInfo:
                    showToast(R.string.loading_device_info);
                    break;
                case Constants.Live.getDeviceInfo_Success:

                    username = deviceInfo.getUserName();
                    password = deviceInfo.getPassword();

//                    //进来默认播放
//                    clickStartBtn(mSurfaceView1,1);
//                    LogUtils.i("device infomation : username:" + username + "  password" + password);
                    break;
                case Constants.Live.getDeviceInfo_failure:
                    dismissProgressDialog();
                    showToast(R.string.loading_device_info_failure);
                    break;

                // 视频控制层回调的消息
                case ConstantLiveSDK.RTSP_FAIL:
                    showToast(R.string.rtsp_fail);
                    dismissProgressDialog();
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (null != mLiveControl) {
                        mLiveControl.stop();
                    }
                    break;
                case ConstantLiveSDK.RTSP_SUCCESS:
                    break;
                case ConstantLiveSDK.STOP_SUCCESS:
                    //停止成功
//                    showToast(R.string.live_stop_success);
                    break;
                case ConstantLiveSDK.START_OPEN_FAILED:
                    showToast(R.string.start_open_failed);
                    dismissProgressDialog();
                    break;
                case ConstantLiveSDK.PLAY_DISPLAY_SUCCESS:
                    dismissProgressDialog();
                    //播放成功
                    showToast("播放成功");
                    if (null != progressBar) {
                        progressBar.setVisibility(View.GONE);
                    }
                    if (playtv.getText().equals("停止")) {
                        mSurfaceView1.setBackgroundColor(Color.parseColor("#00ffffff"));
                    }
                    if (playtv2.getText().equals("停止")) {
                        mSurfaceView2.setBackgroundColor(Color.parseColor("#00ffffff"));
                    }
                    if (playtv3.getText().equals("停止")) {
                        mSurfaceView3.setBackgroundColor(Color.parseColor("#00ffffff"));
                    }
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
//        camera = (Camera) getActivity().getIntent().getSerializableExtra(Constants.IntentKey.CAMERA);
//        SubResourceNodeBean bean2 = new SubResourceNodeBean();
        //id=1, name=1F小一班活动室, nodeType=3, pid=1, isOnline=0, sysCode=ccd2b16740de4152bc96b0336a52ee8b
//        LogUtils.i("uMonitorid="+ App.app.getData("uMonitorid"));
        list = new ArrayList<>();
        listmTitle = new ArrayList<>();
        String uMonitorid = App.app.getData("uMonitorid");
        String uTitle = App.app.getData("uTitle");

        if(!TextUtils.isEmpty(uMonitorid)){
            if(uMonitorid.contains(";")){
                String[] aa = uMonitorid.split(";");
                for(int i=0;i<aa.length;i++){
                    list.add(Integer.parseInt(aa[i]));
                }
            }else{
                list.add(Integer.parseInt(uMonitorid));
            }
        }

        if(!TextUtils.isEmpty(uTitle)&&!uTitle.equals("null")){
            if(uTitle.contains(";")){
                String[] bb = uTitle.split(";");
                for(int i=0;i<bb.length;i++){
                    listmTitle.add(bb[i]);
                }
            }else{
                listmTitle.add(uTitle);
            }
            if(listmTitle.size()==1){
                liveTitle1.setText(listmTitle.get(0).toString());
            }
            if(listmTitle.size()==2){
                liveTitle1.setText(listmTitle.get(0).toString());
                liveTitle2.setText(listmTitle.get(1).toString());
            }
            if(listmTitle.size()>=3){
                liveTitle1.setText(listmTitle.get(0).toString());
                liveTitle2.setText(listmTitle.get(1).toString());
                liveTitle3.setText(listmTitle.get(2).toString());
            }
        }
        /**
         * 根据监控id查找监控信息
         */
//        bean.setId(Integer.parseInt(App.app.getData("uMonitorid")));
        SubResourceNodeBean bean = new SubResourceNodeBean();
        bean.setId(1);
        camera = VMSNetSDK.getInstance().initCameraInfo((SubResourceNodeBean) bean);
//        camera2 = VMSNetSDK.getInstance().initCameraInfo((SubResourceNodeBean) bean2);
        mVMSNetSDK = VMSNetSDK.getInstance();
        mHandler = new ViewHandler();
        mLiveControl = new LiveControl();
        mLiveControl.setLiveCallBack(this);

        /**
         * 默认开启电子放大
         * 视频点击事件
         */
//        mSurfaceView1.setOnZoomListener(new CustomSurfaceView.OnZoomListener() {
//
//            @Override
//            public void onZoomChange(CustomRect original, CustomRect current) {
////                intent = new Intent(getActivity(),PlayLiveFullScreenActivity.class);
////                intent.putExtra("livetype","1");
////                startActivity(intent);
////                PCRect o = new PCRect(original.getLeft(), original.getTop(), original.getRight(), original.getBottom());
////                PCRect c = new PCRect(current.getLeft(), current.getTop(), current.getRight(), current.getBottom());
////                if (null != mLiveControl) {
////                    mLiveControl.setDisplayRegion(true, o, c);
////                }
//            }
//        });
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
    private void initView(View view) {
        fullscreentv = (TextView) view.findViewById(R.id.fullscreentv);
        fullscreentv2 = (TextView) view.findViewById(R.id.fullscreentv2);
        fullscreentv3 = (TextView) view.findViewById(R.id.fullscreentv3);
        playtv = (TextView) view.findViewById(R.id.playtv);
        playtv2 = (TextView) view.findViewById(R.id.playtv2);
        playtv3 = (TextView) view.findViewById(R.id.playtv3);

        mSurfaceView1 = (CustomSurfaceView) view.findViewById(R.id.surfaceView1);
        mSurfaceView1.setOnClickListener(this);
        mSurfaceView2 = (CustomSurfaceView) view.findViewById(R.id.surfaceView2);
        mSurfaceView3 = (CustomSurfaceView) view.findViewById(R.id.surfaceView3);
        mSurfaceView1.getHolder().addCallback(this);
        mSurfaceView2.getHolder().addCallback(this);
        mSurfaceView3.getHolder().addCallback(this);

        progressBar = (ProgressBar) view.findViewById(R.id.live_progress_bar);

        playImg = (ImageView) view.findViewById(R.id.playImg);
        playImg2 = (ImageView) view.findViewById(R.id.playImg2);
        playImg3 = (ImageView) view.findViewById(R.id.playImg3);
        fullscreenlin = (LinearLayout) view.findViewById(R.id.fullscreenlin);
        fullscreenlin2 = (LinearLayout) view.findViewById(R.id.fullscreenlin2);
        fullscreenlin3 = (LinearLayout) view.findViewById(R.id.fullscreenlin3);
        playlin = (LinearLayout) view.findViewById(R.id.playlin);
        playlin2 = (LinearLayout) view.findViewById(R.id.playlin2);
        playlin3 = (LinearLayout) view.findViewById(R.id.playlin3);
        fullscreenlin.setOnClickListener(this);
        fullscreenlin2.setOnClickListener(this);
        fullscreenlin3.setOnClickListener(this);
        playlin.setOnClickListener(this);
        playlin2.setOnClickListener(this);
        playlin3.setOnClickListener(this);

        liveTitle1 = (TextView) view.findViewById(R.id.liveTitle1);
        liveTitle2 = (TextView) view.findViewById(R.id.liveTitle2);
        liveTitle3 = (TextView) view.findViewById(R.id.liveTitle3);
    }

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
//        if (null == camera1) {
//            Log.e(TAG, "getCameraInfo==>>camera is null");
//            return;
//        }
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
        mVMSNetSDK.getCameraInfo(camera);

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
//		if (flag) {
//			mHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_Success);
//		} else {
//			Log.e(TAG, "getDeviceInfo==>>request error");
//		}
    }

    /* (non-Javadoc)
* @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
*/
    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
    }


    private void stopalllive() {
        if (null != mLiveControl) {
            mLiveControl.stop();
        }

        if (playtv.getText().equals("停止")) {
            playtv.setText("播放");
            playImg.setImageResource(R.mipmap.playbtn);
            mSurfaceView1.setBackgroundResource(R.mipmap.monitorlogo);
//            mSurfaceView1.setBackgroundColor(Color.parseColor("#717E9E"));
        }
        if (playtv2.getText().equals("停止")) {
            playtv2.setText("播放");
            playImg2.setImageResource(R.mipmap.playbtn);
            mSurfaceView2.setBackgroundResource(R.mipmap.monitorlogo);
//            mSurfaceView2.setBackgroundColor(Color.parseColor("#717E9E"));
        }
        if (playtv3.getText().equals("停止")) {
            playtv3.setText("播放");
            playImg3.setImageResource(R.mipmap.playbtn);
            mSurfaceView3.setBackgroundResource(R.mipmap.monitorlogo);
//            mSurfaceView3.setBackgroundColor(Color.parseColor("#717E9E"));
        }

    }


    protected void onEventMainThread(PlayLiveEntity entity){
        showProgressDialog(getActivity(),"启动中...");
        switch (entity.getKey()){
           case 1:
//               //第一个播放界面
               type = 1;
               clickStartBtn(mSurfaceView1,livevalue);//获取详情  到进来自动播放
               playtv.setText("停止");
               playImg.setImageResource(R.mipmap.stopbtn);
               break;
           case 2:
               //第2个播放界面
               type=2;
               clickStartBtn(mSurfaceView2,livevalue);
               playtv2.setText("停止");
               playImg2.setImageResource(R.mipmap.stopbtn);
               break;
           case 3:
               //第3个播放界面
               type=3;
               clickStartBtn(mSurfaceView3,livevalue);
               playtv3.setText("停止");
               playImg3.setImageResource(R.mipmap.stopbtn);
               break;
       }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
