package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

import io.rong.imkit.RongIM;

@InjectLayer(R.layout.activity_teacher_info)
public class TeacherInfoActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private UserModel user;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView teacher_info_back, teacher_info_call, teacher_info_msg;
        TextView teacher_info_title, teacher_info_name, teacher_info_basic, teacher_info_class, teacher_info_phone, teacher_info_school, teacher_info_description;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button teacher_info_send_bt;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        CircleImageView teacher_info_head_pic;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(TeacherInfoActivity.this);
        if (App.app.getData("uRole").equals("0")) {
            v.teacher_info_send_bt.setVisibility(View.GONE);
        }

        if (getIntent() != null) {
            user = (UserModel) getIntent().getSerializableExtra("teacher");
            if (user != null) {
                initViewValue(user);
            }
        }
    }

    private void initViewValue(UserModel teacher) {
        if (!TextUtils.isEmpty(teacher.getuName())) {
            v.teacher_info_title.setText(teacher.getuName());
            v.teacher_info_name.setText(teacher.getuName());
        }
        if (!TextUtils.isEmpty(teacher.getuHeadPic())) {
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + teacher.getuHeadPic(), v.teacher_info_head_pic, LoadImage.getHeadImgOptions());
        }
        if (teacher.getuSex() == 0) {
            v.teacher_info_basic.setText("女/" + teacher.getuAge() + "岁");
        } else {
            v.teacher_info_basic.setText("男/" + teacher.getuAge() + "岁");
        }
        if (!TextUtils.isEmpty(teacher.getuMobile())) {
            v.teacher_info_phone.setText(teacher.getuMobile());
        }
        if (!TextUtils.isEmpty(teacher.getuClassName()) && !TextUtils.isEmpty(teacher.getuPosition())) {
            v.teacher_info_class.setText(teacher.getuClassName() + "/" + teacher.getuPosition());
        }
        if (!TextUtils.isEmpty(teacher.getuParkName())) {
            v.teacher_info_school.setText(teacher.getuParkName());
        }
        if (!TextUtils.isEmpty(teacher.getuRemark())) {
            v.teacher_info_description.setText(teacher.getuRemark());
        }
    }

    /**
     * 查看他人信息跳转
     *
     * @param context
     * @param teacher
     */
    public static void start(Context context, UserModel teacher) {
        Intent intent = new Intent(context, TeacherInfoActivity.class);
        intent.putExtra("teacher", teacher);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.teacher_info_back:
                finish();
                break;
            case R.id.teacher_info_call:
                Intent call = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + v.teacher_info_phone.getText().toString()));
                startActivity(call);
                break;
            case R.id.teacher_info_msg:
                Intent msg = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + v.teacher_info_phone.getText().toString()));
                startActivity(msg);
                break;
            case R.id.teacher_info_send_bt:
                if (RongIM.getInstance() != null) {
                    RongIM.getInstance().startPrivateChat(TeacherInfoActivity.this, "" + user.getuId(), v.teacher_info_name.getText().toString());
                }
                break;
            case R.id.teacher_info_head_pic:
                HeadImgActivity.start(TeacherInfoActivity.this, user.getuHeadPic());
                break;
        }
    }
}
