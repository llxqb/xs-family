package com.bhxx.xs_family.activity;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.Item;
import com.bhxx.xs_family.entity.LookCheckStatusEntity;
import com.bhxx.xs_family.fragment.MyActItemFragment;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.FileUtils;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.PictureManageUtil;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.ActionSheetDialog;
import com.bhxx.xs_family.views.MyGridView;

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

@InjectLayer(R.layout.publish_action)
public class PublishActionActivity extends BasicActivity {

    /* 用来标识请求照相功能的activity */
    private final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private final int PHOTO_PICKED_WITH_DATA = 3021;
    private final int PHOTO_PICKED_WITH_DATA_ONCE = 1111;//主图相册图片
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

    private int mainflag = 0;//设置主图图片标示
    private String imagePath;//主图图片本地地址
    private String photoListpath = "";//多图片集合路径
    private String aa;//获取后台传过来的主图路径

    StringBuffer sb;
    private Dialog dlg;
    private String select;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView my_addaction_back, main_img;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        RelativeLayout my_addaction_humannum, my_addaction_applyEndTime;
        TextView human_num_tv, publish_action_starttimeTv, publish_action_endtimeTv, publish_action_applyEndTimeTv;
        EditText publish_action_title, my_addaction_content;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        RelativeLayout publish_action_starttime, publish_action_endtime;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button submit;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(PublishActionActivity.this);
        EventBus.getDefault().register(this);
        if (!(PHOTO_DIR.exists() && PHOTO_DIR.isDirectory())) {
            PHOTO_DIR.mkdirs();
        }
        //添加图片
        gridView = (GridView) findViewById(R.id.my_addaction_photo);
        //加号图片
        addNewPic = BitmapFactory.decodeResource(this.getResources(), R.mipmap.photos_up);
        //addNewPic = PictureManageUtil.resizeBitmap(addNewPic, 180, 180);
        microBmList.add(addNewPic);
        imgAdapter = new AddImageGridAdapter(this, microBmList);
        gridView.setAdapter(imgAdapter);
        //事件监听，点击GridView里的图片时，在ImageView里显示出来
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                ImageView deleteImg = (ImageView) v.findViewById(R.id.deleteImg);
                mainflag = 0;
                if (position == (photoList.size())) {
                    if(photoList.size() >= 6){
                        showToast("已达到最大上传数");
                        return;
                    }
                    new ActionSheetDialog(PublishActionActivity.this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
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
                    Intent intent = new Intent(PublishActionActivity.this, ViewPagerDeleteActivity.class);
                    intent.putParcelableArrayListExtra("files", photoList);
                    intent.putExtra(ViewPagerActivity.CURRENT_INDEX, position);
                    startActivityForResult(intent, PIC_VIEW_CODE);

                }
            }
        });
    }

    /**
     * 删除多图片中图片
     * @param entity
     */
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
                int checkPermission = ContextCompat.checkSelfPermission(PublishActionActivity.this, Manifest.permission.CAMERA);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    (new android.app.AlertDialog.Builder(PublishActionActivity.this)).setMessage("您需要在设置里打开相机权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PublishActionActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                        }
                    }).setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null).create().show();
                    return;
                }
            }
            // 创建照片的存储目录
            mCurrentPhotoFile = new File(PHOTO_DIR, getPhotoFileName());// 给新照的照片文件命名
            final Intent intent = getTakePickIntent(mCurrentPhotoFile);
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
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
                int checkPermission = ContextCompat.checkSelfPermission(PublishActionActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    (new android.app.AlertDialog.Builder(PublishActionActivity.this)).setMessage("您需要在设置里打开存储空间权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PublishActionActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                        }
                    }).setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null).create().show();
                    return;
                }
            }
            if (mainflag == 0) {
                final ProgressDialog dialog;
                dialog = new ProgressDialog(this);
                dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); //设置为圆形
                dialog.setMessage("数据加载中...");
                dialog.setIndeterminate(false);//
                //dialog.setCancelable(true);	//按回退键取消
                dialog.show();
                Window window = dialog.getWindow();
                View view = window.getDecorView();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //初始化提示框
                        dialog.dismiss();
                    }

                }, 1000);
                final Intent intent = new Intent(PublishActionActivity.this, PhotoAlbumActivity.class);
                startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
            } else {
                //主图 相册 图片
                Intent intent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PHOTO_PICKED_WITH_DATA_ONCE);
            }

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
            case PHOTO_PICKED_WITH_DATA_ONCE:
                //主图 相册
                String path = null;
                try {
                    path = getFilePathWithUri(data.getData(), PublishActionActivity.this);

                    if (!TextUtils.isEmpty(path)) {
                        showImg(path);
                    } else {
                        showToast("获取图片路径失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                break;
            case CAMERA_WITH_DATA: {
                Long delayMillis = 0L;
                if (mCurrentPhotoFile == null) {
                    delayMillis = 500L;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (mainflag == 0) {
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
                        } else {
                            //主图 拍照 图片
                            showImg(mCurrentPhotoFile.getAbsolutePath());
                        }

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

    /**
     * 通过URI获取文件的路径
     *
     * @param uri
     * @param activity
     * @return Author JPH
     * Date 2016/6/6 0006 20:01
     */
    public static String getFilePathWithUri(Uri uri, Activity activity) throws Exception {
        if (uri == null) {
            return "";
        }
        String picturePath = null;
        String scheme = uri.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = activity.getContentResolver().query(uri,
                    filePathColumn, null, null, null);//从系统表中查询指定Uri对应的照片
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            picturePath = cursor.getString(columnIndex);  //获取照片路径
            cursor.close();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            picturePath = uri.getPath();
        }
        return picturePath;
    }

    private void showImg(String imagePath) {
        this.imagePath = imagePath;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, option);
        v.main_img.setImageBitmap(bitmap);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.my_addaction_back:
                finish();
                break;
            case R.id.main_img:
                mainflag = 1;
                new ActionSheetDialog(PublishActionActivity.this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
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
                break;
            case R.id.publish_action_starttime:
                Timedialog("请选择活动开始日期", 0);
                break;
            case R.id.publish_action_endtime:
                Timedialog("请选择活动结束日期", 1);
                break;
            case R.id.my_addaction_applyEndTime:
                Timedialog("报名截止时间", 2);
                break;
            case R.id.my_addaction_humannum:
                actdialog();
                break;
            case R.id.submit:
                submit();
                break;

        }
    }

    private void submit() {
        if (!TextUtils.isEmpty(photoListpath)) {
            photoListpath = "";
        }

        if (TextUtils.isEmpty(v.publish_action_title.getText().toString())) {
            showToast("请填写活动标题");
            return;
        }
        if (v.publish_action_starttimeTv.getText().toString().equals("请选择活动开始时间")) {
            showToast("请输入活动开始时间");
            return;
        }
        if (v.publish_action_endtimeTv.getText().toString().equals("请选择活动结束时间")) {
            showToast("请输入活动结束时间");
            return;
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date startDate = null;
        Date endDate = null;
        try {
            startDate = sdf.parse(v.publish_action_starttimeTv.getText().toString());
            endDate = sdf.parse(v.publish_action_endtimeTv.getText().toString());
            Log.i("Tag", "startDate=" + startDate + "   endDate=" + endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (endDate.getTime() - startDate.getTime() <= 0) {
            showToast("活动结束时间必须大于开始时间");
            return;
        }

        if (v.publish_action_applyEndTimeTv.getText().toString().equals("请填写活动报名截止时间")) {
            showToast("请填写活动报名截止时间");
            return;
        }
        if (v.human_num_tv.getText().toString().equals("请选择")) {
            showToast("请选择人数限制");
            return;
        }
        if (TextUtils.isEmpty(imagePath)) {
            showToast("主图图片不能为空");
            return;
        }

        if (TextUtils.isEmpty(v.my_addaction_content.getText().toString())) {
            showToast("请填写活动描述");
            return;
        }
        showProgressDialog(PublishActionActivity.this, "上传中...");
        if (photoList.size() == 0) {
            showToast("动态图片至少要有一张");
            dismissProgressDialog();
            return;
        }

        for (Item item : photoList) {
            photoListpath = photoListpath + item.getPhotoPath() + ";";
        }
        Log.i("Tag", "photoListpath=" + photoListpath);

        Map<String, File> map = new HashMap<>();
        String paramName = FileUtils.getFileName(imagePath);
        File f = FileUtils.getUploadImageFile(imagePath);
        map.put(paramName, f);
        MyOkHttp.multiFile(GlobalValues.UPLOADIMG, "UPLOADIMG", map, "myPic", new MyImgCallback());


    }

    private class MyImgCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast("上传超时");
        }

        @Override
        public void onResponse(String response, int id) {
            CommonBean<String> listbean = JsonUtils.getBean(response, CommonBean.class, String.class);
            if (!TextUtils.isEmpty(listbean.getRows())) {
                aa = listbean.getRows();
                Map<String, File> map = new HashMap<>();
                String[] imgload = photoListpath.split(";");
                for (int i = 0; i < imgload.length; i++) {
                    String paramName = FileUtils.getFileName(imgload[i]);
                    File f = FileUtils.getUploadImageFile(imgload[i]);
                    map.put(paramName, f);
                }
                MyOkHttp.multiFile(GlobalValues.UPLOADIMG, "UPLOADIMG", map, "myPic", new MyImgListCallback());

            }
        }
    }

    private class MyImgListCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            dismissProgressDialog();
            showToast("上传超时");
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> listbean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (!TextUtils.isEmpty(listbean.getRows())) {
                    String picpath = listbean.getRows();
                    //上传文字 图片路径
                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("acPublisherId", App.app.getData("uId"));
                    params.put("acTitle", v.publish_action_title.getText().toString());
                    params.put("acStartTime1", v.publish_action_starttimeTv.getText().toString());
                    params.put("acEndTime1", v.publish_action_endtimeTv.getText().toString());
                    params.put("acCount", v.human_num_tv.getText().toString());
                    params.put("acDesction", v.my_addaction_content.getText().toString());
                    params.put("acEndSigeUpTime1", v.publish_action_applyEndTimeTv.getText().toString());
                    params.put("acMainPic", aa);
                    params.put("acPics", picpath);
                    String taoken = TokenUtils.getInstance().configParams(GlobalValues.PUBLISH_ACTION + "?", params);
                    params.put("taoken", taoken);
                    MyOkHttp.postMap(GlobalValues.PUBLISH_ACTION, "PUBLISH_ACTION", params, new MyStringCallback());
                } else {
                    showToast("发布失败");
                }
            }
        }
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("上传超时");
            dismissProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            dismissProgressDialog();
            if (!TextUtils.isEmpty(response)) {
                dismissProgressDialog();
                CommonBean<ActModel> listBean = JsonUtils.getBean(response, CommonBean.class, ActModel.class);
                if (listBean.isSuccess()) {
                    showToast("发布成功");
                    MyActItemFragment.flashui();
                    finish();
                }
            } else {
                showToast("发布失败");
            }
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PublishActionActivity.class);
        context.startActivity(intent);
    }

    private void Timedialog(String text, final int flag) {
        DateTimeTwoPickDialog dPickDialog;
        switch (flag){
            case 0:
                dPickDialog = new DateTimeTwoPickDialog();
                dPickDialog.dateTimePicKDialog(PublishActionActivity.this, v.publish_action_starttimeTv, text);
                break;
            case 1:
                dPickDialog = new DateTimeTwoPickDialog();
                dPickDialog.dateTimePicKDialog(PublishActionActivity.this, v.publish_action_endtimeTv, text);
                break;
            case 2:
                dPickDialog = new DateTimeTwoPickDialog();
                dPickDialog.dateTimePicKDialog(PublishActionActivity.this, v.publish_action_applyEndTimeTv, text);
                break;
        }
    }



    private void actdialog() {
        showDialog();
    }

    ArrayList dialoglist;

    private void showDialog() {
        dialoglist = new ArrayList();
        initdialoglist();
        // 获取Dialog布局
        View view = LayoutInflater.from(PublishActionActivity.this).inflate(R.layout.dynamic_check_dialog, null);
        WindowManager windowManager = (WindowManager) PublishActionActivity.this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        ImageView my_dynamicscheck_back = (ImageView) view.findViewById(R.id.my_dynamicscheck_back);
        ImageView my_dynamicscheck_add = (ImageView) view.findViewById(R.id.my_dynamicscheck_add);
        my_dynamicscheck_back.setVisibility(View.GONE);
        my_dynamicscheck_add.setVisibility(View.GONE);

        ListView dialog_listview = (ListView) view.findViewById(R.id.dialog_listview);
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_title.setText("请设置人数限制");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                PublishActionActivity.this,
                R.layout.dialog_list_item,//只能有一个定义了id的TextView
                dialoglist);
        dialog_listview.setAdapter(adapter);

        // 定义Dialog布局和参数
        final Dialog dialog = new Dialog(PublishActionActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        dialog.show();
        dialog_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                select = dialoglist.get(position).toString();
                v.human_num_tv.setText(select);
                dialog.dismiss();
            }
        });
    }

    private void initdialoglist() {
        for (int i = 5; i <= 100; i += 5) {
            dialoglist.add(i);
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
