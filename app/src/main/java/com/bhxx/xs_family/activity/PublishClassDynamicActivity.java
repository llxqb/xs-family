package com.bhxx.xs_family.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.photo.AddImageGridAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.DynamicModel;
import com.bhxx.xs_family.beans.Item;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import okhttp3.Call;

@InjectLayer(R.layout.content_publish_class_dynamic)
public class PublishClassDynamicActivity extends BasicActivity {


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
    private String bb;//获取后台传过来的活动图片路径

    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView my_publishclass_back, main_img;
        EditText my_publishclass_title, my_publishclass_content;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button submit;
    }


    @Override
    protected void init() {
        ActivityCollector.addActivity(PublishClassDynamicActivity.this);
        EventBus.getDefault().register(this);
        if (!(PHOTO_DIR.exists() && PHOTO_DIR.isDirectory())) {
            PHOTO_DIR.mkdirs();
        }
        //添加图片
        gridView = (GridView) findViewById(R.id.my_class_photo);
        //加号图片
        addNewPic = BitmapFactory.decodeResource(this.getResources(), R.mipmap.photos_up);
        //addNewPic = PictureManageUtil.resizeBitmap(addNewPic, 180, 180);
        microBmList.add(addNewPic);
        imgAdapter = new AddImageGridAdapter(this, microBmList);
        gridView.setAdapter(imgAdapter);
        //事件监听，点击GridView里的图片时，在ImageView里显示出来
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                mainflag = 0;
                if (position == (photoList.size())) {
                    if (photoList.size() >= 6) {
                        showToast("已达到最大上传数");
                        return;
                    }
                    new ActionSheetDialog(PublishClassDynamicActivity.this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
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
                    Intent intent = new Intent(PublishClassDynamicActivity.this, ViewPagerDeleteActivity.class);
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
                int checkPermission = ContextCompat.checkSelfPermission(PublishClassDynamicActivity.this, Manifest.permission.CAMERA);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    (new AlertDialog.Builder(PublishClassDynamicActivity.this)).setMessage("您需要在设置里打开相机权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PublishClassDynamicActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
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

    private Intent getTakePickIntent(File f) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
        return intent;
    }

    // 请求Gallery程序
    protected void doPickPhotoFromGallery() {
        try {
            if (Build.VERSION.SDK_INT >= 23) {
                int checkPermission = ContextCompat.checkSelfPermission(PublishClassDynamicActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                    (new AlertDialog.Builder(PublishClassDynamicActivity.this)).setMessage("您需要在设置里打开存储空间权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(PublishClassDynamicActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
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
                dialog.show();
                Window window = dialog.getWindow();
                View view = window.getDecorView();
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        //初始化提示框
                        dialog.dismiss();
                    }
                }, 1000);
                //活动 相册 图片
                final Intent intent = new Intent(PublishClassDynamicActivity.this, PhotoAlbumActivity.class);
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


    private void showImg(String imagePath) {
        this.imagePath = imagePath;
        BitmapFactory.Options option = new BitmapFactory.Options();
        option.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, option);
        v.main_img.setImageBitmap(bitmap);
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
        super.onActivityResult(requestCode, resultCode, data);
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
                    path = getFilePathWithUri(data.getData(), PublishClassDynamicActivity.this);
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
                            //活动 拍照 图片
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

    @Override
    protected void click(View view) {

        switch (view.getId()) {
            case R.id.my_publishclass_back:
                finish();
                break;
            case R.id.main_img:
                mainflag = 1;
                new ActionSheetDialog(PublishClassDynamicActivity.this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
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
            case R.id.submit:
                submit();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, PublishClassDynamicActivity.class);
        context.startActivity(intent);
    }

    private void submit() {

        if (!TextUtils.isEmpty(photoListpath)) {
            photoListpath = "";
        }
        if (TextUtils.isEmpty(v.my_publishclass_title.getText().toString())) {
            showToast("标题不能为空");
            return;
        }

        if (TextUtils.isEmpty(imagePath)) {
            showToast("主图图片不能为空");
            return;
        }

        if (TextUtils.isEmpty(v.my_publishclass_content.getText().toString())) {
            showToast("内容不能为空");
            return;
        }
        if (photoList.size() == 0) {
            showToast("动态图片至少要有一张");
            return;
        }
        showProgressDialog(PublishClassDynamicActivity.this, "上传中...");
        for (Item item : photoList) {
            photoListpath = photoListpath + item.getPhotoPath() + ";";
        }

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
            showToast("上传超时");
            dismissProgressDialog();
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                CommonBean<String> listbean = JsonUtils.getBean(response, CommonBean.class, String.class);
                if (!TextUtils.isEmpty(listbean.getRows())) {
                    bb = listbean.getRows();
                    //上传文字 图片路径
                    LinkedHashMap<String, String> params = new LinkedHashMap<>();
                    params.put("dcPublisherId", App.app.getData("uId"));
                    params.put("dcTitle", v.my_publishclass_title.getText().toString());
                    params.put("dcMainPic", aa);
                    params.put("dcDesction", v.my_publishclass_content.getText().toString());
                    params.put("dcPics", bb);
                    String taoken = TokenUtils.getInstance().configParams(GlobalValues.PUBLISH_CLASSDYNAMIC + "?", params);
                    params.put("taoken", taoken);
                    MyOkHttp.postMap(GlobalValues.PUBLISH_CLASSDYNAMIC, "PUBLISH_CLASSDYNAMIC", params, new MyStringCallback());
                } else {
                    showToast("上传失败");
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
                CommonBean<DynamicModel> listbean = JsonUtils.getBean(response, CommonBean.class, DynamicModel.class);
                if (listbean.getRows() != null) {
                    showToast("上传成功");
                    finish();
                }

            }
        }
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
