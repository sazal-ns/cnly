/*
 * Copyright (c) 2016. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.GoogleMap;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.DialogShow;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.DownLoadImageTask;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.GPSTracker;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.PrefUtils;
import com.ns.siddiqui.sazal.clny_v20.model.FbUser;
import com.ns.siddiqui.sazal.clny_v20.model.User;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{

    public static int navItemIndex = 0;

    private static final String TAG_HOME = "Home";
    private static final String TAG_FREE = "Free Clyns";
    private static final String TAG_CALENDER = "Clyns Calender";
    private static final String TAG_PAYMENT = "Payment";
    private static final String TAG_SETTINGS = "Settings";
    private static final String TAG_CLEAN = "Clean";
    private static final String TAG_SUPPORT = "Support";
    public static String CURRENT_TAG = TAG_HOME;

    private boolean shouldLoadHomeFragOnBackPress = true;
    private Handler mHandler;

    private  NavigationView navigationView,footerView;
    private DrawerLayout drawer;
    private  Toolbar toolbar;

    public Context context;

    public static Bitmap bitmap;
    CircleImageView pic;
   public  Intent fbIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mHandler = new Handler();

        preInti();

        navLoad();

        setUpNavigationView();

        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }
    }

    private void setUpNavigationView() {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_home:
                        if (CURRENT_TAG.equals(TAG_HOME))
                            new DialogShow(MainActivity.this,"Where am I?","You are at Home",getResources().getDrawable(R.drawable.ic_home_black_24dp));
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        break;
                    case R.id.nav_gift:
                        if (CURRENT_TAG.equals(TAG_FREE))
                            new DialogShow(MainActivity.this,"Where am I?","You are at Free Clyn",getResources().getDrawable(R.drawable.ic_card_giftcard_black_24dp));
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_FREE;
                        break;
                    case R.id.nav_calender:
                        if (CURRENT_TAG.equals(TAG_FREE))
                            new DialogShow(MainActivity.this,"Where am I?","You are at Free Clyn",getResources().getDrawable(R.drawable.ic_insert_invitation_black_24dp));
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_CALENDER;
                        break;
                    case R.id.nav_payments:
                        if (CURRENT_TAG.equals(TAG_FREE))
                            new DialogShow(MainActivity.this,"Where am I?","You are at Free Clyn",getResources().getDrawable(R.drawable.ic_credit_card_black_24dp));
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_PAYMENT;
                        break;
                    case R.id.nav_clean:
                        if (CURRENT_TAG.equals(TAG_FREE))
                            new DialogShow(MainActivity.this,"Where am I?","You are at Free Clyn",getResources().getDrawable(R.drawable.ic_brush_black_24dp));
                        navItemIndex = 4;
                        CURRENT_TAG = TAG_CLEAN;
                        break;
                    default:
                        navItemIndex =0;
                }
                loadHomeFragment();
                return true;
            }
        });

        footerView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.nav_settings:
                        navItemIndex = 5;
                        CURRENT_TAG = TAG_SETTINGS;
                        break;
                    case R.id.nav_support:
                        startActivity(new Intent(MainActivity.this, SupporMainActivity.class));
                        drawer.closeDrawers();
                        return true;
                    default:
                        navItemIndex = 0;
                }

                loadHomeFragment();
                return false;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }
            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    private void loadHomeFragment() {

        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
            drawer.closeDrawers();
            return;
        }

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                        android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commitAllowingStateLoss();
            }
        };

        if (runnable != null) {
            mHandler.post(runnable);
        }

        drawer.closeDrawers();

        invalidateOptionsMenu();

    }

    private Fragment getHomeFragment() {
        switch (navItemIndex) {
            case 10:
                return new ProfileFragment();
            default:
                return new MapFragment();
        }
    }

    private void loadFbData(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                URL imageURL = null;
                try {
                    imageURL = new URL("https://graph.facebook.com/" + FbUser.facebookID + "/picture?type=large");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    if (imageURL != null) {
                        bitmap  = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                pic.setImageBitmap(bitmap);
            }
        }.execute();
    }


    private void navLoad(){
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

            navigationView = (NavigationView) findViewById(R.id.nav_view);
            View header = navigationView.getHeaderView(0);
            footerView = (NavigationView) findViewById(R.id.navigation_drawer_bottom);

            Button viewProfile = (Button) header.findViewById(R.id.viewProfileButton);
            viewProfile.setOnClickListener(this);

            pic = (CircleImageView) header.findViewById(R.id.pic);
            TextView nameTextView = (TextView) header.findViewById(R.id.nameTextView);

            if (User.getFirstName() != "null" && User.getLastName() != "null") {
                nameTextView.setText(User.getFirstName() + " " + User.getLastName());
            } else if (User.getFirstName() != "null") {
                nameTextView.setText(User.getFirstName());
            } else if (User.getLastName() != "null") {
                nameTextView.setText(User.getLastName());
            } else {
                nameTextView.setText(User.getUserName());
            }
        if (fbIntent.getStringExtra("fb").equals("itIs")){
            nameTextView.setText(FbUser.name);
            loadFbData();
        }
        else if (!User.getImageLink().contains("null") && !User.getImageLink().contains("googleusercontent")) {
                String picUrl = "http://app.clynpro.com/image/";
                new DownLoadImageTask(pic).execute(picUrl + User.getImageLink());
        }else if (fbIntent.getStringExtra("fb").equals("itIsG+")){
            nameTextView.setText(FbUser.name);
            new DownLoadImageTask(pic).execute(User.getImageLink());
        }
    }

    private void preInti() {
        fbIntent = getIntent();

    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }
        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }

        super.onBackPressed();
    }


    @Override
    protected void onUserLeaveHint() {

        PrefUtils.clearCurrentUser(MainActivity.this);
        // We can logout from facebook by calling following method
        LoginManager.getInstance().logOut();
        Log.e("Logout","Done in onUserLeaveHint");

        super.onUserLeaveHint();
    }

    @Override
    protected void onDestroy() {
        PrefUtils.clearCurrentUser(MainActivity.this);
        // We can logout from facebook by calling following method
        LoginManager.getInstance().logOut();
        Log.e("Logout","Done");

        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewProfileButton:
                navItemIndex = 10;
                CURRENT_TAG = TAG_SUPPORT;
                break;
        }
        loadHomeFragment();
    }
}
