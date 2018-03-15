package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.ScaleAnimation;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.viewpagerindicator.PageIndicator;
import com.bhxx.xs_family.views.ZoomImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class ImageDisplayActivity extends AppCompatActivity {

    private ArrayList<String> image_urls;
    private int initIndex = 0;
    private ViewPager mViewPager;
    private PageIndicator pageIndicator;
    private DisplayImageOptions options;

    /**
     * 启动activity
     *
     * @param context
     * @param image_urls 大图
     * @param initIndex  初始位置
     */
    public static void start(Context context, ArrayList<String> image_urls, int initIndex) {

        Intent intent = new Intent(context, ImageDisplayActivity.class);
        intent.putStringArrayListExtra("image_urls", image_urls);
        intent.putExtra("initIndex", initIndex);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_images);

        if (savedInstanceState == null) {
            image_urls = getIntent().getStringArrayListExtra("image_urls");
            initIndex = getIntent().getIntExtra("initIndex", 0);
        } else {
            image_urls = savedInstanceState.getStringArrayList("image_urls");
            initIndex = savedInstanceState.getInt("initIndex");
        }

        mViewPager = (ViewPager) findViewById(R.id.viewPager);
        pageIndicator = (PageIndicator) findViewById(R.id.circle_pager_indicator);
        mViewPager.setAdapter(new ImageAdapter());
        pageIndicator.setViewPager(mViewPager, initIndex);

        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Config.RGB_565)
                .showImageOnFail(R.mipmap.default_photo).build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (image_urls != null) {
            outState.putStringArrayList("image_urls", image_urls);
        }

        outState.putInt("initIndex", initIndex);
    }

    /**
     * 适配器
     *
     * @author zhangqiang
     */
    private class ImageAdapter extends PagerAdapter {

        private int smallWidth;
        private int smallHeight;

        @Override
        public int getCount() {
            return image_urls.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {

            View itemView = getLayoutInflater().inflate(R.layout.pager_item_image_display, container, false);

            final ZoomImageView imageView = (ZoomImageView) itemView.findViewById(R.id.zoom_image);

            imageView.enable();
            imageView.setEnabled(false);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
//            big_imageview.setOnLongClickListener(new OnLongClickListener() {
//
//                @Override
//                public boolean onLongClick(View v) {
//
//                    CustomeListDialog.newInstance(new String[]{"保存"})
//                            .setOnDialogItemClickListener(new OnDialogItemClickListener() {
//
//                                @Override
//                                public void onItemClick(CustomeListDialog dialog, String item, int position2) {
//
//                                    dialog.dismiss();
//
//                                    String url = big_image_urls.get(position);
//                                    File file = ImageLoader.getInstance().getDiskCache().get(url);
//                                    String savePath = FileUtils.getImageSavePath();
//                                    if (file != null && file.exists()) {
//
//                                        boolean succ = FileUtils.copyFile(file, new File(savePath));
//                                        if (succ) {
//
//                                            Toast.makeText(ImageDisplayActivity.this, "图片已保存：" + savePath, Toast.LENGTH_SHORT).show();
//                                            return;
//                                        }
//                                    }
//                                    Toast.makeText(ImageDisplayActivity.this, R.string.save_error, Toast.LENGTH_SHORT).show();
//                                }
//                            })
//                            .show(getSupportFragmentManager(), "save_image");
//                    return true;
//                }
//            });

            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP+image_urls.get(position), imageView, options, new ImageLoadingListener() {

                @Override
                public void onLoadingStarted(String arg0, View arg1) {

                }

                @Override
                public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
                }

                @Override
                public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
                    if (arg1 != null) {
                        ((ZoomImageView) arg1).setImageDrawable(new BitmapDrawable(getResources(), arg2));
                    }
                    imageView.setEnabled(true);

                    if (smallWidth > 0 && smallHeight > 0) {

                        float fromX = (float) smallWidth / arg2.getWidth();
                        float fromY = (float) smallHeight / arg2.getHeight();
                        ScaleAnimation animation = new ScaleAnimation(fromX, 1, fromY, 1, ScaleAnimation.RELATIVE_TO_SELF, 0.5f, ScaleAnimation.RELATIVE_TO_SELF, 0.5f);
                        animation.setDuration(200);
                        arg1.startAnimation(animation);
                    }
                }

                @Override
                public void onLoadingCancelled(String arg0, View arg1) {
                }
            });

            container.addView(itemView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            return itemView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
