//package com.hikvision.sdk.live;
//
//import android.app.Activity;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.util.Log;
//import android.view.SurfaceHolder;
//import android.view.View;
//import android.widget.Button;
//import android.widget.CheckBox;
//import android.widget.ProgressBar;
//import android.widget.RadioGroup;
//import android.widget.RadioGroup.OnCheckedChangeListener;
//
//import com.bhxx.xs_family.R;
//import com.bhxx.xs_family.utils.ActivityCollector;
//import com.hikvision.sdk.VMSNetSDK;
//import com.hikvision.sdk.consts.ConstantLiveSDK;
//import com.hikvision.sdk.consts.Constants;
//import com.hikvision.sdk.consts.PTZCmd;
//import com.hikvision.sdk.net.bean.Camera;
//import com.hikvision.sdk.net.bean.CameraInfo;
//import com.hikvision.sdk.net.bean.DeviceInfo;
//import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
//import com.hikvision.sdk.utils.UIUtil;
//import com.hikvision.sdk.utils.UtilAudioPlay;
//import com.hikvision.sdk.utils.Utils;
//import com.hikvision.sdk.widget.CustomRect;
//import com.hikvision.sdk.widget.CustomSurfaceView;
//import com.hikvision.sdk.widget.CustomSurfaceView.OnZoomListener;
//
//import java.util.Random;
//
//public class LiveActivity extends Activity implements View.OnClickListener, OnCheckedChangeListener, SurfaceHolder.Callback, LiveControl.LiveCallBack {
//
//	private static final String TAG = "LiveActivity";
//	/**
//	 * 监控点
//	 */
//	private Camera camera = null;
//	/**
//	 * 监控点详细信息
//	 */
//	private CameraInfo cameraInfo = new CameraInfo();
//	/**
//	 * 监控点关联的监控设备信息
//	 */
//	private DeviceInfo deviceInfo = null;
//	/**
//	 * sdk实例
//	 */
//	private VMSNetSDK mVMSNetSDK = null;
//
//	/**
//	 * 视图更新处理助手
//	 */
//	private Handler mHandler = null;
//	/**
//	 * 预览控件
//	 */
//	private CustomSurfaceView mSurfaceView = null;
//	/**
//	 * 设备账户名
//	 */
//	private String username = null;
//	/**
//	 * 设备登录密码
//	 */
//	private String password = null;
//	/**
//	 * 控制层对象
//	 */
//	private LiveControl mLiveControl = null;
//	/**
//	 * 开始播放按钮
//	 */
//	private Button startBtn = null;
//	/**
//	 * 停止播放按钮
//	 */
//	private Button stopBtn = null;
//	/**
//	 * 抓拍按钮
//	 */
//	private Button captureBtn = null;
//
//	/**
//     * 录像按钮
//     */
//    private Button mRecordBtn;
//    /**
//     * 音频按钮
//     */
//    private Button mAudioBtn;
//    /**
//     * 云台控制
//     */
//    private Button mPtzBtn;
//    /**
//     * 码流切换
//     */
//    private RadioGroup mRadioGroup;
//    /**
//     * 码流类型
//     */
//    private int                 mStreamType     = ConstantLiveSDK.MAIN_HING_STREAM;
//
//	/**
//	 * 进度条
//	 */
//	private ProgressBar progressBar = null;
//
//	 /**
//     * 音频是否开启
//     */
//    private boolean             mIsAudioOpen;
//    /**
//     * 是否正在录像
//     */
//    private boolean             mIsRecord;
//    /**
//     * 码流切换
//     */
//    private RadioGroup mRadioGroupPtz;
//    /**
//     * 是否正在云台控制
//     */
//    private boolean             mIsPtzStart;
//    /**
//     * 云台控制命令
//     */
//    private int                 mPtzcommand;
//    /**
//     * 电子放大
//     */
//    private CheckBox mZoom;
//
//	/**
//	 * Called when a view has been clicked.
//	 *
//	 * @param v The view that was clicked.
//	 */
//	@Override
//	public void onClick(View v) {
//		Log.i(TAG, "you click view:" + v.getContentDescription());
//		switch (v.getId()) {
//			case R.id.live_start:
//				clickStartBtn();
//				break;
//			case R.id.live_stop:
//				clickStopBtn();
//				break;
//			case R.id.live_capture:
//				clickCaptureBtn();
//				break;
//
//			case R.id.liveRecordBtn:
//                recordBtnOnClick();
//                break;
//
//            case R.id.liveAudioBtn:
//                audioBtnOnClick();
//                break;
//
//            case R.id.ptz_start:
//                ptzBtnOnClick();
//                break;
//
//            case R.id.zoom:
//                zoomBtnOnClick();
//                break;
//
//
//			default:
//				break;
//		}
//	}
//
//    /**
//     * @author lvlingdi 2016-5-6 上午10:31:05
//     */
//    private void zoomBtnOnClick() {
//        boolean isZoom = mZoom.isChecked();
//        if (isZoom) {
//            mSurfaceView.setOnZoomListener(new OnZoomListener() {
//
//                @Override
//                public void onZoomChange(CustomRect original, CustomRect current) {
//                    PCRect o = new PCRect(original.getLeft(), original.getTop(), original.getRight(), original.getBottom());
//                    PCRect c = new PCRect(current.getLeft(), current.getTop(), current.getRight(), current.getBottom());
//                    if (null != mLiveControl) {
//                        mLiveControl.setDisplayRegion(true, o, c);
//                    }
//                }
//            });
//        } else {
//            mSurfaceView.setOnZoomListener(null);
//            if (null != mLiveControl) {
//                mLiveControl.setDisplayRegion(false, null, null);
//            }
//        }
//
//    }
//
//	/**
//	 * start play video
//	 */
//	private void clickStartBtn() {
//		progressBar.setVisibility(View.VISIBLE);
//		String liveUrl = VMSNetSDK.getInstance().getPlayUrl(cameraInfo, mStreamType);
//		mLiveControl.setLiveParams(liveUrl, null == username ? "" : username, null == password ? "" : password);
//		if (LiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
//			mLiveControl.stop();
//		}
//
//		if (LiveControl.LIVE_INIT == mLiveControl.getLiveState()) {
//			mLiveControl.startLive(mSurfaceView);
//		}
//	}
//
//	/**
//	 * stop play video
//	 */
//	private void clickStopBtn() {
//		if (null != mLiveControl) {
//			mLiveControl.stop();
//		}
//	}
//
//	/**
//	 * capture picture from playing video
//	 */
//	private void clickCaptureBtn() {
//		if (null != mLiveControl) {
//			// 随机生成一个1到10000的数字，用于抓拍图片名称的一部分，区分图片，开发者可以根据实际情况修改
//			// 区分图片名称的方法
//			int recordIndex = new Random().nextInt(10000);
//			boolean ret = mLiveControl.capture(Utils.getPictureDirPath().getAbsolutePath(), "Picture" + recordIndex + ".jpg");
//			if (ret) {
//				UIUtil.showToast(LiveActivity.this, "抓拍成功");
//				UtilAudioPlay.playAudioFile(LiveActivity.this, R.raw.paizhao);
//			} else {
//				UIUtil.showToast(LiveActivity.this, "抓拍失败");
//				Log.e(TAG, "clickCaptureBtn():: 抓拍失败");
//			}
//		}
//	}
//
//	/**
//	 * 录像 void
//	 * @author lvlingdi 2016-4-26 下午3:35:57
//	 */
//    private void recordBtnOnClick() {
//        if (null != mLiveControl) {
//            if (!mIsRecord) {
//                // 随即生成一个1到10000的数字，用于录像名称的一部分，区分图片，开发者可以根据实际情况修改区分录像名称的方法
//                int recordIndex = new Random().nextInt(10000);
//                mLiveControl.startRecord(Utils.getVideoDirPath().getAbsolutePath(), "Video" + recordIndex
//                        + ".mp4");
//                mIsRecord = true;
//                UIUtil.showToast(LiveActivity.this, "启动录像成功");
//                mRecordBtn.setText("停止录像");
//            } else {
//                mLiveControl.stopRecord();
//                mIsRecord = false;
//                UIUtil.showToast(LiveActivity.this, "停止录像成功");
//                mRecordBtn.setText("开始录像");
//            }
//        }
//    }
//
//    /**
//     * 音频 void
//     * @author lvlingdi 2016-4-26 下午3:36:17
//     */
//    private void audioBtnOnClick() {
//        if (null != mLiveControl) {
//            if (mIsAudioOpen) {
//                mLiveControl.stopAudio();
//                mIsAudioOpen = false;
//                UIUtil.showToast(LiveActivity.this, "关闭音频");
//                mAudioBtn.setText("开启音频");
//            } else {
//                boolean ret = mLiveControl.startAudio();
//                if (!ret) {
//                    mIsAudioOpen = false;
//                    UIUtil.showToast(LiveActivity.this, "开启音频失败");
//                    mAudioBtn.setText("开音");
//                } else {
//                    mIsAudioOpen = true;
//                    // 开启音频成功，并不代表一定有声音，需要设备开启声音。
//                    UIUtil.showToast(LiveActivity.this, "开启音频成功");
//                    mAudioBtn.setText("关音");
//                }
//            }
//        }
//
//    }
//
//    private void ptzBtnOnClick() {
//        if (null != mLiveControl && LiveControl.LIVE_PLAY == mLiveControl.getLiveState()) {
//            if (mIsPtzStart) {
//                VMSNetSDK.getInstance().sendPTZCtrlCmd(cameraInfo, PTZCmd.ACTION_STOP, mPtzcommand);
//                mIsPtzStart = false;
//                mPtzBtn.setText("开始云台控制");
//            } else {
//                VMSNetSDK.getInstance().sendPTZCtrlCmd(cameraInfo, PTZCmd.ACTION_START, mPtzcommand);
//                mIsPtzStart = true;
//                mPtzBtn.setText("停止云台控制");
//            }
//        }
//    }
//
//	@Override
//	public void surfaceCreated(SurfaceHolder holder) {
//
//	}
//
//	@Override
//	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//	}
//
//	@Override
//	public void surfaceDestroyed(SurfaceHolder holder) {
//		if (null != mLiveControl) {
//			mLiveControl.stop();
//		}
//	}
//
//	/**
//	 * 视图更新处理器
//	 */
//	class ViewHandler extends Handler {
//
//		@Override
//		public void handleMessage(Message msg) {
//			switch (msg.what) {
//				case Constants.Live.getCameraInfo:
//					UIUtil.showProgressDialog(LiveActivity.this, R.string.loading_camera_info);
//					break;
//				case Constants.Live.getCameraInfo_Success:
//					UIUtil.cancelProgressDialog();
//					getDeviceInfo();
//					break;
//				case Constants.Live.getCameraInfo_failure:
//					UIUtil.cancelProgressDialog();
//					UIUtil.showToast(LiveActivity.this, R.string.loading_camera_info_failure);
//					break;
//				case Constants.Live.getDeviceInfo:
//					UIUtil.showProgressDialog(LiveActivity.this, R.string.loading_device_info);
//					break;
//				case Constants.Live.getDeviceInfo_Success:
//					UIUtil.cancelProgressDialog();
//					username = deviceInfo.getUserName();
//					password = deviceInfo.getPassword();
//					Log.i(TAG, "device infomation : username:" + username + "  password" + password);
//					break;
//				case Constants.Live.getDeviceInfo_failure:
//					UIUtil.cancelProgressDialog();
//					UIUtil.showToast(LiveActivity.this, R.string.loading_device_info_failure);
//					break;
//
//				// 视频控制层回调的消息
//				case ConstantLiveSDK.RTSP_FAIL:
//					UIUtil.showToast(LiveActivity.this, R.string.rtsp_fail);
//					if (null != progressBar) {
//						progressBar.setVisibility(View.GONE);
//					}
//					if (null != mLiveControl) {
//						mLiveControl.stop();
//					}
//					break;
//				case ConstantLiveSDK.RTSP_SUCCESS:
//					UIUtil.showToast(LiveActivity.this, R.string.rtsp_success);
//					break;
//				case ConstantLiveSDK.STOP_SUCCESS:
//					UIUtil.showToast(LiveActivity.this, R.string.live_stop_success);
//					break;
//				case ConstantLiveSDK.START_OPEN_FAILED:
//					UIUtil.showToast(LiveActivity.this, R.string.start_open_failed);
//					break;
//				case ConstantLiveSDK.PLAY_DISPLAY_SUCCESS:
//					UIUtil.showToast(LiveActivity.this, R.string.play_display_success);
//					if (null != progressBar) {
//						progressBar.setVisibility(View.GONE);
//						mSurfaceView.setBackgroundColor(Color.parseColor("#00ffffff"));
//					}
//					break;
//				default:
//					break;
//			}
//		}
//	}
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.ac_live);
//
//		ActivityCollector.addActivity(this);
//		initData();
//		initView();
//		loadData();
//	}
//
//	/**
//	 * init activity data
//	 */
//	private void initData() {
//		camera = (Camera)getIntent().getSerializableExtra(Constants.IntentKey.CAMERA);
//		mVMSNetSDK = VMSNetSDK.getInstance();
//		mHandler = new ViewHandler();
//		mLiveControl = new LiveControl();
//		mLiveControl.setLiveCallBack(this);
//	}
//
//	@Override
//    public void onMessageCallback(int message) {
//        sendMessageCase(message);
//    }
//
//    /**
//     * 发送消息
//     *
//     * @param i void
//     * @since V1.0
//     */
//    private void sendMessageCase(int i) {
//        if (null != mHandler) {
//            mHandler.sendEmptyMessage(i);
//        }
//    }
//
//	/**
//	 * init activity value
//	 */
//	private void initView() {
//		mSurfaceView = (CustomSurfaceView)findViewById(R.id.surfaceView1);
//		mSurfaceView.getHolder().addCallback(this);
//
//		startBtn = (Button)findViewById(R.id.live_start);
//		startBtn.setOnClickListener(this);
//
//		stopBtn = (Button)findViewById(R.id.live_stop);
//		stopBtn.setOnClickListener(this);
//
//		captureBtn = (Button)findViewById(R.id.live_capture);
//		captureBtn.setOnClickListener(this);
//
//		mRecordBtn = (Button) findViewById(R.id.liveRecordBtn);
//        mRecordBtn.setOnClickListener(this);
//        mRecordBtn.setText("启动录像");
//
//        mAudioBtn = (Button) findViewById(R.id.liveAudioBtn);
//        mAudioBtn.setOnClickListener(this);
//        mAudioBtn.setText("开启音频");
//
//        mRadioGroup = (RadioGroup) findViewById(R.id.radioGroup);
//        mRadioGroup.setOnCheckedChangeListener(this);
//        mStreamType = ConstantLiveSDK.MAIN_HING_STREAM;
//
//		progressBar = (ProgressBar)findViewById(R.id.live_progress_bar);
//
//		mPtzBtn = (Button) findViewById(R.id.ptz_start);
//		mPtzBtn.setOnClickListener(this);
//
//		mRadioGroupPtz = (RadioGroup) findViewById(R.id.radioGroup_ptz);
//		mRadioGroupPtz.setOnCheckedChangeListener(this);
//		mPtzcommand = PTZCmd.CUSTOM_CMD_UP;
//		mZoom = (CheckBox) findViewById(R.id.zoom);
//		mZoom.setOnClickListener(this);
//	}
//
//	/**
//	 * 打开预览
//	 */
//	private void loadData() {
//		getCameraInfo();
//	}
//
//	/**
//	 * 获取监控点详细信息
//	 */
//    private void getCameraInfo() {
//        if (null == camera) {
//            Log.e(TAG, "getCameraInfo==>>camera is null");
//            return;
//        }
//        mHandler.sendEmptyMessage(Constants.Live.getCameraInfo);
//        mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
//
//            @Override
//            public void onFailure() {
//                mHandler.sendEmptyMessage(Constants.Live.getCameraInfo_failure);
//
//            }
//
//            @Override
//            public void loading() {
//                mHandler.sendEmptyMessage(Constants.Live.getCameraInfo);
//
//            }
//
//            @Override
//            public void onSuccess(Object data) {
//                if (data instanceof CameraInfo) {
//                    cameraInfo = (CameraInfo) data;
//                    mHandler.sendEmptyMessage(Constants.Live.getCameraInfo_Success);
//                }
//            }
//
//        });
//       boolean flag = mVMSNetSDK.getCameraInfo(camera);
//
////        if (null != cameraInfo) {
////            mHandler.sendEmptyMessage(Constants.Live.getCameraInfo_Success);
////        } else {
////            mHandler.sendEmptyMessage(Constants.Live.getCameraInfo_failure);
////        }
//    }
//
//	/**
//	 * 获取设备信息
//	 */
//	private void getDeviceInfo() {
//		if (null == cameraInfo) {
//			Log.e(TAG, "getDeviceInfo==>>cameraInfo is null");
//			return;
//		}
//
//		mVMSNetSDK.setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
//
//            @Override
//            public void onFailure() {
//                mHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_failure);
//
//            }
//
//            @Override
//            public void loading() {
//                mHandler.sendEmptyMessage(Constants.Login.SHOW_LOGIN_PROGRESS);
//
//            }
//
//            @Override
//            public void onSuccess(Object data) {
//                if (data instanceof DeviceInfo) {
//                    deviceInfo = (DeviceInfo) data;
//                    mHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_Success);
//                }
//            }
//
//        });
//
//		boolean flag = mVMSNetSDK.getDeviceInfo(cameraInfo.getDeviceID());
////		if (flag) {
////			mHandler.sendEmptyMessage(Constants.Live.getDeviceInfo_Success);
////		} else {
////			Log.e(TAG, "getDeviceInfo==>>request error");
////		}
//	}
//
//    /* (non-Javadoc)
//     * @see android.widget.RadioGroup.OnCheckedChangeListener#onCheckedChanged(android.widget.RadioGroup, int)
//     */
//    @Override
//    public void onCheckedChanged(RadioGroup group, int checkedId) {
//        //
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
//
//    }
//
//    private void getPtzcommand(RadioGroup group) {
//        switch (group.getCheckedRadioButtonId()) {
//            case R.id.rb_up:
//                mPtzcommand = PTZCmd.CUSTOM_CMD_UP;
//            break;
//
//            case R.id.rb_down:
//                mPtzcommand = PTZCmd.CUSTOM_CMD_DOWN;
//            break;
//
//            case R.id.rb_left:
//                mPtzcommand = PTZCmd.CUSTOM_CMD_LEFT;
//            break;
//
//            case R.id.rb_right:
//                mPtzcommand = PTZCmd.CUSTOM_CMD_RIGHT;
//            break;
//
//            default:
//                break;
//        }
//    }
//
//    private void getStreamType(RadioGroup group) {
//        switch (group.getCheckedRadioButtonId()) {
//            case R.id.main_hing_Radio:
//                mStreamType = ConstantLive.MAIN_HING_STREAM;
//            break;
//
//            case R.id.main_standard_Radio:
//                mStreamType = ConstantLive.MAIN_STANDARD_STREAM;
//            break;
//
//            case R.id.sub_Radio:
//                mStreamType = ConstantLive.SUB_STREAM;
//            break;
//
//            default:
//                break;
//        }
//    }
//
//}
