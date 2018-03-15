package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.utils.ActivityCollector;

@InjectLayer(R.layout.activity_msg_setting)
public class MsgSettingActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private int msgPush = 1;//1 开启 0关闭
    private int msgReceive = 1;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView msg_setting_back;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageButton msg_push_bt, msg_send_bt;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(MsgSettingActivity.this);

    }

    public static void start(Context context) {
        Intent intent = new Intent(context, MsgSettingActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.msg_setting_back:
                finish();
                break;
            case R.id.msg_push_bt:
                if (msgPush == 1) {
                    msgPush = 0;
                    v.msg_push_bt.setImageResource(R.mipmap.icon_close);
                } else {
                    msgPush = 1;
                    v.msg_push_bt.setImageResource(R.mipmap.icon_open);
                }
                break;
            case R.id.msg_send_bt:
                if (msgReceive == 1) {
                    msgReceive = 0;
                    v.msg_send_bt.setImageResource(R.mipmap.icon_close);
                } else {
                    msgReceive = 1;
                    v.msg_send_bt.setImageResource(R.mipmap.icon_open);
                }
                break;
        }
    }
}
