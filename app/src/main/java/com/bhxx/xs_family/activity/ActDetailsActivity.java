package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.pc.ioc.event.EventBus;
import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectBinder;
import com.android.pc.ioc.inject.InjectLayer;
import com.android.pc.ioc.view.listener.OnClick;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.application.App;
import com.bhxx.xs_family.beans.ActModel;
import com.bhxx.xs_family.entity.ApplyActEntity;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.LoadImage;
import com.bhxx.xs_family.utils.LogUtils;
import com.bhxx.xs_family.values.GlobalValues;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@InjectLayer(R.layout.activity_act_details)
public class ActDetailsActivity extends BasicActivity {
    @InjectAll
    private Views v;
    private ActModel data;
    private AtomicInteger what = new AtomicInteger(0);
    private boolean isContinue = true;
    private List<String> scrollImages;

    private class Views {
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        ImageView act_details_back;
        //        ImageView act_details_img;
        TextView act_details_name, act_details_time, act_details_num, act_details_about_act, act_details_plan,
                act_details_must_know;
        @InjectBinder(listeners = {OnClick.class}, method = "click")
        Button act_apply_bt;
        ViewPager act_detail_viewpager;
        LinearLayout act_detail_dot;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(ActDetailsActivity.this);
        EventBus.getDefault().register(this);
        scrollImages = new ArrayList<String>();
        if (!App.app.getData("uRole").equals("2")) {
            v.act_apply_bt.setVisibility(View.GONE);
        }
        if (getIntent() != null) {
            data = (ActModel) getIntent().getSerializableExtra("act");

            if (data != null) {
                String pic = data.getAcMainPic()+";"+data.getAcPics();
                String[] acPics = pic.split(";");
                for (int i = 0; i < acPics.length; i++) {
                    scrollImages.add(acPics[i]);
                }
                initViewPager();
                initView(data);
            }
        }
    }

    private void initView(ActModel bean) {
        if (!TextUtils.isEmpty(bean.getAcTitle())) {
            v.act_details_name.setText(bean.getAcTitle());
        }
        if (!TextUtils.isEmpty(bean.getAcEndSigeUpTime())) {
            v.act_details_time.setText(bean.getAcEndSigeUpTime());
        }
        if (!TextUtils.isEmpty(bean.getAcMainPic())) {
//            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + bean.getAcMainPic(), v.act_details_img, LoadImage.getDefaultOptions());
        }
        v.act_details_num.setText((bean.getAcCount() - bean.getAcSurCount()) + "人已报名");
        if (!TextUtils.isEmpty(bean.getAcDesction())) {
            v.act_details_about_act.setText(bean.getAcDesction());
            v.act_details_plan.setText(bean.getAcTrip());
            v.act_details_must_know.setText(bean.getAcNotice());
        }
        switch (bean.getActivityState()) {
            case 1:
                v.act_apply_bt.setText("活动未开始");
                break;
            case 2:
                v.act_apply_bt.setText("活动进行中");
                break;
            case 3:
                v.act_apply_bt.setText("活动已结束");
                break;
        }
    }

    /**
     * 跳转活动详情
     *
     * @param context
     * @param bean
     */
    public static void start(Context context, ActModel bean) {
        Intent intent = new Intent(context, ActDetailsActivity.class);
        intent.putExtra("act", bean);
        context.startActivity(intent);
    }

    protected void onEventMainThread(ApplyActEntity entity) {
        data.setActivityState(1);
        v.act_apply_bt.setText("活动已报名");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void click(View view) {
        switch (view.getId()) {
            case R.id.act_details_back:
                finish();
                break;
            case R.id.act_apply_bt:
                if (data.getActivityState() == 0) {
                    ApplyActActivity.start(ActDetailsActivity.this, data.getAcId());
                }
                break;
        }
    }

    // 滚图
    private void initViewPager() {
        AdvAdapter adapter = new AdvAdapter(scrollImages);
        v.act_detail_viewpager.setAdapter(adapter);
        v.act_detail_viewpager.addOnPageChangeListener(new GuidePageChangeListener());
        initDot(scrollImages.size());

        v.act_detail_viewpager.setOnTouchListener(new View.OnTouchListener() {
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
            v.act_detail_viewpager.setCurrentItem(msg.what);
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

            ImageView imageView = new ImageView(ActDetailsActivity.this);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            container.addView(imageView,
                    new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

            if (imageUrls != null && imageUrls.size() > 0) {
//                imageView.setImageResource(imageUrls.get(position % imageUrls.size()));
                ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + imageUrls.get(position % imageUrls.size()), imageView, LoadImage.getDefaultOptions());
            }

//            imageView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                }
//            });
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

        v.act_detail_dot.removeAllViews();
        int margin = (int) (getResources().getDisplayMetrics().density * 5);
        for (int i = 0; i < num; i++) {
            ImageView imageView = new ImageView(ActDetailsActivity.this);
            imageView.setImageResource(R.mipmap.pic_ctiy_01);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.leftMargin = margin;
            params.rightMargin = margin;
            params.weight = 1;
            v.act_detail_dot.addView(imageView, params);
        }
        setCurrentDot(0);
    }

    private void setCurrentDot(int position) {

        AdvAdapter adapter = (AdvAdapter) v.act_detail_viewpager.getAdapter();
        int urlNum = adapter.getImageUrls() == null ? 0 : adapter.getImageUrls().size();
        if (urlNum == 0) {
            urlNum = position;
        }

        for (int i = 0; i < v.act_detail_dot.getChildCount(); i++) {

            ImageView view = (ImageView) v.act_detail_dot.getChildAt(i);
            if (i == position % urlNum) {
                view.setImageResource(R.mipmap.w_l);
            } else {
                view.setImageResource(R.mipmap.w_h);
            }
        }
    }
}
