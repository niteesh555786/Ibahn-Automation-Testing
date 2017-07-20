package com.akkipedia.skeleton.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Handler;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akkipedia.skeleton.interfaces.BottomTabItemSelectedListener;
import com.akkipedia.skeleton.interfaces.DrawerClickListener;
import com.akkipedia.skeleton.utils.ScreenUtils;
import com.bizbrolly.skeleton.R;

import java.util.List;

/**
 * Created by Akash on 15/11/16.
 *
 */

public class BaseSkeletonActivity extends AppCompatActivity{
    private boolean contentViewSet = false;
    DrawerLayout drawerLayout;
    //region Methods for progress dialog
    protected ProgressDialog progressDialog;

    public final void showProgressDialog(String title, String message){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
    }

    public final void showProgressDialog(String title, String message, int timeOut){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setCancelable(false);
        }
        progressDialog.setTitle(title);
        progressDialog.setMessage(message);
        if(!progressDialog.isShowing()){
            progressDialog.show();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideProgressDialog();
            }
        }, timeOut);
    }

    public void startActivityWithTransition(Intent i){
        startActivity(i);
        overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
    }

    protected final void runDelayed(Runnable runnable, int delayMils){
        new Handler().postDelayed(runnable, delayMils);
    }

    protected final void runDelayed(Runnable runnable){
        runDelayed(runnable, 100);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.exit_slide_in, R.anim.exit_slide_out);
    }

    public final void showProgressDialog(){
        showProgressDialog("Loading", "Please wait");
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideProgressDialog();
    }

    public final void hideProgressDialog(){
        if(progressDialog!=null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    //endregion

    //region Methods for alert dialog

    protected final void showAlertDialog(String title, String message, final int dialogId){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onNegativeButtonClick(dialogInterface, dialogId);
                    }
                })
                .show();
    }

    public final AlertDialog createListDialog(
            String[]list,
            DialogInterface.OnClickListener onClickListener
    ){
        ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, list
        );
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setAdapter(
                        dialogAdapter,
                        onClickListener
                );
        return dialogBuilder.create();
    }


    public final AlertDialog createListDialog(
            List<String>list,
            DialogInterface.OnClickListener onClickListener
    ){
        ArrayAdapter<String> dialogAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1, list
        );
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this)
                .setAdapter(
                        dialogAdapter,
                        onClickListener
                );
        return dialogBuilder.create();
    }

    public final void showAlertDialog(String title, String message, String negativeButtonText, final int dialogId){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onNegativeButtonClick(dialogInterface, dialogId);
                    }
                })
                .show();
    }

    public final void showAlertDialog(String title, String message, String negativeButtonText, String positiveButtonText, final int dialogId){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setCancelable(false)
                .setTitle(title)
                .setMessage(message)
                .setNegativeButton(negativeButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onNegativeButtonClick(dialogInterface, dialogId);
                    }
                })
                .setPositiveButton(positiveButtonText, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onPositiveButtonClick(dialogInterface, dialogId);
                    }
                })
                .show();
    }

    public void onPositiveButtonClick(DialogInterface dialog,int dialogId){
        throw new RuntimeException("onPositiveButtonClick needs to be overridden when using positive button in alert dialog");
    }

    public void onNegativeButtonClick(DialogInterface dialog,int dialogId){

    }

    //endregion

    //region Methods for Navigation Drawer

    protected final void setContentWithNavigationDrawer(
            int mainLayoutId,
            final List<String> drawerOptions,
            final DrawerClickListener drawerClickListener
    ){
        if(contentViewSet){
            throw new RuntimeException("setContentWithNavigationDrawer() must be called before setContentView()");
        }
        drawerLayout = new DrawerLayout(this);
        DrawerLayout.LayoutParams drawerLayoutParams =new DrawerLayout.LayoutParams(
                ScreenUtils.dpToPx(280),
                DrawerLayout.LayoutParams.MATCH_PARENT
        );
        drawerLayoutParams.gravity = Gravity.START;


        View content = LayoutInflater.from(this).inflate(mainLayoutId, null, false);
        drawerLayout.addView(content, new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        ListView drawerList = new ListView(this);
        drawerList.setBackgroundColor(Color.WHITE);

        ArrayAdapter<String> drawerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                drawerOptions
        );
        drawerLayout.addView(drawerList, drawerLayoutParams);
        drawerList.setAdapter(drawerAdapter);

        setContentView(drawerLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view,final int i, long l) {
                drawerLayout.closeDrawer(Gravity.LEFT);
                drawerLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        drawerClickListener.onDrawerItemClicked(i, drawerOptions.get(i));
                    }
                });
            }
        });
        customiseNavigationDrawer(drawerLayout,
                drawerList,
                drawerAdapter
        );
    }

    protected final View setContentWithNavigationDrawer(
            int mainLayoutId,
            int drawerViewLayoutId
    ){
        if(contentViewSet){
            throw new RuntimeException("setContentWithNavigationDrawer() must be called before setContentView()");
        }
        drawerLayout = new DrawerLayout(this);
        DrawerLayout.LayoutParams drawerLayoutParams =new DrawerLayout.LayoutParams(
                ScreenUtils.dpToPx(300),
                DrawerLayout.LayoutParams.MATCH_PARENT
        );
        drawerLayoutParams.gravity = Gravity.START;


        View content = LayoutInflater.from(this).inflate(mainLayoutId, null, false);
        drawerLayout.addView(content, new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        View drawerViewLayout = LayoutInflater.from(this).inflate(drawerViewLayoutId, null, false);
        drawerLayout.addView(drawerViewLayout, drawerLayoutParams);

        setContentView(drawerLayout);
        return drawerViewLayout;
    }

    protected final void setUpDrawerWithToolbar(Toolbar toolbar){
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        if(drawerLayout!=null) {
            ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(

                    this, drawerLayout, toolbar,

                    0, 0

            ){
                @Override
                public void onDrawerOpened(View drawerView) {
                    super.onDrawerOpened(drawerView);
                    BaseSkeletonActivity.this.onDrawerOpened(drawerView);
                }

                @Override
                public void onDrawerClosed(View drawerView) {
                    super.onDrawerClosed(drawerView);
                    BaseSkeletonActivity.this.onDrawerClosed(drawerView);
                }
            };

            drawerLayout.addDrawerListener(drawerToggle);
            drawerToggle.syncState();
        }
        else
            throw new RuntimeException("setContentWithNavigationDrawer should be overridden before adding toolbar to drawer");
    }

    //TODO add actions onDrawerOpened and onDrawerClosed in navigation drawer even if it doesn't have toolbar
    protected void onDrawerOpened(View drawerView){}
    protected void onDrawerClosed(View drawerView){}


    protected final void setContentWithNavigationDrawer(
            int mainLayoutId
    ){
        if(contentViewSet){
            throw new RuntimeException("setContentWithNavigationDrawer() must be called before setContentView()");
        }
        drawerLayout = new DrawerLayout(this);
        DrawerLayout.LayoutParams drawerLayoutParams =new DrawerLayout.LayoutParams(
                ScreenUtils.dpToPx(280),
                DrawerLayout.LayoutParams.MATCH_PARENT
        );
        drawerLayoutParams.gravity = Gravity.START;
        View content = LayoutInflater.from(this).inflate(mainLayoutId, null, false);
        drawerLayout.addView(content, new DrawerLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        RelativeLayout drawerView = new RelativeLayout(this);
        drawerLayout.addView(drawerView, drawerLayoutParams);
        customiseNavigationDrawer(drawerLayout, drawerView);
    }

    /**
     * Override this method to customise the navigation drawer
     */
    protected void customiseNavigationDrawer(
            DrawerLayout drawerLayout,
            ListView drawerList,
            ArrayAdapter<String> drawerAdapter
    ){

    }

    protected void customiseNavigationDrawer(
            DrawerLayout drawerLayout,
            RelativeLayout drawerView
    ){
        throw new RuntimeException("customiseNavigationDrawer() must be overridden when using setContentWithNavigationDrawer(int mainLayoutId)");
    }


    /**
     * Making sure setContentWithNavigationDrawer() is called before setContentView().
     */
    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        contentViewSet = true;
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        contentViewSet = true;
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        contentViewSet = true;
    }


    //endregion


    //region Tab related regions

    protected final void addTabs(RelativeLayout rootLayout, int menuId, final BottomTabItemSelectedListener tabItemSelectedListener){
        BottomNavigationView bottomTabs = new BottomNavigationView(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ScreenUtils.dpToPx(56));
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        bottomTabs.setLayoutParams(layoutParams);
        bottomTabs.inflateMenu(menuId);
        bottomTabs.setBackgroundColor(Color.GRAY);

        ColorStateList colorStateList = new ColorStateList (
                new int [] [] {
                        new int [] {}
                },
                new int [] {
                        Color.WHITE
                }
        );
        bottomTabs.setItemTextColor(colorStateList);
        bottomTabs.setItemIconTintList(colorStateList);
        rootLayout.addView(bottomTabs);
        final LinearLayout container = new LinearLayout(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams1.bottomMargin = ScreenUtils.dpToPx(56);
        container.setLayoutParams(layoutParams1);
        rootLayout.addView(container);
        bottomTabs.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                tabItemSelectedListener.onTabSelected(item,container);
                return false;
            }
        });

    }

    //endregion


    public void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

}
