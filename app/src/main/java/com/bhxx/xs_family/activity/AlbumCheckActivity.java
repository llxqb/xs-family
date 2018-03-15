package com.bhxx.xs_family.activity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.ClassinfoDialogAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ClassModel;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.entity.LookCheckStatusEntity;
import com.bhxx.xs_family.fragment.AlbumCheckOneFragment;
import com.bhxx.xs_family.fragment.AlbumCheckThreeFragment;
import com.bhxx.xs_family.fragment.AlbumCheckTwoFragment;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;

@InjectLayer(R.layout.activity_album_check)
public class AlbumCheckActivity extends BasicActivity {

    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView my_albumcheck_back, my_albumcheck_add;
        TabLayout my_albumcheck_tablayout;
        ViewPager my_albumcheck_viewpager;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
        v.my_albumcheck_viewpager.setOffscreenPageLimit(3);
        v.my_albumcheck_viewpager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));

        v.my_albumcheck_tablayout.setupWithViewPager(v.my_albumcheck_viewpager);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.my_albumcheck_back:
                finish();
                break;
            case R.id.my_albumcheck_add:
                showDialog();
                break;
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private final String[] titles = {"未审核", "已审核", "删除申请"};
        private List<Fragment> fragments = new ArrayList<Fragment>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            AlbumCheckOneFragment fragmenone = new AlbumCheckOneFragment();
            AlbumCheckTwoFragment fragmentwo = new AlbumCheckTwoFragment();
            AlbumCheckThreeFragment fragmenthree = new AlbumCheckThreeFragment();
            fragments.add(fragmenone);
            fragments.add(fragmentwo);
            fragments.add(fragmenthree);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    public static void start(Context context) {
        Intent intent = new Intent(context, AlbumCheckActivity.class);
        context.startActivity(intent);
    }

    /**
     * 圆所下面班级id弹窗
     */
    private ListView dialog_listview;
    private CommonListBean<ClassModel> listBean;

    private void showDialog() {
        // 获取Dialog布局
        View view = LayoutInflater.from(AlbumCheckActivity.this).inflate(R.layout.dynamic_check_dialog, null);
        WindowManager windowManager = (WindowManager) AlbumCheckActivity.this.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        // 设置Dialog最小宽度为屏幕宽度
        view.setMinimumWidth(display.getWidth());

        // 获取自定义Dialog布局中的控件
        dialog_listview = (ListView) view.findViewById(R.id.dialog_listview);
        RelativeLayout dialog_layout = (RelativeLayout) view.findViewById(R.id.dialog_layout);
        TextView dialog_title = (TextView) view.findViewById(R.id.dialog_title);
        dialog_title.setText("相册审核");
        // 定义Dialog布局和参数
        final Dialog dialog = new Dialog(AlbumCheckActivity.this, R.style.ActionSheetDialogStyle);
        dialog.setContentView(view);
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        Window dialogWindow = dialog.getWindow();
        dialogWindow.setGravity(Gravity.LEFT | Gravity.TOP);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.x = 0;
        lp.y = 0;
        dialogWindow.setAttributes(lp);
        dialog.show();
        initClasslist();
        dialog_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (listBean.getRows() != null && listBean.getRows().size() > 0) {
                    listBean.getRows().get(position).getcId();
                    EventBus.getDefault().post(new LookCheckStatusEntity(listBean.getRows().get(position).getcId()));
                }
                dialog.dismiss();
            }
        });
    }

    private void initClasslist() {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("parkId", App.app.getData("parkId"));
        String taoken = TokenUtils.getInstance().configParams(GlobalValues.ALLCLASS + "?", params);
        params.put("taoken", taoken);
        MyOkHttp.postMap(GlobalValues.ALLCLASS, "ALLCLASS", params, new MyStringCallback());
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            showToast("查询失败");
        }

        @Override
        public void onResponse(String response, int id) {
            if (!TextUtils.isEmpty(response)) {
                listBean = JsonUtils.getBean(response, CommonListBean.class, ClassModel.class);
                if (listBean.getRows() != null && listBean.getRows().size() > 0) {
                    ClassinfoDialogAdapter adapter = new ClassinfoDialogAdapter(listBean.getRows(), AlbumCheckActivity.this, R.layout.classinfodialog);
                    dialog_listview.setAdapter(adapter);
                } else {
                    showToast("无班级信息");
                }
            }
        }
    }
}
