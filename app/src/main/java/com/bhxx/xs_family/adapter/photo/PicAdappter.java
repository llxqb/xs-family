package com.bhxx.xs_family.adapter.photo;

import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore.Images.Thumbnails;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;


import com.bhxx.xs_family.beans.Album;
import com.bhxx.xs_family.utils.PictureManageUtil;

import java.util.HashMap;
import java.util.Map;


public class PicAdappter extends BaseAdapter   {
	private Context context;
	private Album album;
	private Map<String, Bitmap> cacheBms = new HashMap<String, Bitmap>();
	
	public PicAdappter(Context context, Album album) {
		this.context = context;
		this.album = album;
	}

	@Override
	public int getCount() {
		return album.getBitList().size();
	}

	@Override
	public Object getItem(int position) {
		return album.getBitList().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		PhotoGridItem item;
		if(convertView == null){
			item = new PhotoGridItem(context);
			item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,  
                     LayoutParams.MATCH_PARENT));
		}else{
			item = (PhotoGridItem)convertView;
		}
		item.getmSelect().setTag("select_"+position);
        item.getmImageView().setTag("image_"+position);
        if(cacheBms.get(position+"")==null){
        	Bitmap bitmap = Thumbnails.getThumbnail(context.getContentResolver(),  album.getBitList().get(position).getPhotoID(), Thumbnails.MICRO_KIND, null);
        	int rotate = PictureManageUtil.getCameraPhotoOrientation(album.getBitList().get(position).getPhotoPath());
			bitmap = PictureManageUtil.rotateBitmap(bitmap, rotate);
        	item.SetBitmap(bitmap);
        	cacheBms.put(position+"", bitmap);
        }else{
        	item.SetBitmap(cacheBms.get(position+""));
        }
        boolean flag = album.getBitList().get(position).isSelect();
		item.setChecked(flag);
		return item;
	}
}
