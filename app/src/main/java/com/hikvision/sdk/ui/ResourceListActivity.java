package com.hikvision.sdk.ui;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.LogUtils;
import com.hikvision.sdk.VMSNetSDK;
import com.hikvision.sdk.consts.Constants;
import com.hikvision.sdk.data.TempDatas;
import com.hikvision.sdk.net.bean.Camera;
import com.hikvision.sdk.net.bean.RootCtrlCenter;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;
import com.hikvision.sdk.net.business.OnVMSNetSDKBusiness;
import com.hikvision.sdk.playback.PlayBackActivity;
import com.hikvision.sdk.utils.HttpConstants;
import com.hikvision.sdk.utils.UIUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ResourceListActivity extends ListActivity {
	
	private ArrayAdapter adapter = null;
	/**
	 * listitem显示数据
	 */
	private ArrayList<String> data = new ArrayList<String>();
	/**
	 * 资源源数据
	 */
	private ArrayList<Object> source = new ArrayList<Object>();
	
	private Handler mHandler = null;
	private Dialog dialog = null;
	
	private class ViewHandler extends Handler {
		
		/**
		 * Handle system messages here.
		 *
		 * @param msg
		 */
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			switch (msg.what) {
				case Constants.Resource.SHOW_LOADING_PROGRESS:
					showLoadingProgress();
					break;
				case Constants.Resource.CANCEL_LOADING_PROGRESS:
					cancelLoadingProgress();
					break;
				case Constants.Resource.LOADING_SUCCESS:
					cancelLoadingProgress();
					onloadingSuccess();
					break;
				case Constants.Resource.LOADING_FAILED:
					cancelLoadingProgress();
					onloadingFailed();
					break;
			}
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ActivityCollector.addActivity(this);

		LogUtils.i("addr="+App.app.getData("addr")+" data="+TempDatas.getIns().getLoginData());
		initeData();
		initeView();
	}
	
	/**
	 * 初始化视图
	 */
	private void initeView() {
		//ac  title
		View v = LayoutInflater.from(this).inflate(R.layout.ac_title,null);
		ImageView ac_back = (ImageView) v.findViewById(R.id.ac_back);
		getListView().addHeaderView(v);
		ac_back.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, data);
		setListAdapter(adapter);
		
		TextView emptyView = new TextView(this);
		((ViewGroup)getListView().getParent()).addView(emptyView);
		emptyView.setText(R.string.empty);
		emptyView.setTextSize(20f);
		emptyView.setGravity(Gravity.CENTER_HORIZONTAL);
		getListView().setEmptyView(emptyView);


	}
	
	/**
	 * 初始化数据
	 */
	private void initeData() {
		mHandler = new ViewHandler();
		Intent intent = getIntent();
		if (intent.hasExtra(Constants.IntentKey.GET_ROOT_NODE)) {
			// 初次加载根节点数据
			getRootControlCenter();
		} else
			if (intent.hasExtra(Constants.IntentKey.GET_SUB_NODE)) {
				// 加载子节点列表
				int parentNodeType = intent.getIntExtra(Constants.IntentKey.PARENT_NODE_TYPE, 0);
				int parentId = intent.getIntExtra(Constants.IntentKey.PARENT_ID, 0);
				getSubResourceList(parentNodeType, parentId);
			}
	}
	
	/**
	 * 加载进度条
	 */
	private void showLoadingProgress() {
		UIUtil.showProgressDialog(this, R.string.loading_process_tip);
	}
	
	/**
	 * 取消进度条
	 */
	private void cancelLoadingProgress() {
		UIUtil.cancelProgressDialog();
	}
	
	/**
	 * 加载失败
	 *
	 * @author hanshuangwu 2016年1月21日 下午3:25:52
	 */
	private void onloadingFailed() {
		UIUtil.showToast(this, R.string.loading_failed);
	}
	
	/**
	 * 加载成功
	 *
	 * @author hanshuangwu 2016年1月21日 下午3:25:59
	 */
	private void onloadingSuccess() {
		UIUtil.showToast(this, R.string.loading_success);
		adapter.notifyDataSetChanged();
	}
	
	/**
	 * 获取登录设备mac地址
	 *
	 * @return
	 */
	protected String getMacAddress() {
		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
		String mac = wm.getConnectionInfo().getMacAddress();
		return mac == null ? "" : mac;
	}
	
	/**
	 * This method will be called when an item in the list is selected. Subclasses should override. Subclasses can call
	 * getListView().getItemAtPosition(position) if they need to access the data associated with the selected item.
	 *
	 * @param l The ListView where the click happened
	 * @param v The view that was clicked within the ListView
	 * @param position The position of the view in the list
	 * @param id The row id of the item that was clicked
	 */
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {

		if(position==0)return;
		int nodeType = 0;
		Object node = source.get(position-1);
		LogUtils.i("position="+position);
		if (node instanceof RootCtrlCenter) {
			nodeType = Integer.parseInt(((RootCtrlCenter)node).getNodeType());
		} else
			if (node instanceof SubResourceNodeBean) {
				nodeType = ((SubResourceNodeBean)node).getNodeType();
			}
		if (HttpConstants.NodeType.TYPE_CAMERA_OR_DOOR == nodeType) {
			// 构造camera对象
			final Camera camera = VMSNetSDK.getInstance().initCameraInfo((SubResourceNodeBean)node);
			dialog = new AlertDialog.Builder(this).setSingleChoiceItems(R.array.camera_select_items, 0,
			        new DialogInterface.OnClickListener() {
				        
				        @Override
				        public void onClick(DialogInterface dialog, int which) {
					        dialog.dismiss();
					        switch (which) {
								case 0:
									// 预览
									if (VMSNetSDK.getInstance().isHasLivePermission(camera)) {
										gotoLive(camera);
									} else {
										UIUtil.showToast(ResourceListActivity.this, R.string.no_permission);
									}
									break;
								case 1:
									// 回放
									if (VMSNetSDK.getInstance().isHasPlayBackPermission(camera)) {
										gotoPlayBack(camera);
									} else {
										UIUtil.showToast(ResourceListActivity.this, R.string.no_permission);
									}
									break;
								default:
									break;
							}
						}
			        }).show();
			return;
		} else {
			// 请求此item的下级资源
			Object obj = source.get(position-1);
			int parentNodeType = 0;
			int pId = 0;
			if (obj instanceof RootCtrlCenter) {
				parentNodeType = Integer.parseInt(((RootCtrlCenter)obj).getNodeType());
				pId = ((RootCtrlCenter)obj).getId();
			} else
				if (obj instanceof SubResourceNodeBean) {
					parentNodeType = ((SubResourceNodeBean)obj).getNodeType();
					pId = ((SubResourceNodeBean)obj).getId();
				}
			Intent intent = new Intent(this, ResourceListActivity.class);
			intent.putExtra(Constants.IntentKey.GET_SUB_NODE, true);
			intent.putExtra(Constants.IntentKey.PARENT_NODE_TYPE, parentNodeType);
			intent.putExtra(Constants.IntentKey.PARENT_ID, pId);
			startActivity(intent);
		}
	}
	
	/**
	 * 监控点详情
	 * @param
	 */
	private void gotoDetail(Camera camera) {
		
	}
	
	/**
	 * 回放监控点
	 * @param
	 */
	private void gotoPlayBack(Camera camera) {
	    if (camera == null) {
            Log.e(Constants.LOG_TAG, "gotoPlayBack():: fail");
            return;
        }
        Intent it = new Intent(this, PlayBackActivity.class);
        it.putExtra(Constants.IntentKey.CAMERA, camera);
        startActivity(it);
	}
	
	/**
	 * 预览监控点
	 * @param camera
	 */
	private void gotoLive(Camera camera) {
		if (camera == null) {
			Log.e(Constants.LOG_TAG, "gotoLive():: fail");
			return;
		}
//		Intent it = new Intent(this, LiveActivity.class);
//		it.putExtra(Constants.IntentKey.CAMERA, camera);
//		startActivity(it);
	}
	
	/**
	 * 获取根控制中心
	 */
	public void getRootControlCenter() {
		boolean flag = false;
		VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
		    /* (non-Javadoc)
		     * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onSuccess(java.lang.Object)
		     */
		    @Override
		    public void onSuccess(Object obj) {
		        super.onSuccess(obj);
		        if (obj instanceof RootCtrlCenter) {
		            source.clear();
                    data.clear();
	                source.add((RootCtrlCenter)obj);
	                data.add(((RootCtrlCenter)obj).getName());
	                mHandler.sendEmptyMessage(Constants.Resource.LOADING_SUCCESS);
		        }
		    }
		    
		    /* (non-Javadoc)
		     * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onFailure()
		     */
		    @Override
		    public void onFailure() {
		        super.onFailure();
		        mHandler.sendEmptyMessage(Constants.Resource.LOADING_FAILED);
		    }
        });
		flag = VMSNetSDK.getInstance().getRootCtrlCenterInfo(1, HttpConstants.SysType.TYPE_VIDEO, 15);
	}
	
	/**
	 * 获取下级资源列表
	 *
	 * @param parentNodeType 父节点类型
	 * @param pId 父节点ID
	 */
	private void getSubResourceList(int parentNodeType, final int pId) {
		boolean flag = false;
		
		VMSNetSDK.getInstance().setOnVMSNetSDKBusiness(new OnVMSNetSDKBusiness() {
            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onSuccess(java.lang.Object)
             */
            @Override
            public void onSuccess(Object obj) {
                super.onSuccess(obj);
                if (obj instanceof List<?>) {
                    List<SubResourceNodeBean> list = new ArrayList<SubResourceNodeBean>();
                    list.addAll((Collection<? extends SubResourceNodeBean>) obj);
                    
                    data.clear();
                    source.clear();
                    if (null != list) {
                        for (SubResourceNodeBean bean : list) {
                            data.add(bean.getName());
                            source.add(bean);
                        }
//						LogUtils.logE("TAG",data.toString());
//						LogUtils.logE("TAG",list.toString());
                        mHandler.sendEmptyMessage(Constants.Resource.LOADING_SUCCESS);
                    }
                }
            }


            /* (non-Javadoc)
             * @see com.hikvision.sdk.net.business.OnVMSNetSDKBusiness#onFailure()
             */
            @Override
            public void onFailure() {
                super.onFailure();
                mHandler.sendEmptyMessage(Constants.Resource.LOADING_FAILED);
            }
        });
		
		flag = VMSNetSDK.getInstance().getSubResourceList( 1,
		        15, HttpConstants.SysType.TYPE_VIDEO, parentNodeType, pId + "");
	}
	
}
