package com.bhxx.xs_family.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bhxx.xs_family.R;


public class RoundImage extends ImageView {
    private int roundWidth = 13;
    private int roundHeight = 13;

    public RoundImage(Context context) {
        super(context);
        init(context, null);
    }

    public RoundImage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context, attrs);
    }

    public RoundImage(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.RoundAngleImageView);
            roundWidth = a.getDimensionPixelSize(
                    R.styleable.RoundAngleImageView_roundWidth, roundWidth);
            roundHeight = a.getDimensionPixelSize(
                    R.styleable.RoundAngleImageView_roundHeight, roundHeight);
            a.recycle();
        } else {
            float density = context.getResources().getDisplayMetrics().density;
            roundWidth = (int)(roundWidth * density);
            roundHeight = (int)(roundHeight * density);
        }
    }

    /** 重写draw() */
    @Override
    public void draw(Canvas canvas) {

        //实例化一个和ImageView一样大小的bitmap
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Config.ARGB_8888);

        //实例化一个canvas，这个canvas对应的内存为上面的bitmap
        Canvas canvas2 = new Canvas(bitmap);
        if (bitmap.isRecycled()) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                    Config.ARGB_8888);
            canvas2 = new Canvas(bitmap);
        }

        //将imageView自己绘制到canvas2上，这个导致bitmap里面存放了imageView
        super.draw(canvas2);

        //利用canvas画一个圆角矩形，这个会修改bitmap的数据
        drawRoundAngle(canvas2);

        //将裁剪好的bitmap绘制到系统当前canvas上，这样裁剪好的imageview就能显示到屏幕上
        Paint paint = new Paint();
        paint.setXfermode(null);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        bitmap.recycle();
    }

    public void setRoundWidth(int roundWidth, int roundHeight) {
        this.roundWidth = roundWidth;
        this.roundHeight = roundHeight;
    }

    private void drawRoundAngle(Canvas canvas)
    {
        Paint maskPaint = new Paint();
        maskPaint.setAntiAlias(true);
        maskPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        Path maskPath = new Path();
        maskPath.addRoundRect(new RectF(0.0F, 0.0F, getWidth(), getHeight()), roundWidth, roundHeight, Path.Direction.CW);

        //这是设置了填充模式，非常关键
        maskPath.setFillType(Path.FillType.INVERSE_WINDING);
        canvas.drawPath(maskPath, maskPaint);
    }
}  