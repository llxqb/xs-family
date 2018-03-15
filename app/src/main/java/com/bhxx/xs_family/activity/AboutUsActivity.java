package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.utils.ActivityCollector;

@InjectLayer(R.layout.activity_about_us)
public class AboutUsActivity extends BasicActivity {
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView about_us_back;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(AboutUsActivity.this);
    }
    public static void start(Context context) {
        Intent intent = new Intent(context, AboutUsActivity.class);
        context.startActivity(intent);
    }
    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.about_us_back:
                finish();
                break;
        }
    }
}
