package com.akkipedia.skeleton.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Region;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.bizbrolly.skeleton.R;

/**
 * Created by bizbrolly on 8/30/16.
 */
public class CircleMaskView extends ImageView {
    private Path circlePath;
    private Path circleBorderPath;
    private Paint mBorderPaint;
    private Paint transparentPaint;
    float radius;


    public CircleMaskView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircleMaskView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        setWillNotDraw(false);
        //Get attribs
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.CircleMaskView);

        int borderColor = attributeArray.getColor(R.styleable.CircleMaskView_borderColor, Color.TRANSPARENT);
        float borderWidth = attributeArray.getDimension(R.styleable.CircleMaskView_borderWidth, ScreenUtils.dpToPx(1));


        attributeArray.recycle();


        this.circlePath = new Path();
        this.circleBorderPath = new Path();


        this.mBorderPaint = new Paint();
        setBorderColor(borderColor);
        this.mBorderPaint.setAntiAlias(true);
//        this.mBorderPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        this.mBorderPaint.setStrokeCap(Paint.Cap.ROUND);
        setBorderWidth(borderWidth);
        this.mBorderPaint.setStyle(Paint.Style.STROKE);
        this.transparentPaint = new Paint();
        transparentPaint.setAntiAlias(true);
        this.transparentPaint.setColor(Color.TRANSPARENT);
        this.transparentPaint.setStyle(Paint.Style.FILL);
    }

    public void setRadius(float radius) {
        calculatePath(radius);
        invalidate();
    }

    public void setBorderWidth(float width){
        this.mBorderPaint.setStrokeWidth(width);
        invalidate();
    }

    public void setBorderColor(int color) {
        this.mBorderPaint.setColor(color);
        invalidate();
    }

    private void calculatePath(float radius) {
        this.radius = radius;
        float centerX = ((float)getMeasuredWidth()) / 2f;
        float centerY = ((float)getMeasuredHeight()) / 2f;

        circlePath.reset();
        circlePath.moveTo(centerX + radius, centerY);
        circlePath.addCircle(
                centerX,
                centerY,
                radius,
                Path.Direction.CW
        );

        float borderWidth = 1f;

        circleBorderPath.reset();
        circleBorderPath.moveTo(centerX + (radius/* - borderWidth*/), centerY);
        circleBorderPath.addCircle(
                centerX,
                centerY,
                radius/* - borderWidth*/,
                Path.Direction.CW
        );
        invalidate();
    }


    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
        c.drawCircle(
                getMeasuredWidth() / 2f,
                getMeasuredHeight() / 2f,
                radius,
                transparentPaint
        );
        c.drawPath(circleBorderPath, mBorderPaint);
        c.clipPath(circlePath/*, Region.Op.INTERSECT*/);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(width, height);
        calculatePath(Math.min(width / 2f, height / 2f) - 10f);
    }

}
