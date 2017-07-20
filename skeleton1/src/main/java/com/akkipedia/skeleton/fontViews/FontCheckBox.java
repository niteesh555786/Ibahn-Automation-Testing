package com.akkipedia.skeleton.fontViews;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.CheckBox;

/**
 * Created by Akash on 28/11/16.
 */

public class FontCheckBox extends CheckBox{

    public FontCheckBox(Context context) {
        super(context);
    }

    public FontCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        FontApplier.applyCustomFont(context, attrs, this);
    }

    public FontCheckBox(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        FontApplier.applyCustomFont(context, attrs, this);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FontCheckBox(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        FontApplier.applyCustomFont(context, attrs, this);
    }

}
