package com.dandrzas.onlinemap;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatImageView;

public class ImageViewWithRectDraw extends AppCompatImageView {
    private Paint paint;
    public boolean drawRect=false;
    public float xTouchCoordinate, yTouchCoordinate, startX, startY;


    public ImageViewWithRectDraw(Context context) {
        super(context);
        init();
    }

    public ImageViewWithRectDraw(Context context, AttributeSet attrs) {

        super(context, attrs);
        init();
    }

    public ImageViewWithRectDraw(Context context, AttributeSet attrs, int defStyleAttr) {

        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAlpha(50);
        paint.setStrokeWidth(5);
    }

    @Override
    public void onDrawForeground(Canvas canvas) {
        super.onDrawForeground(canvas);


        if(drawRect) {
            canvas.drawRect(startX, startY, xTouchCoordinate, yTouchCoordinate, paint);
        }else    canvas.drawRect(0, 0, 0, 0, paint);
        Log.d("draw start x:", Float.toString(startX));
        Log.d("draw start y:", Float.toString(startY));
        Log.d("draw end x:", Float.toString(xTouchCoordinate));
        Log.d("draw end y:", Float.toString(yTouchCoordinate));
    }


    public void setxTouchCoordinate(double xTouchCoordinate) {
        this.xTouchCoordinate = (float) xTouchCoordinate;
    }

    public void setyTouchCoordinate(double yTouchCoordinate) {
        this.yTouchCoordinate = (float) yTouchCoordinate;
    }

    public void setStartX(double startX) {
        this.startX = (float )startX;
    }

    public void setStartY(double startY) {
        this.startY = (float) startY;
    }

    public void setDrawRect(boolean drawRect) {
        this.drawRect = drawRect;
    }

    public void setDrawColor(int color)
    {
            paint.setColor(color);
            paint.setAlpha(50);
    }

}

