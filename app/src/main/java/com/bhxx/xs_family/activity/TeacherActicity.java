package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.UserModel;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;

/**
 * 老师个人资料
 */
@InjectLayer(R.layout.activity_teacher_acticity)
public class TeacherActicity extends BasicActivity {
    @InjectAll
    private Views v;
    private static final int REQUEST_CODE_TAKE_PHOTO = 1;
    private static final int REQUEST_CODE_PICK_IMAGE = 2;
    private static final int REQUEST_CODE_CROP = 3;
    private final int SAVE = 0;
    private File takePhotFile;
    private File cropFile;
    private UserModel user;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView teacher_back;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        RelativeLayout teacher_head_layout;
        CircleImageView teacher_head_pic;
        TextView teacher_name, teacher_age, teacher_city, teacher_duty, teacher_introduce;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        TextView  teacher_class, teacher_kindergarten, teacher_sex;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        initValue();
    }

    private void initValue() {
        if (!TextUtils.isEmpty(App.app.getData("uPic"))) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + App.app.getData("uPic"), v.teacher_head_pic, LoadImage.getHeadImgOptions());
        }
        if (App.app.readObjData("user") != null) {
            user = (UserModel) App.app.readObjData("user");
            if (!TextUtils.isEmpty(user.getuName())) {
                v.teacher_name.setText(user.getuName());
            }
            if (!TextUtils.isEmpty(user.getuCity())) {
                v.teacher_city.setText(user.getuCity());
            }
            if (!TextUtils.isEmpty(user.getuParkName())) {
                v.teacher_kindergarten.setText(user.getuParkName());
            }
            if (!TextUtils.isEmpty(user.getuClassName())) {
                v.teacher_class.setText(user.getuClassName());
            }
            if (!TextUtils.isEmpty(user.getuRemark())) {
                v.teacher_introduce.setText(user.getuRemark());
            }
            v.teacher_age.setText(user.getuAge() + "");
            if (user.getuSex() == 0) {
                v.teacher_sex.setText("女");
            } else {
                v.teacher_sex.setText("男");
            }
        }
        v.teacher_duty.setText("老师");
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.teacher_back:
                finish();
                break;
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, TeacherActicity.class);
        context.startActivity(intent);
    }
}
