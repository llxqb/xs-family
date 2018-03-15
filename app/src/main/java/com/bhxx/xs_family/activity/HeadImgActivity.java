package com.bhxx.xs_family.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;

import com.android.pc.ioc.inject.InjectAll;
import com.android.pc.ioc.inject.InjectLayer;
import com.bhxx.xs_family.R;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.values.GlobalValues;
import com.bhxx.xs_family.views.ZoomImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

@InjectLayer(R.layout.activity_head_img)
public class HeadImgActivity extends BasicActivity {

    @InjectAll
    private Views v;
    private DisplayImageOptions options;

    private class Views {
        ZoomImageView head_big_image;
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(HeadImgActivity.this);
        options = new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnFail(R.mipmap.default_photo).build();
        v.head_big_image.enable();
        v.head_big_image.setEnabled(false);
        if (getIntent() != null) {
            String imgUrl = getIntent().getStringExtra("url");
            ImageLoader.getInstance().displayImage(GlobalValues.IMG_IP + imgUrl, v.head_big_image, options);
        }
    }

    public static void start(Context context, String imgUrl) {
        Intent intent = new Intent(context, HeadImgActivity.class);
        intent.putExtra("url", imgUrl);
        context.startActivity(intent);
    }

    @Override
    protected void click(View view) {

    }
}
