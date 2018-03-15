package com.bhxx.xs_family.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

public class ActivityUtils {

	/**
	 * 跳转到裁剪页面
	 * @param activity
	 * @param requestcODE
	 */
	public static void startImageCropActivity(Activity activity,int requestCode,Uri sourceUri,Uri ouputUri){
		
		Intent intent = new Intent("com.android.camera.action.CROP");  
		   intent.setDataAndType(sourceUri, "image/*");  
		   intent.putExtra("crop", "true");//可裁剪  
		   intent.putExtra("aspectX", 1);  
		   intent.putExtra("aspectY", 1);  
		   intent.putExtra("outputX", 150);  
		   intent.putExtra("outputY", 150);  
		   intent.putExtra("scale", true);  
		   intent.putExtra(MediaStore.EXTRA_OUTPUT, ouputUri);  
		   intent.putExtra("return-data", false);//若为false则表示不返回数据  
		   intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());  
		   intent.putExtra("noFaceDetection", true);   
		   activity.startActivityForResult(intent, requestCode); 
	}
	
	/**
	 * 安装应用
	 * @param context
	 * @param uri
	 */
	public static void installApk(Context context,Uri uri){
		
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);

		/* 调用getMIMEType()来取得MimeType */
		String type = "application/vnd.android.package-archive";

		/* 设置intent的file与MimeType */
		intent.setDataAndType(uri, type);
		/*
		* 经过实验，发现无论是否成功安装，该Intent都返回result为0 具体结果如下： type requestCode
		* resultCode data 取消安装 10086 0 null 覆盖安装 10086 0 null 全新安装 10086 0
		* null
		*/
		context.startActivity(intent); 
	}
}
