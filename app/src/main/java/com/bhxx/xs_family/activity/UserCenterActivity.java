package com.bhxx.xs_family.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.DateDialog.widget.DatePicker;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.CommonBean;
import com.bhxx.xs_family.beans.StudentModel;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.entity.UploadPicEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.ActivityUtils;
import com.bhxx.xs_family.utils.CheckUtils;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.FileUtils;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.ActionSheetDialog;
import com.bhxx.xs_family.views.ActionSheetDialog.OnSheetItemClickListener;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;

import okhttp3.Call;

@InjectLayer(R.layout.activity_user_center)
public class UserCenterActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_PICK_IMAGE = 2;
    private static final int REQUEST_CODE_CROP = 3;
    private File takePhotFile;
    private File cropFile;
    private UserModel user;
    private String age = "";
    private String oldage = "";
    private String uBrithday = "";


    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView user_center_back, user_center_save;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        RelativeLayout user_center_head_layout;
        CircleImageView user_center_head_pic;
        EditText user_center_name, user_center_city;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView user_center_age;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        LinearLayout user_center_sex_layout;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView user_center_sex, user_center_child_name, user_center_child_age, user_center_child_sex, user_center_child_class;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(UserCenterActivity.this);
        initValue();
    }

    private void initValue() {
        if (!TextUtils.isEmpty(App.app.getData("uPic"))) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + App.app.getData("uPic"), v.user_center_head_pic, LoadImage.getHeadImgOptions());
        }
        if (App.app.readObjData("user") != null) {
            user = (UserModel) App.app.readObjData("user");
            if (!TextUtils.isEmpty(user.getuName())) {
                v.user_center_name.setText(user.getuName());
            }
            if (!TextUtils.isEmpty(user.getuCity())) {
                v.user_center_city.setText(user.getuCity());
            }
            v.user_center_age.setText(user.getuAge() + "");
            oldage = user.getuAge() + "";
            if (user.getuSex() == 0) {
                v.user_center_sex.setText("女");
            } else {
                v.user_center_sex.setText("男");
            }
            StudentModel student = user.getAppStudent();
            if (student != null) {
                if (!TextUtils.isEmpty(student.getStName())) {
                    v.user_center_child_name.setText(student.getStName());
                }
                v.user_center_child_age.setText(student.getStAge() + "");

                if (student.getStSex() == 0) {
                    v.user_center_child_sex.setText("女");
                } else {
                    v.user_center_child_sex.setText("男");
                }
                if (!TextUtils.isEmpty(student.getAppClass().getClName())) {
                    v.user_center_child_class.setText(student.getAppClass().getClName());
                }
            }
        }

    }

    public static void start(Context context) {
        Intent intent = new Intent(context, UserCenterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.user_center_back:
                finish();
                break;
            case R.id.user_center_save:
                LinkedHashMap<String, String> params = new LinkedHashMap<>();
                params.put("uId", App.app.getData("uId"));
                params.put("uName", v.user_center_name.getText().toString());
                if (!TextUtils.isEmpty(uBrithday)) {
                    params.put("uBrithday", uBrithday);
                }
                if (!TextUtils.isEmpty(v.user_center_city.getText().toString())) {
                    params.put("uCity", v.user_center_city.getText().toString());
                }
                if (v.user_center_sex.getText().toString().equals("女")) {
                    params.put("uSex", "0");
                } else if (v.user_center_sex.getText().toString().equals("男")) {
                    params.put("uSex", "1");
                }
                String taoken = TokenUtils.getInstance().configParams(GlobalValues.APP_USER + "?", params);
                params.put("taoken", taoken);
                MyOkHttp.postMap(GlobalValues.APP_USER, 1, "dynamic", params, new MyStringCallback());
                break;

            case R.id.user_center_age:
                DatePicker picker = new DatePicker(this, DatePicker.YEAR_MONTH_DAY);
                picker.setRange(1900, 2016);
                picker.setOnDatePickListener(new DatePicker.OnYearMonthDayPickListener() {
                    @Override
                    public void onDatePicked(String year, String month, String day) {
                        uBrithday = year + "-" + month + "-" + day;
                        v.user_center_age.setText("" + CheckUtils.getAge(CheckUtils.getDate(uBrithday)));
                    }
                });
                picker.show();
                break;
            case R.id.user_center_head_layout:
                new ActionSheetDialog(UserCenterActivity.this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                        .addSheetItem("拍照", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {

                                if (checkCameraHardWare(UserCenterActivity.this)) {
                                    String status = Environment.getExternalStorageState();
                                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                                        if (Build.VERSION.SDK_INT >= 23) {
                                            int checkPermission = ContextCompat.checkSelfPermission(UserCenterActivity.this, Manifest.permission.CAMERA);
                                            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                                                (new AlertDialog.Builder(UserCenterActivity.this)).setMessage("您需要在设置里打开相机权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        ActivityCompat.requestPermissions(UserCenterActivity.this, new String[]{Manifest.permission.CAMERA}, 100);
                                                    }
                                                }).setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null).create().show();
                                                return;
                                            }
                                        }

                                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                        Uri imageUri = Uri.fromFile(takePhotFile);
                                        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                                        startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO);
                                    }

                                } else {
                                    showToast("相机不可用");
                                }
                            }
                        }).addSheetItem("相册", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                        if (Build.VERSION.SDK_INT >= 23) {
                            int checkPermission = ContextCompat.checkSelfPermission(UserCenterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
                            if (checkPermission != PackageManager.PERMISSION_GRANTED) {
                                (new AlertDialog.Builder(UserCenterActivity.this)).setMessage("您需要在设置里打开存储空间权限。").setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        ActivityCompat.requestPermissions(UserCenterActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
                                    }
                                }).setNegativeButton("取消", (android.content.DialogInterface.OnClickListener) null).create().show();
                                return;
                            }
                        }

                        Intent intent = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                    }
                }).show();
                break;
            case R.id.user_center_sex_layout:
                new ActionSheetDialog(UserCenterActivity.this).builder().setCancelable(true).setCanceledOnTouchOutside(true)
                        .addSheetItem("男", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
                            @Override
                            public void onClick(int which) {
                            }
                        }).addSheetItem("女", ActionSheetDialog.SheetItemColor.Blue, new OnSheetItemClickListener() {
                    @Override
                    public void onClick(int which) {
                    }
                }).show();
                break;

        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {

            String takePhotoPath = savedInstanceState.getString("takePhotoPath");
            if (!TextUtils.isEmpty(takePhotoPath)) {
                takePhotFile = new File(takePhotoPath);
            }

            String cropFilePath = savedInstanceState.getString("cropFilePath");
            if (!TextUtils.isEmpty(cropFilePath)) {
                cropFile = new File(cropFilePath);
            }
        } else {

            File dir = new File(FileUtils.SDPATH);
            if (!dir.exists()) {
                dir.delete();
            }

            takePhotFile = new File(dir, "photo_cache.jpg");
            takePhotFile.delete();
            cropFile = new File(dir, "crop_cache.jpg");
            cropFile.delete();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (takePhotFile != null) {
            outState.putString("takePhotoPath", takePhotFile.getAbsolutePath());
        }

        if (cropFile != null) {
            outState.putString("cropFilePath", cropFile.getAbsolutePath());
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_TAKE_PHOTO:
                    ActivityUtils.startImageCropActivity(this, REQUEST_CODE_CROP, Uri.fromFile(takePhotFile), Uri.fromFile(cropFile));
                    break;
                case REQUEST_CODE_PICK_IMAGE:
                    ActivityUtils.startImageCropActivity(this, REQUEST_CODE_CROP, data.getData(), Uri.fromFile(cropFile));
                    break;
                case REQUEST_CODE_CROP:
                    //图片显示
                    try {
                        FileInputStream fis = new FileInputStream(cropFile.getAbsolutePath());
                        Bitmap bitmap = BitmapFactory.decodeStream(fis);
                        v.user_center_head_pic.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //图片上传
                    upphoto();
                    break;
            }
        }
    }

    private void upphoto() {
        showProgressDialog(UserCenterActivity.this, "上传中...");
        LinkedHashMap<String, String> params = new LinkedHashMap<>();
        params.put("uId", App.app.getData("uId"));
        MyOkHttp.uploadFileParams(GlobalValues.APP_USERPHOTO, "photo", 0, params, cropFile, "uHeadPic", new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            switch (id) {
                case 0:
                    dismissProgressDialog();
                    showToast("修改失败");
                    break;
                case 1:
                    dismissProgressDialog();
                    showToast("修改失败");
                    break;

            }
        }

        @Override
        public void onResponse(String response, int id) {
            Log.i("test", response);
            switch (id) {
                case 0:
                    CommonBean<String> bean = JsonUtils.getBeans(response, CommonBean.class, String.class);
                    if (bean != null) {
                        if (bean.isSuccess()) {
                            dismissProgressDialog();
                            String rows = bean.getRows();
                            if (!TextUtils.isEmpty(rows)) {
                                App.app.setData("uPic", rows);
                                showToast("修改成功");
                                EventBus.getDefault().post(new UploadPicEntity(UploadPicEntity.UPLOAD_PARENT));
                            }
                        } else {
                            showToast("修改失败");
                            dismissProgressDialog();
                        }
                    }
                    break;
                case 1:
                    CommonBean<UserModel> beans = JsonUtils.getBeans(response, CommonBean.class, UserModel.class);
                    if (beans != null) {
                        App.app.saveObjData("user", beans.getRows());
                        dismissProgressDialog();
                        showToast("修改成功");
                        EventBus.getDefault().post(new UploadPicEntity(UploadPicEntity.UPLOAD_PARENT_NAME));
                        finish();
                    } else {
                        showToast("修改失败");
                        dismissProgressDialog();
                    }
                    break;
            }
        }

    }


    /**
     * 检查相机是否可用
     *
     * @param context
     * @return
     */
    private boolean checkCameraHardWare(Context context) {
        PackageManager packageManager = context.getPackageManager();
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        }
        return false;
    }
}
