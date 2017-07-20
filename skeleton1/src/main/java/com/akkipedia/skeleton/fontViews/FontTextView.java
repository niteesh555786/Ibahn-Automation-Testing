package com.akkipedia.skeleton.fontViews;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bizbrolly.skeleton.R;

/**
 * Created by Akash on 15/11/16.
 */

public class FontTextView extends TextView {

    public FontTextView(Context context) {
        super(context);
    }

    public FontTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontApplier.applyCustomFont(context, attrs, this);
    }

    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        FontApplier.applyCustomFont(context, attrs, this);
    }

    public FontTextView(Context context,String fontPath) {
        super(context);
        FontApplier.applyCustomFont(context, fontPath, this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FontTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        FontApplier.applyCustomFont(context, attrs, this);
    }


}
