package com.bhxx.xs_family.utils;

import android.graphics.RectF;
import android.widget.ImageView;

public class Info {
	public RectF mRect = new RectF();
	RectF mLocalRect = new RectF();
	public RectF mImgRect = new RectF();
	public RectF mWidgetRect = new RectF();
	float mScale;
	public float mDegrees;
	public ImageView.ScaleType mScaleType;

	public Info(RectF rect, RectF local, RectF img, RectF widget, float scale,
			float degrees, ImageView.ScaleType scaleType) {
		mRect.set(rect);
		mLocalRect.set(local);
		mImgRect.set(img);
		mWidgetRect.set(widget);
		mScale = scale;
		mScaleType = scaleType;
		mDegrees = degrees;
	}
}
