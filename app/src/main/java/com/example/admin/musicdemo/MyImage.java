package com.example.admin.musicdemo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;


public class MyImage extends ImageView {
    private Paint paint;
    public MyImage(Context context) {
        this(context,null);
    }

    public MyImage(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyImage(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint();
        paint.setAntiAlias(true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Drawable drawable = getDrawable();
        if(drawable!=null){
            Bitmap bitmap = ((BitmapDrawable)(drawable)).getBitmap();
            Bitmap bitmapTemp = Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas1 = new Canvas(bitmapTemp);
            Rect rectSrc = new Rect(0,0,bitmap.getWidth(),bitmap.getHeight());
            Rect rectBg = new Rect(10,10,bitmap.getWidth()-10,bitmap.getHeight()-10);
            int x= bitmap.getWidth()/2;
            canvas1.drawCircle(x,x,x,paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            canvas1.drawBitmap(bitmap,rectSrc,rectSrc,paint);
            paint.reset();
            canvas.drawBitmap(bitmapTemp,rectSrc,rectBg,paint);
        }else {
            super.onDraw(canvas);
        }
    }
}
