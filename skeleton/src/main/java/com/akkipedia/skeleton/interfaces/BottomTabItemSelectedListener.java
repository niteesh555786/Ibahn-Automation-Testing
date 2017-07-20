package com.akkipedia.skeleton.interfaces;

import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.LinearLayout;

/**
 * Created by Ayush on 20/11/16.
 */

public interface BottomTabItemSelectedListener {
    void onTabSelected(@NonNull MenuItem item, LinearLayout container);
}
