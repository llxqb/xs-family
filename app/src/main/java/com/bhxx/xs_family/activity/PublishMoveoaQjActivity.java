package com.bhxx.xs_family.activity;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.DateDialog.showalltime.DateTimeTwoPickDialog;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.photo.AddImageGridAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.Item;
import com.bhxx.xs_family.beans.LeaveModel;
import com.bhxx.xs_family.entity.LookCheckStatusEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.FileUtils;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.PictureManageUtil;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.ActionSheetDialog;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

@InjectLayer(R.layout.activity_publish_moveoa_qj)
public class PublishMoveoaQjActivity extends BasicActivity {

    /* 用来标识请求照相功能的activity */
    private final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private final int PHOTO_PICKED_WITH_DATA = 3021;
    // GridView预览删除页面过来
    private final int PIC_VIEW_CODE = 2016;
    /* 拍照的照片存储位置 */
    private final File PHOTO_DIR = new File(
            Environment.getExternalStorageDirectory()
                    + "/xsfamily");
    private File mCurrentPhotoFile;// 照相机拍照得到的图片
    // 用来显示预览图
    private ArrayList<Bitmap> microBmList = new ArrayList<Bitmap>();
    // 所选图的信息(主要是路径)
    private ArrayList<Item> photoList = new ArrayList<Item>();
    private AddImageGridAdapter imgAdapter;
    private Bitmap addNewPic;
    private GridView gridView;//显示所有上传图片
    private String select = "";
    private Dialog dlg;
    StringBuffer sb;
    private String photoListpath = "";//多图片集合路径

    @InjectAll
    private Views v;
    private String 请输入请假申请理由;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView publish_moveoaqj_back;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        RelativeLayout publish_moveoaqj_type;
        TextView publish_moveoaqj_typetv;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        RelativeLayout publish_moveoaqj_starttime, publish_moveoaqj_endtime;
        TextView publish_moveoaqj_starttimeTv, publish_moveoaqj_endtimeTv;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button submit;
        EditText publish_moveoaqj_content;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        EventBus.getDefault().register(this);

        if (!(PHOTO_DIR.exists() && PHOTO_DIR.isDirectory())) {
            PHOTO_DIR.mkdirs();
        }
        //添加图片
        gridView = (GridView) findViewById(R.id.my_album_photo);
        //加号图片
        addNewPic = BitmapFactory.decodeResource(this.getResources(), R.mipmap.photos_up);
        microBmList.add(addNewPic);
        imgAdapter = new AddImageGridAdapter(this, microBmList);
        gridView.setAdapter(imgAdapter);
        //事件监听，点击GridView里的图片时，在ImageView里显示出来
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (position == (photoList.size())) {
                    if (photoList.size() >= 6) {
                        showToast("已达到最大上传数");
                        return;
                    }
                    new ActionSheetDialog(PublishMoveoaQjActivity.this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                            .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                                @Override
                                public void onClick(int which) {
                                    String status = Environment.getExternalStorageState();
                                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                                        //判断是否有SD卡
                                        doTakePhoto();// 用户点击了从照相机获取
                                    } else {
                                        showToast("没有SD卡");
                                    }
                                }
                            }).addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue, new ActionSheetDialog.OnSheetItemClickListener() {
                        @Override
                        public void onClick(int which) {
                            //打开选择图片界面
                            doPickPhotoFromGallery();
                        }
                    }).show();

                } else {
                    Intent intent = new Intent(PublishMoveoaQjActivity.this, ViewPagerDeleteActivity.class);
                    intent.putParcelableArrayListExtra("files", photoList);
                    intent.putExtra(ViewPagerActivity.CURRENT_INDEX, position);
                    startActivityForResult(intent, PIC_VIEW_CODE);
                }
            }
        });
    }

    protected void onEventMainThread(LookCheckStatusEntity entity) {
        photoList.remove(entity.getKey());
        imgAdapter.notifyDataSetChanged();
    }


    /**
     * 拍照获取图片
     */
    protected void doTakePhoto() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkPermission = ContextCompat.checkSelfPermission(PublishMoveoaQjActivity.this, Manifest.permission.CAMERA);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    (new android.app.AlertDialog.Builder(PublishMoveoaQjActivity.this)).setMessage("您需要在设置里打开相机权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PublishMoveoaQjActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                        }
                    }).setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null).create().show();
                    return;
                }
            }
            // 创建照片的存储目录
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
            startActivityForResult(intent, CAMERA_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            showToast("找不到相机");
        }
    }

    public String getPhotoFileName() {
        UUID uuid = UUID.randomUUID();
        return uuid + ".jpg";
    }

    public static Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkPermission = ContextCompat.checkSelfPermission(PublishMoveoaQjActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    (new android.app.AlertDialog.Builder(PublishMoveoaQjActivity.this)).setMessage("您需要在设置里打开存储空间权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PublishMoveoaQjActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                        }
                    }).setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null).create().show();
                    return;
                }
            }
            final ProgressDialog dialog;
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //设置为圆形
            dialog.setMessage("数据加载中...");
            dialog.setIndeterminate(false);//
            dialog.show();
            Window window = dialog.getWindow();
            View view = window.getDecorView();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    //初始化提示框
                    dialog.dismiss();
                }

            }, 1000);
            final Intent intent = new Intent(PublishMoveoaQjActivity.this, PhotoAlbumActivity.class);
            startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
        } catch (ActivityNotFoundException e) {
            showToast("图库中找不到照片");
        }
    }

    Handler handler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    imgAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
        }

        ;
    };

    /**
     * 处理其他页面返回数据
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK)
            return;
        switch (requestCode) {
            case PHOTO_PICKED_WITH_DATA: {
                // 调用Gallery返回的
                ArrayList<Item> tempFiles = new ArrayList<Item>();
                if (data == null)
                    return;
                tempFiles = data.getParcelableArrayListExtra("fileNames");

                if (tempFiles == null) {
                    return;
                }
                microBmList.remove(addNewPic);
                Bitmap bitmap;
                for (int i = 0; i < tempFiles.size(); i++) {
                    bitmap = MediaStore.Images.Thumbnails.getThumbnail(this.getContentResolver(), tempFiles.get(i).getPhotoID(), MediaStore.Images.Thumbnails.MICRO_KIND, null);
                    int rotate = PictureManageUtil.getCameraPhotoOrientation(tempFiles.get(i).getPhotoPath());
                    bitmap = PictureManageUtil.rotateBitmap(bitmap, rotate);
                    microBmList.add(bitmap);
                    photoList.add(tempFiles.get(i));
                }
                microBmList.add(addNewPic);
                imgAdapter.notifyDataSetChanged();
                break;
            }
            case CAMERA_WITH_DATA: {
                Long delayMillis = 0L;
                if (mCurrentPhotoFile == null) {
                    delayMillis = 500L;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 照相机程序返回的,再次调用图片剪辑程序去修剪图片
                        //去掉GridView里的加号
                        microBmList.remove(addNewPic);
                        Item item = new Item();
                        item.setPhotoPath(mCurrentPhotoFile.getAbsolutePath());
                        photoList.add(item);
                        //根据路径，得到一个压缩过的Bitmap（宽高较大的变成500，按比例压缩）
                        Bitmap bitmap = PictureManageUtil.getCompressBm(mCurrentPhotoFile.getAbsolutePath());
                        //获取旋转参数
                        int rotate = PictureManageUtil.getCameraPhotoOrientation(mCurrentPhotoFile.getAbsolutePath());
                        //把压缩的图片进行旋转
                        bitmap = PictureManageUtil.rotateBitmap(bitmap, rotate);
                        microBmList.add(bitmap);
                        microBmList.add(addNewPic);
                        Message msg = handler.obtainMessage(1);
                        msg.sendToTarget();
                    }
                }, delayMillis);
                break;
            }
            case PIC_VIEW_CODE: {
                ArrayList<Integer> deleteIndex = data.getIntegerArrayListExtra("deleteIndexs");
                if (deleteIndex.size() > 0) {
                    for (int i = deleteIndex.size() - 1; i >= 0; i--) {
                        microBmList.remove((int) deleteIndex.get(i));
                        photoList.remove((int) deleteIndex.get(i));
                    }
                }
                imgAdapter.notifyDataSetChanged();
                break;
            }
        }
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.publish_moveoaqj_back:
                finish();
                break;
            case R.id.publish_moveoaqj_type:
                qjdialog();
                break;
            case R.id.publish_moveoaqj_starttime:
                Timedialog("请选择请假开始日期", 0);
                break;
            case R.id.publish_moveoaqj_endtime:
                Timedialog("请选择请假结束日期", 1);
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PublishMoveoaQjActivity.class);
        context.startActivity(intent);
    }

    private void submit() {

        if (!TextUtils.isEmpty(photoListpath)) {
            photoListpath = "";
        }
        if (TextUtils.isEmpty(select)) {
            showToast("请输入请假类型");
            return;
        }
        if (v.publish_moveoaqj_starttimeTv.getText().toString().equals("请选择请假开始时间")) {
            showToast("请输入请假开始时间");
            return;
        }
        if (v.publish_moveoaqj_endtimeTv.getText().toString().equals("请选择请假结束时间")) {
            showToast("请输入请假结束时间");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(v.publish_moveoaqj_starttimeTv.getText().toString());
            endDate = sdf.parse(v.publish_moveoaqj_endtimeTv.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (endDate.getTime() - startDate.getTime() <= 0) {
            showToast("活动结束时间必须大于开始时间");
            return;
        }
        if (TextUtils.isEmpty(v.publish_moveoaqj_content.getText().toString())) {
            showToast("请输入请假申请理由");
            return;
        }
        if (photoList.size() > 4) {
            showToast("最多上传4张图片");
            return;
        }


        /**
         * 有图片先上传图片
         */
        if (photoList.size() > 0) {
            showProgressDialog(PublishMoveoaQjActivity.this, "上传中...");
            for (Item item : photoList) {
                photoListpath = photoListpath + item.getPhotoPath() + ";";
            }
            Log.i("Tag", "photoListpath=" + photoListpath);
            Map<String, File> map = new HashMap<>();
            String[] imgload = photoListpath.split(";");
            for (int i = 0; i < imgload.length; i++) {
                String paramName = FileUtils.getFileName(imgload[i]);
                File f = FileUtils.getUploadImageFile(imgload[i]);
                map.put(paramName, f);
            }
            MyOkHttp.multiFile(GlobalValues.UPLOADIMG, "UPLOADIMG", map, "myPic", new MyImgListCallback());
        } else {
            showProgressDialog(PublishMoveoaQjActivity.this, "上传中...");
            LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
            params.put("leTeacherId", App.app.getData("uId"));
            params.put("leTypeName", select);
            params.put("leDesction", v.publish_moveoaqj_content.getText().toString());
            params.put("leStartTime1", v.publish_moveoaqj_starttimeTv.getText().toString());
            params.put("leEndTime1", v.publish_moveoaqj_endtimeTv.getText().toString());
            //lePics 请假图片
            String taoken = TokenUtils.getInstance().configParams(GlobalValues.PUBLISH_QJ + "?", params);
            params.put("taoken", taoken);
            MyOkHttp.postMap(GlobalValues.PUBLISH_QJ, "PUBLICQJ", params, new MyStringCallback());
        }


    }

    private class MyImgListCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("上传超时");
            dismissProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> listBean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (!TextUtils.isEmpty(listBean.getRows())) {
                    LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
                    params.put("leTeacherId", App.app.getData("uId"));
                    params.put("leTypeName", select);
                    params.put("leDesction", v.publish_moveoaqj_content.getText().toString());
                    params.put("leStartTime1", v.publish_moveoaqj_starttimeTv.getText().toString());
                    params.put("leEndTime1", v.publish_moveoaqj_endtimeTv.getText().toString());
                    params.put("lePics", listBean.getRows());
                    //lePics 请假图片
                    String taoken = TokenUtils.getInstance().configParams(GlobalValues.PUBLISH_QJ + "?", params);
                    params.put("taoken", taoken);
                    MyOkHttp.postMap(GlobalValues.PUBLISH_QJ, "PUBLICQJ", params, new MyStringCallback());
                } else {
                    showToast("上传失败");
                }
            }

        }
    }


    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("请求失败");
            dismissProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                dismissProgressDialog();
                CommonBean<LeaveModel> leaveBean = JsonUtils.getBean(response, CommonBean.class, LeaveModel.class);
                if (leaveBean.isSuccess()) {
                    showToast("发布成功");
                    MoveoaQjActivity.flashUI();
                    finish();
                }
            } else {
                showToast("请求失败");
            }
        }
    }

    private void qjdialog() {
        new AlertDialog.Builder(PublishMoveoaQjActivity.this).setTitle("请假类型").setItems(
                new String[]{"病假", "事假", "调休"},
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                select = "病假";
                                v.publish_moveoaqj_typetv.setText(select);
                                break;
                            case 1:
                                select = "事假";
                                v.publish_moveoaqj_typetv.setText(select);
                                break;
                            case 2:
                                select = "调休";
                                v.publish_moveoaqj_typetv.setText(select);
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }


    private void Timedialog(String text, final int flag) {
        DateTimeTwoPickDialog dPickDialog;
        switch (flag) {
            case 0:
                dPickDialog = new DateTimeTwoPickDialog();
                dPickDialog.dateTimePicKDialog(PublishMoveoaQjActivity.this, v.publish_moveoaqj_starttimeTv, null);
                break;
            case 1:
                dPickDialog = new DateTimeTwoPickDialog();
                dPickDialog.dateTimePicKDialog(PublishMoveoaQjActivity.this, v.publish_moveoaqj_endtimeTv, null);
                break;
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
