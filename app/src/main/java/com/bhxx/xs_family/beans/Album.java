package com.bhxx.xs_family.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class Album implements Serializable{
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String name;   //相册名字
	private String count; //数量
	private int  bitmap;  // 相册第一张图片
	private List<Item> bitList = new ArrayList<Item>();

	public Album() {
	}


	public Album(String name, String count, int bitmap) {
		super();
		this.name = name;
		this.count = count;
		this.bitmap = bitmap;
	}


	public List<Item> getBitList() {
		return bitList;
	}


	public void setBitList(List<Item> bitList) {
		this.bitList = bitList;
	}


	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	public int getBitmap() {
		return bitmap;
	}
	public void setBitmap(int bitmap) {
		this.bitmap = bitmap;
	}

	@Override
	public String toString() {
		return "PhotoAlbum [name=" + name + ", count=" + count + ", bitmap="
				+ bitmap + ", bitList=" + bitList + "]";
	}
}
