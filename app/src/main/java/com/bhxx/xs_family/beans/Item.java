package com.bhxx.xs_family.beans;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Item implements Parcelable, Serializable, Comparable<Item> {
	private static final long serialVersionUID = 8682674788506891598L;
	private int photoID; // 图片ID
	private String photoPath; // 图片路径
	private String dateTaken; // 时间
	private boolean select; // 是否选中

	public Item() {
		select = false;
	}

	public Item(int id) {
		photoID = id;
		select = false;
	}

	public Item(int id, String path) {
		photoID = id;
		photoPath = path;
	}

	public Item(int id, String path, String dTaken) {
		photoID = id;
		photoPath = path;
		dateTaken = dTaken;
	}

	public Item(int id, boolean flag) {
		photoID = id;
		select = flag;
	}

	public int getPhotoID() {
		return photoID;
	}

	public void setPhotoID(int photoID) {
		this.photoID = photoID;
	}

	public boolean isSelect() {
		return select;
	}

	public void setSelect(boolean select) {
		this.select = select;
	}

	public String getPhotoPath() {
		return photoPath;
	}

	public void setPhotoPath(String photoPath) {
		this.photoPath = photoPath;
	}

	public String getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(String dateTaken) {
		this.dateTaken = dateTaken;
	}


	@Override
	public String toString() {
		return "PhotoItem [photoID=" + photoID + ", select=" + select + "]";
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(photoID);
		dest.writeString(photoPath);
		dest.writeString(dateTaken);
		if(select){
			dest.writeInt(1);
		}else{
			dest.writeInt(0);
		}
	}

	public static final Creator<Item> CREATOR = new Creator<Item>() {
		// 重写Creator

		@Override
		public Item createFromParcel(Parcel source) {
			Item p = new Item();
			p.photoID = source.readInt();
			p.photoPath = source.readString();
			p.dateTaken = source.readString();
			int selectInt = source.readInt();
			if(selectInt == 1){
				p.select = true;
			}else{
				p.select = false;
			}
			return p;
		}

		@Override
		public Item[] newArray(int size) {
			return new Item[size];
		}
	};

	/**
	 * 按日期倒叙排序
	 */
	@Override
	public int compareTo(Item another) {
		if(this.getDateTaken().compareTo(another.getDateTaken())>0){
			return -1;
		}else if(this.getDateTaken().compareTo(another.getDateTaken())<0){
			return 1;
		}
		return 0;
	}
}
