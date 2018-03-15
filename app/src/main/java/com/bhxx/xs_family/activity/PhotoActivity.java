package com.bhxx.xs_family.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.bhxx.xs_family.R;
import com.bhxx.xs_family.adapter.photo.PicAdappter;
import com.bhxx.xs_family.beans.Album;
import com.bhxx.xs_family.beans.Item;
import com.bhxx.xs_family.utils.ActivityCollector;
import com.bhxx.xs_family.utils.ActivityManager;

import java.io.File;
import java.util.ArrayList;

public class PhotoActivity extends BasicActivity implements View.OnClickListener{

    private GridView gv;
    private Album album;
    private PicAdappter adapter;
    private TextView tv;
    private  int chooseNum = 0;
    private TextView finishBtn;//完成按钮
    private ImageView cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        tv = (TextView)findViewById(R.id.photo_album_chooseNum);
        finishBtn = (TextView)findViewById(R.id.finish);
        cancel = (ImageView) findViewById(R.id.cancel);
        album = (Album)getIntent().getExtras().get("album");
        /**获取已经选择的图片**/
        for (int i = 0; i < album.getBitList().size(); i++) {
            if(album.getBitList().get(i).isSelect()){
                chooseNum++;
            }
        }
        gv =(GridView)findViewById(R.id.photo_gridview);
        adapter = new PicAdappter(this,album);
        gv.setAdapter(adapter);
        tv.setText("选中"+chooseNum+"个");
        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Item> fileNamesList = new ArrayList<Item>();
                for (int i = 0; i < album.getBitList().size(); i++) {
                    if(album.getBitList().get(i).isSelect()){
                        fileNamesList.add(album.getBitList().get(i));
                    }
                }
                Intent it = new Intent();
                it.putParcelableArrayListExtra("fileNames", fileNamesList);
                ActivityManager.getActivity("PhotoAlbumActivity").setResult(RESULT_OK, it);
                ActivityManager.getActivity("PhotoAlbumActivity").finish();
                ActivityManager.removeActivity("PhotoAlbumActivity");
                PhotoActivity.this.finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    protected void init() {
        ActivityCollector.addActivity(PhotoActivity.this);
    }

    @Override
    protected void click(View view) {

    }


    @Override
    public void onClick(View view) {

            if(view.getTag().toString().startsWith("select")){
                String vPosition=view.getTag().toString().substring(7);
                isselected(vPosition);
            }
            else{
                String vPosition=view.getTag().toString().substring(6);
                isselected(vPosition);

                /*String vPosition=view.getTag().toString().substring(6);
                Intent intent = new Intent(PhotoActivity.this, ViewPagerActivity.class);
                final String paths = album.getBitList().get(Integer.parseInt(vPosition)).getPhotoPath();
                new AlertDialog.Builder(PhotoActivity.this).setMessage(paths)
                        .setPositiveButton("删除？", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                File f = new File(paths);
                                if(!f.exists()){
                                    Toast.makeText(PhotoActivity.this, "该图片不存在", Toast.LENGTH_SHORT).show();
                                }else{
                                    f.delete();
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

                //List->>ArrayList
                ArrayList<Item>  fileNames= new ArrayList<Item> ();
                for(int i = 0;i<album.getBitList().size();i++){
                    fileNames.add(album.getBitList().get(i));
                }
                intent.putExtra(ViewPagerActivity.FILES, fileNames);
                intent.putExtra(ViewPagerActivity.CURRENT_INDEX, Integer.parseInt(vPosition));
//				startActivity(intent);
            }*/
        }
    }

    private void isselected(String vPosition){
        if( album.getBitList().get(Integer.parseInt(vPosition)).isSelect()){
            album.getBitList().get(Integer.parseInt(vPosition)).setSelect(false);
            chooseNum--;
        }else{
            if(chooseNum>5){
                showToast("最多上传6张图片");
                return;
            }
            album.getBitList().get(Integer.parseInt(vPosition)).setSelect(true);
            chooseNum++;
        }
        tv.setText("选中"+chooseNum+"个");
        adapter.notifyDataSetChanged();
    }
}
