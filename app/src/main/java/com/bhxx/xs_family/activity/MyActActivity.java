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
import com.bhxx.xs_family.fragment.ParentMyActItemFragment;
import com.bhxx.xs_family.utils.ActivityCollector;

import java.util.ArrayList;
import java.util.List;

@InjectLayer(R.layout.activity_my_act)
public class MyActActivity extends BasicActivity {
    @InjectAll
    private Views v;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView my_act_back;
        TabLayout my_act_tablayout;
        ViewPager my_act_viewpager;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(MyActActivity.this);
        v.my_act_viewpager.setAdapter(new MyPageAdapter(getSupportFragmentManager()));
        v.my_act_tablayout.setupWithViewPager(v.my_act_viewpager);
    }

    /**
     * 我的活动页面跳转
     *
     * @param context
     */
    public static void start(Context context) {
        Intent intent = new Intent(context, MyActActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.my_act_back:
                finish();
                break;
        }
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private final String[] titles={"未开始","已结束"};
        private List<Fragment> frag = new ArrayList<Fragment>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            frag.add(ParentMyActItemFragment.getInstance(0));
            frag.add(ParentMyActItemFragment.getInstance(1));
        }

        @Override
        public Fragment getItem(int position) {
            return frag.get(position);
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
