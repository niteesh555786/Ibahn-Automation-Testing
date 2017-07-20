package com.akkipedia.skeleton.fontViews;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.bizbrolly.skeleton.R;

/**
 * Created by Akash on 15/11/16.
 */


public class FontApplier {

    private static final String ANDROID_SCHEMA = "http://schemas.android.com/apk/res/android";

    static void applyCustomFont(Context context, AttributeSet attrs, TextView textView) {
        TypedArray attributeArray = context.obtainStyledAttributes(
                attrs,
                R.styleable.FontTextView);

        String fontName = attributeArray.getString(R.styleable.FontTextView_font);
        int textStyle = attrs.getAttributeIntValue(ANDROID_SCHEMA, "textStyle", Typeface.NORMAL);

        Typeface customFont = selectTypeface(context, fontName, textStyle);
        textView.setTypeface(customFont);

        attributeArray.recycle();
    }

    static void applyCustomFont(Context context, String fontPath, TextView textView) {
        Typeface customFont = selectTypeface(context, fontPath, 0);
        textView.setTypeface(customFont);

    }

    private static Typeface selectTypeface(Context context, String fontName, int textStyle) {

        if(fontName == null){
            return null;
        }
//        if (fontName.contentEquals("Roboto")) {
//            return null;
//        }
//        else {
        return FontCache.getTypeface(fontName, context);
//        }
    }
}