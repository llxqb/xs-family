package com.bhxx.xs_family.fragment;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;

import java.util.ArrayList;
import java.util.List;

public class MoveoaCheckitemFragment extends BaseFragment {
    @InjectAll
    private Views v;
    int type;

    private class Views {
        TabLayout my_moveoacheck_tablayout;
        ViewPager my_moveoacheck_viewpager;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.my_moveoa_list_layout, null);
        Handler_Inject.injectFragment(this, rootView);

        if (getArguments() != null) {
            this.type = getArguments().getInt("type");
        }

        return rootView;
    }


    @Override
    protected void init() {
        v.my_moveoacheck_viewpager.setOffscreenPageLimit(3);
        v.my_moveoacheck_viewpager.setAdapter(new MyPageAdapter(getChildFragmentManager()));

        v.my_moveoacheck_tablayout.setupWithViewPager(v.my_moveoacheck_viewpager);

    }

    /**
     * 实例化
     *
     * @param type
     * @return
     */
    public static MoveoaCheckitemFragment getInstance(int type) {
        MoveoaCheckitemFragment fragment = new MoveoaCheckitemFragment();
        Bundle bd = new Bundle();
        bd.putInt("type", type);
        fragment.setArguments(bd);

        return fragment;
    }

    @Override
    protected void click(View view) {
    }

    private class MyPageAdapter extends FragmentPagerAdapter {
        private final String[] titles = {"请假申请", "物品申领", "费用报销"};
        private List<Fragment> fragments = new ArrayList<Fragment>();

        public MyPageAdapter(FragmentManager fm) {
            super(fm);
            MoveoaCheckOneFragment fragmentone = new MoveoaCheckOneFragment();
            MoveoaCheckTwoFragment fragmenttwo = new MoveoaCheckTwoFragment();
            MoveoaCheckThreeFragment fragmentthree = new MoveoaCheckThreeFragment();
            fragments.add(fragmentone);
            fragments.add(fragmenttwo);
            fragments.add(fragmentthree);
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
