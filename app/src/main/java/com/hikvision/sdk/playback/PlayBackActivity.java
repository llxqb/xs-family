/*
 * @ProjectName: 智能楼宇
 * @Copyright: 2013 HangZhou Hikvision System Technology Co., Ltd. All Right Reserved.
 * @address: http://www.hikvision.com
 * @date: 2016-4-19 上午10:24:22
 * @Description: 本内容仅限于杭州海康威视系统技术公有限司内部使用，禁止转发.
 */
package com.hikvision.sdk.playback;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.hik.mcrsdk.rtsp.RtspClient;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.ConstantPlayBack.PlayBack;
import com.hikvision.sdk.consts.Constants;
import com.hikvision.sdk.live.PCRect;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.CameraInfo;
import com.hikvision.sdk.net.bean.DeviceInfo;
import com.hikvision.sdk.net.bean.RecordInfo;
import com.hikvision.sdk.net.bean.RecordSegment;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.utils.SDKUtil;
import com.hikvision.sdk.utils.UIUtil;
import com.hikvision.sdk.utils.UtilAudioPlay;
import com.hikvision.sdk.utils.Utils;
import com.hikvision.sdk.widget.CustomRect;
import com.hikvision.sdk.widget.CustomSurfaceView;
import com.hikvision.sdk.widget.CustomSurfaceView.OnZoomListener;

import org.MediaPlayer.PlayM4.Player;

import java.util.Calendar;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

/**
 * <p></p>
 * @author lvlingdi 2016-4-19 上午10:24:22
 * @version V1.0   
 * @modificationHistory=========================逻辑或功能性重大变更记录
 * @modify by user: {修改人} 2016-4-19
 * @modify by reason:{方法名}:{原因}
 */
public class PlayBackActivity extends Activity implements PlayBackCallBack, OnClickListener {
    
    /**
     * 日志
     */
    private static final String TAG  = PlayBackActivity.class.getSimpleName();
    private static final int PROGRESS_MAX_VALUE = 100;
    /**
     * 播放视图控件
     */
    private CustomSurfaceView         mSurfaceView;
    /**
     * 存储介质选择
     */
    private RadioGroup mStorageTypesRG;
    /**
     * 开始按钮
     */
    private Button mStartButton;
    /**
     * 停止按钮
     */
    private Button mStopButton;
    /**
     * 暂停按钮
     */
    private Button mPauseButton;
    /**
     * 抓拍按钮
     */
    private Button mCaptureButton;
    /**
     * 录像按钮
     */
    private Button mRecordButton;
    /**
     * 音频按钮
     */
    private Button mAudioButton;
    /** 等待框 */
    private ProgressBar mProgressBar;
    /**
     * 控制层对象
     */
    private PlayBackControl mPlayBackControl;
    /**
     * 创建消息对象
     */
    private Handler mMessageHandler;
    /**
     * 是否暂停标签
     */
    private boolean             mIsPause;

    /**
     * 音频是否开启
     */
    private boolean             mIsAudioOpen;
    /**
     * 是否正在录像
     */
    private boolean             mIsRecord;

    private VMSNetSDK           mVMSNetSDK;
    /**
     * 监控点略况
     */
    private Camera              mCamera;
    /**
     * 监控点详情
     */
    private CameraInfo          mCameraInfo;
    /**
     * 设备详情
     */
    private DeviceInfo          mDeviceInfo;
    /**
     * 录像存储介质
     */
    private int[] mRecordPos;
    /**
     * 录像唯一标识,与录像存储介质一一对应
     */
    private String[] mGuids;
    /**
     * 存储介质
     */
    private int mStorageType;
    /**
     * 录像唯一标识Guid
     */
    private String mGuid;
    /**
     * 录像详情
     */
    private RecordInfo mRecordInfo;
    /**
     * 开始时间
     */
    private Calendar mStartTime;
    /**
     * 结束时间
     */
    private Calendar mEndTime;

    /**
     * 回放时的参数对象
     */
    private PlayBackParams      mParamsObj;
    /**
     * 录像片段
     */
    private RecordSegment       mRecordSegment;
    /**
     * 播放进度条
     */
    private SeekBar mProgressSeekbar = null;
    /**
     * 定时器
     */
    private Timer mUpdateTimer = null;
    /**
     * 定时器执行的任务
     */
    private TimerTask mUpdateTimerTask = null;
    /**
     * 电子放大
     */
    private CheckBox mZoom;

    /* (non-Javadoc)
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.play_back);
        ActivityCollector.addActivity(this);
        initView();
        initData();
        queryPlaybackInfo();
    }

    /**
     *
     * @author lvlingdi 2016-4-19 下午5:20:35
     */
    private void initView() {
        mSurfaceView = (CustomSurfaceView) findViewById(R.id.playbackSurfaceView);

        mStorageTypesRG = (RadioGroup) findViewById(R.id.rg_storage_type);

        mStartButton = (Button) findViewById(R.id.playBackStart);
        mStartButton.setOnClickListener(this);

        mStopButton = (Button) findViewById(R.id.playBackStop);
        mStopButton.setOnClickListener(this);

        mPauseButton = (Button) findViewById(R.id.playBackPause);
        mPauseButton.setOnClickListener(this);

        mCaptureButton = (Button) findViewById(R.id.playBackCapture);
        mCaptureButton.setOnClickListener(this);

        mRecordButton = (Button) findViewById(R.id.playBackRecord);
        mRecordButton.setOnClickListener(this);

        mAudioButton = (Button) findViewById(R.id.playBackRadio);
        mAudioButton.setOnClickListener(this);

        mZoom = (CheckBox) findViewById(R.id.zoom);
        mZoom.setOnClickListener(this);

        mProgressBar = (ProgressBar) findViewById(R.id.playBackProgressBar);
        mProgressBar.setVisibility(View.GONE);

        mProgressSeekbar = (SeekBar) findViewById(R.id.progress_seekbar);

        mProgressSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar arg0) {
                stopBtnOnClick();
                int progress = arg0.getProgress();
                if (progress == PROGRESS_MAX_VALUE) {
                    stopBtnOnClick();
                    return;
                }
                long begin = mStartTime.getTimeInMillis();
                long end = mEndTime.getTimeInMillis();
                long avg = (end - begin) / PROGRESS_MAX_VALUE;
                long trackTime = begin + (progress * avg);
                Calendar track = Calendar.getInstance();
                track.setTimeInMillis(trackTime);
                mParamsObj.startTime = SDKUtil.calendarToABS(track);
                startBtnOnClick();
            }

            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar arg0) {
            }

            /**
             * 拖动条进度改变的时候调用
             */
            @Override
            public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

            }
        });
    }

    /**
     * 初始化数据
     * @author lvlingdi 2016-4-19 下午5:20:50
     */
    private void initData() {
        mMessageHandler = new MyHandler();
        mVMSNetSDK = VMSNetSDK.getInstance();
        // 初始化远程回放控制层对象
        mPlayBackControl = new PlayBackControl();
        // 设置远程回放控制层回调
        mPlayBackControl.setPlayBackCallBack(this);
        // 创建远程回放需要的参数
        mParamsObj = new PlayBackParams();
        // 播放控件
        mParamsObj.surfaceView = mSurfaceView;
        //监控点
        mCamera = (Camera)getIntent().getSerializableExtra(Constants.IntentKey.CAMERA);


        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        mStartTime = Calendar.getInstance();
        mEndTime = Calendar.getInstance();

        mStartTime.set(year, month, day, 0, 0, 0);
        mEndTime.set(year, month, day, 23, 59, 59);

    }

    /* (non-Javadoc)
     * @see android.app.Activity#onDestroy()
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopBtnOnClick();
    }

    /**
     * 远程回放录像查询
     * @author lvlingdi 2016-4-20 上午9:46:42
     */
    public void queryPlaybackInfo() {
        getCameraInfo();
    }

    /* (non-Javadoc)
     * @see com.hikvision.sdk.palypack.PlayBackCallBack#onMessageCallback(int)
     */
    @Override
    public void onMessageCallback(int message) {
        sendMessageCase(message);
    }

    /**
     * 开启播放
     * @author lvlingdi 2016-4-19 下午5:01:22
     */
    private void startBtnOnClick() {
        mProgressBar.setVisibility(View.VISIBLE);
        if (null != mPlayBackControl) {
            mPlayBackControl.startPlayBack(mParamsObj);
        }
    }

    /**
     * 设置回放参数
     * @author lvlingdi 2016-4-21 下午4:41:19
     */
    private void setParamsObj() {
        if (null != mDeviceInfo) {
            mParamsObj.name = mDeviceInfo.getUserName() == null ? "" : mDeviceInfo.getUserName() ;
            mParamsObj.passwrod = mDeviceInfo.getPassword() == null ? "" : mDeviceInfo.getPassword();
        }
        if (null != mRecordInfo) {
            String rtspUri = VMSNetSDK.getInstance().getPlayBackRtspUrl(mCameraInfo, mRecordInfo.getSegmentListPlayUrl(), mStartTime, mEndTime);
            mParamsObj.startTime = SDKUtil.calendarToABS(mStartTime);
            mParamsObj.endTime = SDKUtil.calendarToABS(mEndTime);
            mParamsObj.url = rtspUri;
        }
    }

    /**
     * 停止播放
     * @author lvlingdi 2016-4-19 下午5:11:56
     */
    private void stopBtnOnClick() {
        mProgressBar.setVisibility(View.GONE);
        if (null != mPlayBackControl) {
            mPlayBackControl.stopPlayBack();
        }
        stopUpdateTimer();
    }

    /**
     * 暂停、回放播放 void
     * @author lvlingdi 2016-4-19 下午5:13:35
     */
    private void pauseBtnOnClick() {
        if (null == mPlayBackControl) {
            return;
        }
        if (!mIsPause) {
            mPlayBackControl.pausePlayBack();
        } else {
            mPlayBackControl.resumePlayBack();
        }
    }

    /**
     * 抓拍
     * @author lvlingdi 2016-4-26 下午3:13:33
     */
    private void captureBtnOnClick() {
        if (null != mPlayBackControl) {
            // 随即生成一个1到10000的数字，用于抓拍图片名称的一部分，区分图片
            int recordIndex = new Random().nextInt(10000);
            boolean ret = mPlayBackControl.capture(Utils.getPictureDirPath().getAbsolutePath(), "Picture" + recordIndex
                    + ".jpg");
            if (ret) {
                UIUtil.showToast(PlayBackActivity.this, "抓拍成功");
                UtilAudioPlay.playAudioFile(PlayBackActivity.this, R.raw.paizhao);
            } else {
                UIUtil.showToast(PlayBackActivity.this, "抓拍失败");
                Log.e(TAG, "captureBtnOnClick():: 抓拍失败");
            }
        }
    }

    /**
     * 录像 void
     * @author lvlingdi 2016-4-26 下午3:15:38
     */
    private void recordBtnOnClick() {
        if (null != mPlayBackControl) {
            if (!mIsRecord) {
                int recordIndex = new Random().nextInt(10000);
                boolean ret = mPlayBackControl.startRecord(Utils.getVideoDirPath().getAbsolutePath(), "Video" + recordIndex + ".mp4");
                if (ret) {
                    mIsRecord = true;
                    UIUtil.showToast(PlayBackActivity.this, "启动录像成功");
                    mRecordButton.setText("停止录像");
                } else {
                    mIsRecord = false;
                    UIUtil.showToast(PlayBackActivity.this, "启动录像失败");
                }
            } else {
                mPlayBackControl.stopRecord();
                mIsRecord = false;
                UIUtil.showToast(PlayBackActivity.this, "停止录像成功");
                mRecordButton.setText("开始录像");
            }
        }
    }

    /**
     * 音频按钮 void
     * @author lvlingdi 2016-4-26 下午3:20:35
     */
    private void audioBtnOnClick() {
        if (null != mPlayBackControl) {
            if (mIsAudioOpen) {
                mPlayBackControl.stopAudio();
                mIsAudioOpen = false;
                UIUtil.showToast(PlayBackActivity.this, "关闭音频");
                mAudioButton.setText("开启音频");
            } else {
                boolean ret = mPlayBackControl.startAudio();
                if (!ret) {
                    mIsAudioOpen = false;
                    UIUtil.showToast(PlayBackActivity.this, "开启音频失败");
                } else {
                    mIsAudioOpen = true;
                    // 开启音频成功，并不代表一定有声音，需要设备开启声音。
                    UIUtil.showToast(PlayBackActivity.this, "开启音频成功");
                    mAudioButton.setText("关闭音频");
                }
            }
        }
    }

    /**
     * @author lvlingdi 2016-5-6 上午10:31:05
     */
    private void zoomBtnOnClick() {
        boolean isZoom = mZoom.isChecked();
        if (isZoom) {
            mSurfaceView.setOnZoomListener(new OnZoomListener() {

                @Override
                public void onZoomChange(CustomRect original, CustomRect current) {
                    PCRect o = new PCRect(original.getLeft(), original.getTop(), original.getRight(), original.getBottom());
                    PCRect c = new PCRect(current.getLeft(), current.getTop(), current.getRight(), current.getBottom());
                    if (null != mPlayBackControl) {
                        mPlayBackControl.setDisplayRegion(true, o, c);
                    }
                }
            });
        } else {
            mSurfaceView.setOnZoomListener(null);
            if (null != mPlayBackControl) {
                mPlayBackControl.setDisplayRegion(false, null, null);
            }
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.playBackStart:

                startBtnOnClick();
            break;

            case R.id.playBackStop:
                stopBtnOnClick();
            break;

            case R.id.playBackPause:
                pauseBtnOnClick();
            break;

            case R.id.playBackCapture:
                captureBtnOnClick();
            break;

            case R.id.playBackRecord:
                recordBtnOnClick();
            break;

            case R.id.playBackRadio:
                audioBtnOnClick();
            break;

            case R.id.zoom:
                zoomBtnOnClick();
                break;

        }
    }

    private void sendMessageCase(int i) {
        if (null != mMessageHandler) {
            mMessageHandler.sendEmptyMessage(i);
        }
    }

    class MyHandler extends Handler {
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case Constants.PlayBack.getCameraInfo:
                    UIUtil.showProgressDialog(PlayBackActivity.this, R.string.loading_camera_info);
                    break;
                case Constants.PlayBack.getCameraInfo_Success:
                    UIUtil.cancelProgressDialog();
                    mRecordPos = processStorageType(mCameraInfo);
                    mGuids = processGuid(mCameraInfo);
                    if (null != mRecordPos && 0 < mRecordPos.length) {
                        mStorageType = mRecordPos[0];
                    }
                    if (null != mGuids && 0 < mGuids.length) {
                        mGuid = mGuids[0];
                    }
                    getDeviceInfo();
                    break;
                case Constants.PlayBack.getCameraInfo_failure:
                    UIUtil.cancelProgressDialog();
                    UIUtil.showToast(PlayBackActivity.this, R.string.loading_camera_info_failure);
                    break;
                case Constants.PlayBack.getDeviceInfo:
                    UIUtil.showProgressDialog(PlayBackActivity.this, R.string.loading_device_info);
                    break;
                case Constants.PlayBack.getDeviceInfo_Success:
                    UIUtil.cancelProgressDialog();
                    initStorageTypeView();
                    if (null != mRecordPos && 0 < mRecordPos.length) {
                        queryRecordSegment();
                    }
                    break;
                case Constants.PlayBack.getDeviceInfo_failure:
                    UIUtil.cancelProgressDialog();
                    UIUtil.showToast(PlayBackActivity.this, R.string.loading_device_info_failure);
                    break;

                case Constants.PlayBack.queryRecordSegment_Success:
                    UIUtil.cancelProgressDialog();
                    setParamsObj();
                    startBtnOnClick();
                    break;

                case Constants.PlayBack.queryRecordSegment_failure:
                    UIUtil.cancelProgressDialog();
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    UIUtil.showToast(PlayBackActivity.this, "录像文件查询失败");
                    break;
                case ConstantPlayBack.START_RTSP_SUCCESS:
                    UIUtil.showToast(PlayBackActivity.this, "启动取流库成功");
                    startUpdateTimer();
                    mSurfaceView.setBackgroundColor(Color.parseColor("#00ffffff"));
                    break;

                case ConstantPlayBack.START_RTSP_FAIL:

                    UIUtil.showToast(PlayBackActivity.this, "启动取流库失败");
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                break;

                case ConstantPlayBack.PAUSE_SUCCESS:
                    UIUtil.showToast(PlayBackActivity.this, "暂停成功");
                    mPauseButton.setText("恢复");
                    mIsPause = true;
                break;

                case ConstantPlayBack.PAUSE_FAIL:
                    UIUtil.showToast(PlayBackActivity.this, "暂停失败");
                    mPauseButton.setText("暂停");
                    mIsPause = false;

                break;

                case ConstantPlayBack.RESUEM_FAIL:
                    UIUtil.showToast(PlayBackActivity.this, "恢复播放失败");
                    mPauseButton.setText("恢复");
                    mIsPause = true;
                break;

                case ConstantPlayBack.RESUEM_SUCCESS:
                    UIUtil.showToast(PlayBackActivity.this, "恢复播放成功");
                    mPauseButton.setText("暂停");
                    mIsPause = false;
                break;

                case ConstantPlayBack.START_OPEN_FAILED:
                    UIUtil.showToast(PlayBackActivity.this, "启动播放库失败");
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                break;

                case ConstantPlayBack.PLAY_DISPLAY_SUCCESS:
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                break;
                case ConstantPlayBack.CAPTURE_FAILED_NPLAY_STATE:
                    UIUtil.showToast(PlayBackActivity.this, "非播状态不能抓怕");
                break;
                case ConstantPlayBack.PAUSE_FAIL_NPLAY_STATE:
                    UIUtil.showToast(PlayBackActivity.this, "非播放状态不能暂停");
                break;
                case ConstantPlayBack.RESUEM_FAIL_NPAUSE_STATE:
                    UIUtil.showToast(PlayBackActivity.this, "非播放状态");
                break;

                case RtspClient.RTSPCLIENT_MSG_CONNECTION_EXCEPTION:
                    if (null != mProgressBar) {
                        mProgressBar.setVisibility(View.GONE);
                    }
                    UIUtil.showToast(PlayBackActivity.this, "RTSP链接异常");
                break;

                case ConstantPlayBack.MSG_REMOTELIST_UI_UPDATE:
                    updateRemotePlayUI();
                    break;

            }
        }

        /**
         * @author lvlingdi 2016-4-21 上午10:20:11
         */
        private void initStorageTypeView() {
            if (mRecordPos == null || mRecordPos.length <= 0) {
                return;
            }
            for (int i = 0;i < mRecordPos.length;i++) {
                RadioButton rb = new RadioButton(PlayBackActivity.this);
//                if (0 == i) {
//                    rb.setChecked(true);
//                }
                rb.setTag(i);
                switch (mRecordPos[i]) {
                    case PlayBack.RECORD_TYPE_NVT:
                        rb.setText(PlayBack.RECORD_TYPE_NVT_STR);
                        break;

                    case PlayBack.RECORD_TYPE_PU:
                        rb.setText(PlayBack.RECORD_TYPE_PU_STR);
                        break;

                    case PlayBack.RECORD_TYPE_NVR:
                        rb.setText(PlayBack.RECORD_TYPE_NVR_STR);
                        break;

                    case PlayBack.RECORD_TYPE_CVM:
                        rb.setText(PlayBack.RECORD_TYPE_CVM_STR);
                        break;

                    default:
                        break;
                }
                mStorageTypesRG.addView(rb);
                mStorageTypesRG.setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(RadioGroup arg0, int arg1) {
                        int radioButtonId = arg0.getCheckedRadioButtonId();
                        RadioButton rb = (RadioButton)findViewById(radioButtonId);
                        String type = rb.getTag().toString();
                        int index = Integer.valueOf(type);
                        if (null != mRecordPos && index < mRecordPos.length) {
                            mStorageType = mRecordPos[index];
                        }
                        if (null != mGuids && index < mGuids.length) {
                            mGuid = mGuids[index];
                        }
                        stopBtnOnClick();
                        mProgressBar.setVisibility(View.VISIBLE);
                        queryRecordSegment();
                    }
                });
            }

        }
    }

    /**
     * 解析录像存储类型
     * @author lvlingdi 2016-4-21 上午10:07:33
     * @param cameraInfo
     */
    private int[] processStorageType(CameraInfo cameraInfo) {
        String pos = cameraInfo.getRecordPos();
        if (SDKUtil.isEmpty(pos)) {
            return null;
        }
        String[] recordPos = pos.split(",");
        int[] types = new int[recordPos.length];
        for (int i = 0; i < recordPos.length; i++) {
            types[i] = Integer.valueOf(recordPos[i]);
        }
        return types;

    }
    /**
     * 解析Guid
     * @author lvlingdi 2016-4-21 上午10:09:12
     * @param cameraInfo
     */
    private String[] processGuid(CameraInfo cameraInfo) {
        String guid = cameraInfo.getGuid();
        if (SDKUtil.isEmpty(guid)) {
            return null;
        }
        String[] guids = guid.split(",");
        return guids;
    }

    /**
     * 获取监控点详细信息
     */
    private void getCameraInfo() {
        if (null == mCamera) {
            Log.e(TAG, "getCameraInfo==>>camera is null");
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo);
        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo_failure);

            }

            @Override
            public void loading() {
                mMessageHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);

            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof CameraInfo) {
                    mCameraInfo = (CameraInfo) data;
                    mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo_Success);
                }
            }

        });

          mVMSNetSDK.getCameraInfo(mCamera);
        //        if (flag) {
//            mMessageHandler.sendEmptyMessage(Constants.PlayBack.getCameraInfo_Success);
//        } else {
//            Log.e(TAG, "getCameraInfo==>>request error");
//        }
    }

    /**
     * 查找录像片段
     * @author lvlingdi 2016-4-21 下午3:30:18
     */
    private void queryRecordSegment() {
        if (null == mCameraInfo) {
            Log.e(TAG, "queryRecordSegment==>>cameraInfo is null");
            return;
        }
        mProgressBar.setVisibility(View.VISIBLE);
        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mMessageHandler.sendEmptyMessage(Constants.PlayBack.queryRecordSegment_failure);

            }

            @Override
            public void loading() {


            }

            @Override
            public void onSuccess(Object obj) {
                if (obj instanceof RecordInfo) {
                    mRecordInfo = ((RecordInfo)obj);

                    //级联设备的时候
                    if (null != mRecordInfo.getSegmentList() && 0 < mRecordInfo.getSegmentList().size())  {
                        mRecordSegment = mRecordInfo.getSegmentList().get(0);
                    }
                    mMessageHandler.sendEmptyMessage(Constants.PlayBack.queryRecordSegment_Success);
                }
            }

        });
        mVMSNetSDK.queryRecordSegment(mCameraInfo, mStartTime, mEndTime, mStorageType, mGuid);
    }
    /**
     * 获取设备信息
     */
    private void getDeviceInfo() {
        if (null == mCameraInfo) {
            Log.e(TAG, "getDeviceInfo==>>cameraInfo is null");
            return;
        }

        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {

            @Override
            public void onFailure() {
                mMessageHandler.sendEmptyMessage(Constants.PlayBack.getDeviceInfo_failure);

            }

            @Override
            public void loading() {
                mMessageHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);

            }

            @Override
            public void onSuccess(Object data) {
                if (data instanceof DeviceInfo) {
                    mDeviceInfo = (DeviceInfo) data;
                    mMessageHandler.sendEmptyMessage(Constants.PlayBack.getDeviceInfo_Success);
                }
            }

        });

        mVMSNetSDK.getDeviceInfo(mCameraInfo.getDeviceID());
    }

    private void updateRemotePlayUI() {
        if (null == mPlayBackControl) {
            return;
        }
        Player palyer = mPlayBackControl.getPlayer();
        int status = mPlayBackControl.getPlayBackState();
        if (palyer != null && status == PlayBackControl.PLAYBACK_PLAY) {
            long osd = mPlayBackControl.getOSDTime();
            handlePlayProgress(osd);
        }
    }
    /**
     * 
     * @author lvlingdi 2016-4-27 下午3:39:33
     * @param osdTime
     */
    private void handlePlayProgress(long osd) {
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(osd);
        long begin = mStartTime.getTimeInMillis();
        long end = mEndTime.getTimeInMillis();
        
        double x = ((osd - begin) * PROGRESS_MAX_VALUE) / (double) (end - begin);
        int progress = (int) x;
        mProgressSeekbar.setProgress(progress);
        int beginTimeClock = (int) ((osd - begin) / 1000);
//        updateTimeBucketBeginTime(beginTimeClock);
//        nextPlayPrompt(osd, end);
    }
    
    /**
     * 启动定时器
     *
     * @see
     * @since V1.0
     */
    private void startUpdateTimer() {
        stopUpdateTimer();
        // 开始录像计时
        mUpdateTimer = new Timer();
        mUpdateTimerTask = new TimerTask() {
            @Override
            public void run() {
                mMessageHandler.sendEmptyMessage(ConstantPlayBack.MSG_REMOTELIST_UI_UPDATE);
                
            }
        };
        // 延时1000ms后执行，1000ms执行一次
        mUpdateTimer.schedule(mUpdateTimerTask, 0, 1000);
    }
    
    /**
     * 停止定时器
     * @author lvlingdi 2016-4-27 下午3:49:36
     */
    private void stopUpdateTimer() {
        if (mUpdateTimer != null) {
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }

        if (mUpdateTimerTask != null) {
            mUpdateTimerTask.cancel();
            mUpdateTimerTask = null;
        }
    }
}
