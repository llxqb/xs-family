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
import android.widget.ImageView;
import android.widget.LinearLayout;


import com.bhxx.xs_family.R;
import com.bhxx.xs_family.beans.Item;
import com.bhxx.xs_family.photo.HackyViewPager;
import com.bhxx.xs_family.photo.PhotoView;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.PictureManageUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewPagerDeleteActivity extends BasicActivity {

    public static final String FILES = "files";
    public static final String CURRENT_INDEX = "currentIndex";

    public ArrayList<Item> files = new ArrayList<Item>();
    public int index;
    private LinearLayout btnDelete;
    private ViewPager mViewPager;
    private ArrayList<Integer> deleteIndexs = new ArrayList<Integer>();
    private ArrayList<Integer> remainIndexs = new ArrayList<Integer>();
    private static int iniSize = 0;

    private Map<String, BitmapDrawable> cacheBitmap = new HashMap<String, BitmapDrawable>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_delete);

        mViewPager = (HackyViewPager) findViewById(R.id.view_pager);

//		this.files = this.getIntent().getStringArrayExtra(FILES);
        this.files = this.getIntent().getParcelableArrayListExtra(FILES);
        this.index = this.getIntent().getIntExtra(CURRENT_INDEX, 0);
        iniSize = files.size();
        for (int i = 0; i < iniSize; i++) {
            remainIndexs.add(i);
        }

        ImageView btnBack = (ImageView)findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                remainToDelete();
                intent.putIntegerArrayListExtra("deleteIndexs", deleteIndexs);
                intent.putParcelableArrayListExtra("files", files);
                setResult(RESULT_OK, intent);
                ViewPagerDeleteActivity.this.finish();
            }
        });
        btnDelete = (LinearLayout) findViewById(R.id.deletelayout);
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = mViewPager.getCurrentItem();
                if(files.size()>1){
                    files.remove(position);
                    remainIndexs.remove(position);
                }else{
                    files.remove(0);
                    remainIndexs.remove(0);
                }
                mViewPager.setAdapter(new SamplePagerAdapter());
                if(files.size()>0){
                    if(position == files.size()){
                        mViewPager.setCurrentItem(position-1);
                    }else{
                        mViewPager.setCurrentItem(position);
                    }
                }else{
                    Intent intent = new Intent();
                    remainToDelete();
                    intent.putIntegerArrayListExtra("deleteIndexs", deleteIndexs);
                    setResult(RESULT_OK, intent);
                    ViewPagerDeleteActivity.this.finish();
                }
            }
        });
        SamplePagerAdapter spAdapter = new SamplePagerAdapter();
        mViewPager.setAdapter(spAdapter);
        mViewPager.setCurrentItem(index);
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(this);
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
                bmDrawable = new BitmapDrawable(ViewPagerDeleteActivity.this.getResources(), bitmap);
                cacheBitmap.put(files.get(position).getPhotoPath(), bmDrawable);
                photoView.setImageDrawable(bmDrawable);
            }else{
                photoView.setImageDrawable(cacheBitmap.get(files.get(position).getPhotoPath()));
            }

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

    public void remainToDelete(){
        for(int i=0;i<iniSize;i++){
            boolean flag = false;
            if(remainIndexs.size()>0){
                for(int j=0;j<remainIndexs.size();j++){
                    if(i==remainIndexs.get(j)){
                        flag=true;
                    }
                }
                if(flag==false){
                    deleteIndexs.add(i);
                }
            }else{
                deleteIndexs.add(i);
            }
        }
    }
}
