package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.fragment.MyConnectionDynamicFragment;
import com.bhxx.xs_family.fragment.MyConnectionQinZiFragment;
import com.bhxx.xs_family.fragment.MyConnectionXiangCeFragment;
import com.bhxx.xs_family.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

@InjectLayer(R.layout.activity_my_collect)
public class MyCollectActivity extends BasicActivity {
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView my_collect_back;
        TabLayout my_collect_tablayout;
        ViewPager my_collect_viewpager;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(MyCollectActivity.this);
        v.my_collect_viewpager.setOffscreenPageLimit(3);
        v.my_collect_viewpager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));

        v.my_collect_tablayout.setupWithViewPager(v.my_collect_viewpager);
    }

    /**
     * 我的收藏页跳转
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, MyCollectActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.my_collect_back:
                finish();
                break;
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private final String[] titles = {"动态", "相册", "亲子"};
        private List<Fragment> fragments = new ArrayList<Fragment>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            fragments.add(new MyConnectionDynamicFragment());
            fragments.add(new MyConnectionXiangCeFragment());
            fragments.add(new MyConnectionQinZiFragment());
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
