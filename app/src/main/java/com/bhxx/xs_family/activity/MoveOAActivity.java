package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.MoveoaQjAdapter;
import com.bhxx.xs_family.utils.ActivityCollector;

@InjectLayer(R.layout.activity_move_oa)
public class MoveOAActivity extends BasicActivity {

    @InjectAll
    private Views v;

    private class Views{
        @InjectBinder(listeners = {OnClick.class} ,method = "click")
        ImageView my_moveoa_back;
        @InjectBinder(listeners = {OnClick.class} ,method = "click")
        ImageView user_work_kq,user_work_qj,user_work_wp,user_work_fy;
    }
    @Override
    protected void init() {
        ActivityCollector.addActivity(MoveOAActivity.this);
    }

    @Override
    protected void click(View view) {

        switch (view.getId()){
            case R.id.my_moveoa_back:
                finish();
                break;
            case R.id.user_work_kq:
                MoveoaKqActivity.start(MoveOAActivity.this);
                break;
            case R.id.user_work_qj:
                MoveoaQjActivity.start(MoveOAActivity.this);
                break;
            case R.id.user_work_wp:
                MoveOaWpActivity.start(MoveOAActivity.this);
                break;
            case R.id.user_work_fy:
                MoveOaFyActivity.start(MoveOAActivity.this);
                break;
        }
    }

    public static  void start(Context context){
        Intent intent = new Intent(context,MoveOAActivity.class);
        context.startActivity(intent);

    }
}
