package com.bhxx.xs_family.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.view.listener.OnClick;
import com.android.pc.util.Handler_Inject;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.activity.ActDetailsActivity;
import com.bhxx.xs_family.activity.AddressBookActivity;
import com.bhxx.xs_family.activity.AlbumActivity;
import com.bhxx.xs_family.activity.ChoiceActActivity;
import com.bhxx.xs_family.activity.ClassDynamicActivity;
import com.bhxx.xs_family.activity.FoodMenuActivity;
import com.bhxx.xs_family.activity.LittleClassActivity;
import com.bhxx.xs_family.activity.OtherScheduleActivity;
import com.bhxx.xs_family.activity.RestScheduleActivity;
import com.bhxx.xs_family.adapter.HomeActivityAdapter;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.beans.CommonListBean;
import com.bhxx.xs_family.entity.ApplyActEntity;
import com.bhxx.xs_family.utils.CommonCallback;
import com.bhxx.xs_family.utils.JsonUtils;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.utils.MyOkHttp;
import com.bhxx.xs_family.utils.TokenUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import okhttp3.Call;

public class HomePage extends BaseFragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {
    private ViewPager parent_home_viewpager;
    private LinearLayout parent_home_dot;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;
    private AdvAdapter adapter;
    private HomeActivityAdapter actAdapter;
    @InjectBinder(listeners = {OnClick.class}, method = "click")
    private RelativeLayout parent_model_bj, parent_model_xc, parent_model_sp, parent_model_zx, parent_model_kt, parent_model_tx, parent_home_act_layout;
    private ListView parent_home_list;
    private SwipeRefreshLayout swipe_ly;
    private List<ActModel> actList = new ArrayList<>();
    private List<String> scrollImages;
    private List<ActModel> actModelList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.home_page, container, false);
        parent_home_list = (ListView) rootView.findViewById(R.id.parent_home_list);
        swipe_ly = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_ly);
        swipe_ly.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light);
        swipe_ly.setOnRefreshListener(this);
        scrollImages = new ArrayList<String>();
        actModelList = new ArrayList<ActModel>();

        initHead();
//        initViewPager();
        EventBus.getDefault().register(this);
        Handler_Inject.injectFragment(this, rootView);
        return rootView;
    }

    @Override
    protected void init() {
        //获取首页活动列表
        getHomeActivityList(0);
        //获取轮播图片
        getCarouselList();
    }

    /**
     * 头部
     */
    private void initHead() {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.home_page_head, null);
        parent_home_viewpager = (ViewPager) view.findViewById(R.id.parent_home_viewpager);
        parent_home_dot = (LinearLayout) view.findViewById(R.id.parent_home_dot);
        parent_model_bj = (RelativeLayout) view.findViewById(R.id.parent_model_bj);
        parent_model_bj.setOnClickListener(this);
        parent_model_xc = (RelativeLayout) view.findViewById(R.id.parent_model_xc);
        parent_model_xc.setOnClickListener(this);
        parent_model_sp = (RelativeLayout) view.findViewById(R.id.parent_model_sp);
        parent_model_sp.setOnClickListener(this);
        parent_model_zx = (RelativeLayout) view.findViewById(R.id.parent_model_zx);
        parent_model_zx.setOnClickListener(this);
        parent_model_kt = (RelativeLayout) view.findViewById(R.id.parent_model_kt);
        parent_model_kt.setOnClickListener(this);
        parent_model_tx = (RelativeLayout) view.findViewById(R.id.parent_model_tx);
        parent_model_tx.setOnClickListener(this);
        parent_home_act_layout = (RelativeLayout) view.findViewById(R.id.parent_home_act_layout);
        parent_home_act_layout.setOnClickListener(this);
        parent_home_list.addHeaderView(view);
        actAdapter = new HomeActivityAdapter(actList, getActivity(), R.layout.home_act_item);
        parent_home_list.setAdapter(actAdapter);
    }

    /**
     * 获取首页活动列表
     */
    private void getHomeActivityList(int type) {
        LinkedHashMap<String, String> params = new LinkedHashMap<String, String>();
        params.put("page", "1");
        params.put("pageSize", GlobalValues.PAGE_SIZE);
        params.put("uId", App.app.getData("uId"));
        params.put("acConditionState", "-1");
        String token = TokenUtils.getInstance().configParams(GlobalValues.APP_ACT + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.APP_ACT, type, "HOMEACT", params, new MyStringCallback());
    }

    private void getCarouselList(){
        LinkedHashMap<String,String> params = new LinkedHashMap<>();
        params.put("uId",App.app.getData("uId"));
        String token = TokenUtils.getInstance().configParams(GlobalValues.CAROUSEL + "?", params);
        params.put("taoken", token);
        MyOkHttp.postMap(GlobalValues.CAROUSEL, 2, "CAROUSEL", params, new MyStringCallback());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.parent_model_bj:
                ClassDynamicActivity.start(getActivity());
                break;
            case R.id.parent_model_xc:
                AlbumActivity.start(getActivity());
                break;
            case R.id.parent_model_sp:
                FoodMenuActivity.start(getActivity());
                break;
            case R.id.parent_model_zx:
                if (App.app.getData("uRole").equals("0")) {
                    RestScheduleActivity.start(getActivity());
                } else {
                    OtherScheduleActivity.start(getActivity());
                }
                break;
            case R.id.parent_model_kt:
                LittleClassActivity.start(getActivity());
                break;
            case R.id.parent_model_tx:
                AddressBookActivity.start(getActivity());
                break;
            case R.id.parent_home_act_layout:
                ChoiceActActivity.start(getActivity());
                break;
        }
    }

    @Override
    public void onRefresh() {
        getHomeActivityList(1);
    }

    private class MyStringCallback extends CommonCallback {
        @Override
        public void onError(Call call, Exception e, int id) {
            swipe_ly.setRefreshing(false);
        }

        @Override
        public void onResponse(String response, int id) {
            switch (id) {
                case 0:
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<ActModel> acts = JsonUtils.getBeans(response, CommonListBean.class, ActModel.class);
                        if (acts.getSuccess()) {
                            if (acts.getRows() != null && acts.getRows().size() > 0) {
                                actList.clear();
                                actList.addAll(acts.getRows());
                                actAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                case 1:
                    if (!TextUtils.isEmpty(response)) {
                        CommonListBean<ActModel> acts = JsonUtils.getBeans(response, CommonListBean.class, ActModel.class);
                        if (acts.getSuccess()) {
                            if (acts.getRows() != null && acts.getRows().size() > 0) {
                                actList.clear();
                                actList.addAll(acts.getRows());
                                actAdapter.notifyDataSetChanged();
                                swipe_ly.setRefreshing(false);
                                showToast("刷新成功");
                            }
                        }
                    }
                    break;

                case 2:
                    if(!TextUtils.isEmpty(response)){
                        CommonListBean<ActModel> acts = JsonUtils.getBeans(response, CommonListBean.class, ActModel.class);
                        if (acts.getSuccess()) {
                            if (acts.getRows() != null && acts.getRows().size() > 0) {
                                for(int i=0;i<acts.getRows().size();i++){
                                    scrollImages.add(acts.getRows().get(i).getAcMainPic());
                                    actModelList.add(acts.getRows().get(i));
                                }
                                initViewPager();
                            }
                        }
                    }
                    break;
            }
        }
    }

    protected void onEventMainThread(ApplyActEntity entity) {
        for (ActModel model : actAdapter.getDataList()) {
            if (model.getActivityState() == entity.getActId()) {
                if (model.getActivityState() == 0) {
                    model.setActivityState(1);
                }
            }
        }
        actAdapter.notifyDataSetChanged();
    }

    @Override
    protected void click(View view) {

    }

    // 滚图
    private void initViewPager() {
//        scrollImages.add(R.mipmap.home_01);
//        scrollImages.add(R.mipmap.home_02);
        adapter = new AdvAdapter(scrollImages);
        parent_home_viewpager.setAdapter(adapter);
        parent_home_viewpager.addOnPageChangeListener(new GuidePageChangeListener());
        initDot(scrollImages.size());

        parent_home_viewpager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                    case MotionEvent.ACTION_MOVE:
                        isContinue = false;
                        break;
                    case MotionEvent.ACTION_UP:
                        isContinue = true;
                        break;
                    default:
                        isContinue = true;
                        break;
                }
                return false;
            }
        });
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    if (isContinue) {
                        viewHandler.sendEmptyMessage(what.get());
                        whatOption();
                    }
                }
            }
        }).start();
    }

    private void whatOption() {
        what.incrementAndGet();
        if (what.get() > Integer.MAX_VALUE - 1) {
            what.set(0);
        }
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {

        }
    }

    private final Handler viewHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            parent_home_viewpager.setCurrentItem(msg.what);
            super.handleMessage(msg);
        }
    };

    private final class GuidePageChangeListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            what.getAndSet(arg0);
            setCurrentDot(arg0);
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private final class AdvAdapter extends PagerAdapter {

        private List<String> imageUrls;

        public AdvAdapter(List<String> imageUrls) {
            super();
            this.imageUrls = imageUrls;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            ImageView imageView = new ImageView(getActivity());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            if (imageUrls != null && imageUrls.size() > 0) {
//                imageView.setImageResource(imageUrls.get(position % imageUrls.size()));
                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP +imageUrls.get(position % imageUrls.size()),imageView, LoadImage.getDefaultOptions());
            }

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActDetailsActivity.start(getActivity(), actModelList.get(position % imageUrls.size()));
                }
            });
            return imageView;
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public List<String> getImageUrls() {
            return imageUrls;
        }
    }

    private void initDot(int num) {

        parent_home_dot.removeAllViews();
        int margin = (int) (getResources().getDisplayMetrics().density * 5);
        for (int i = 0; i < num; i++) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(R.mipmap.pic_ctiy_01);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = margin;
            params.rightMargin = margin;
            params.weight = 1;
            parent_home_dot.addView(imageView, params);
        }
        setCurrentDot(0);
    }

    private void setCurrentDot(int position) {

        AdvAdapter adapter = (AdvAdapter) parent_home_viewpager.getAdapter();
        int urlNum = adapter.getImageUrls() == null ? 0 : adapter.getImageUrls().size();
        if (urlNum == 0) {
            urlNum = position;
        }

        for (int i = 0; i < parent_home_dot.getChildCount(); i++) {
            ImageView view = (ImageView) parent_home_dot.getChildAt(i);
            if (i == position % urlNum) {
                view.setImageResource(R.mipmap.w_l);
            } else {
                view.setImageResource(R.mipmap.w_h);
            }
        }
    }

}
