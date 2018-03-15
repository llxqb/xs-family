package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.SysMessAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.SystemMessageBean;
import com.bhxx.xs_family.db.dao.SysMessDao;
import com.bhxx.xs_family.utils.ActivityCollector;

import org.apache.http.util.TextUtils;

@InjectLayer(R.layout.activity_message_details)
public class SysMessActivity extends BasicActivity {
    private SysMessAdapter adapter;
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView sys_mess_back;
        ListView sys_mess_lv;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(SysMessActivity.this);
        if(!TextUtils.isEmpty(App.app.getData("uId"))){
            adapter=new SysMessAdapter(SysMessDao.queryAllType(App.app.getData("uId")),SysMessActivity.this,R.layout.sys_mess_item);
            v.sys_mess_lv.setAdapter(adapter);
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, SysMessActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.sys_mess_back:
                finish();
                break;
        }
    }
}
