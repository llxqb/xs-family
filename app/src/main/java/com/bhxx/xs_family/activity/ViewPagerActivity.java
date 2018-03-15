package com.bhxx.xs_family.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bhxx.xs_family.R;
import com.bhxx.xs_family.beans.Item;
import com.bhxx.xs_family.photo.PhotoView;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.PictureManageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewPagerActivity extends BasicActivity {

    public static final String FILES = "files";
    public static final String CURRENT_INDEX = "currentIndex";

    private ArrayList<Item> files = new ArrayList<Item>();
    public int index;
    private Button btnCheck;
    private final int VIEW_PAGER_CODE = 1122;
    private int chooseNum = 0;
    private TextView tv;
    private ViewPager mViewPager;
    private Map<String, BitmapDrawable> cacheBitmap = new HashMap<String, BitmapDrawable>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager);

        files = this.getIntent().getParcelableArrayListExtra(FILES);
        this.index = this.getIntent().getIntExtra(CURRENT_INDEX, 0);

        /**获取已经选择的图片**/
        for (int i = 0; i < files.size(); i++) {
            if(files.get(i).isSelect()){
                chooseNum++;
            }
        }
        tv = (TextView)findViewById(R.id.photo_album_chooseNum);
        tv.setText("选中"+chooseNum+"个");

        Button btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putParcelableArrayListExtra(FILES, files);
                setResult(VIEW_PAGER_CODE, data);
                ViewPagerActivity.this.finish();
            }
        });
        btnCheck = (Button)findViewById(R.id.btnCheck);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mViewPager.getCurrentItem();
                boolean isSelect = files.get(position).isSelect();
                files.get(position).setSelect(!isSelect);
                if(isSelect){
                    chooseNum--;
                    btnCheck.setBackgroundResource(R.mipmap.user_rb);

                }else{
                    chooseNum++;
                    btnCheck.setBackgroundResource(R.mipmap.user_rb_pre);
                }
                tv.setText("选中"+chooseNum+"个");
            }
        });
        boolean isSelect = files.get(mViewPager.getCurrentItem()).isSelect();
        if(isSelect){
            btnCheck.setBackgroundResource(R.mipmap.user_rb_pre);
        }else{
            btnCheck.setBackgroundResource(R.mipmap.user_rb);
        }
        mViewPager.setAdapter(new SamplePagerAdapter());
        mViewPager.setCurrentItem(index);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                boolean isSelect = files.get(position).isSelect();
                if(isSelect){
                    btnCheck.setBackgroundResource(R.mipmap.user_rb_pre);
                }else{
                    btnCheck.setBackgroundResource(R.mipmap.user_rb);
                }
            }

            @Override
            public void onPageScrolled(int position, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        //完成
        Button btnFinish = (Button)findViewById(R.id.btnFinish);
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent data = new Intent();
                data.putParcelableArrayListExtra(FILES, files);
                setResult(VIEW_PAGER_CODE, data);
                ViewPagerActivity.this.finish();
            }
        });
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(ViewPagerActivity.this);
    }

    @Override
    protected void click(View view) {

    }

    class SamplePagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return files.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            BitmapDrawable bmDrawable;
            if(cacheBitmap.get(files.get(position).getPhotoPath())==null){
                //根据路径，得到一个压缩过的Bitmap（宽高较大的变成500，按比例压缩）
                Bitmap bitmap = PictureManageUtil.getCompressBm(files.get(position).getPhotoPath());
                //获取旋转参数
                int rotate = PictureManageUtil.getCameraPhotoOrientation(files.get(position).getPhotoPath());
                //把压缩的图片进行旋转
                bitmap = PictureManageUtil.rotateBitmap(bitmap, rotate);
                bmDrawable = new BitmapDrawable(ViewPagerActivity.this.getResources(), bitmap);
                cacheBitmap.put(files.get(position).getPhotoPath(), bmDrawable);
                photoView.setImageDrawable(bmDrawable);
            }else{
                photoView.setImageDrawable(cacheBitmap.get(files.get(position).getPhotoPath()));
            }
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.gc();
    }
}
