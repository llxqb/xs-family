package com.bhxx.xs_family.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.fragment.MyActItemFragment;
import com.bhxx.xs_family.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

@InjectLayer(R.layout.content_action)
public class ActionManagerActivity extends BasicActivity {
    private static int broadcast=0;
    @InjectAll
    private Views v;

    private class Views{
        @InjectBinder(listeners = {OnClick.class},method = "click")
        ImageView my_action_back,my_action_add;
        TabLayout my_action_tablayout;
        ViewPager my_action_viewpager;

    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(ActionManagerActivity.this);
        v.my_action_viewpager.setOffscreenPageLimit(4);
        v.my_action_viewpager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));

        v.my_action_tablayout.setupWithViewPager(v.my_action_viewpager);
    }

    @Override
    protected void click(View view) {

        switch (view.getId()){
            case R.id.my_action_back:
                finish();
                break;
            case R.id.my_action_add:
                //发布活动
                PublishActionActivity.start(ActionManagerActivity.this);
                break;
        }
    }

    public static void start(Context context){
        Intent intent = new Intent(context,ActionManagerActivity.class);
        context.startActivity(intent);

    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private final String[] titles = {"未开始", "已结束", "审核中","未通过"};
        private List<Fragment> fragments = new ArrayList<Fragment>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(MyActItemFragment.getInstance(0));
            fragments.add(MyActItemFragment.getInstance(12));
            fragments.add(MyActItemFragment.getInstance(2));
            fragments.add(MyActItemFragment.getInstance(3));
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


}
