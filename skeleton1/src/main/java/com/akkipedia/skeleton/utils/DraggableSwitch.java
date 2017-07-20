package com.akkipedia.skeleton.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.CompoundButton;
import android.widget.Switch;

/**
 * Created by bizbrolly on 25/05/17.
 */

public class DraggableSwitch extends Switch {
    private boolean isFromUser = false;

    public DraggableSwitch(Context context) {
        super(context);
    }

    public DraggableSwitch(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DraggableSwitch(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DraggableSwitch(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean result;

        isFromUser = true;
        result = super.onTouchEvent(ev);
        isFromUser = false;

        return result;
    }

    @Override
    public boolean performClick() {
        boolean result;

        isFromUser = true;
        result = super.performClick();
        isFromUser = false;

        return result;
    }


    public static abstract class OnCheckChangeListnener implements OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            onCheckedChanged(buttonView, isChecked, ((DraggableSwitch)buttonView).isFromUser);
        }

        public  abstract void onCheckedChanged(CompoundButton buttonView, boolean isChecked, boolean isFromUser);

    }
}
