/*
 * Copyright (c) 2016. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ns.siddiqui.sazal.clny_v20.helpingHand.DialogShow;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.DownLoadImageTask;
import com.ns.siddiqui.sazal.clny_v20.model.User;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        navLoad();
    }

    private DrawerLayout drawer;
    private void navLoad(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = (DrawerLayout) findViewById(R.id.profile_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.profile_nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header= navigationView.getHeaderView(0);
        NavigationView footerView = (NavigationView) findViewById(R.id.profile_navigation_drawer_bottom);
        footerView.setNavigationItemSelectedListener(this);

        Button viewProfile = (Button) header.findViewById(R.id.viewProfileButton);
        viewProfile.setOnClickListener(this);
        CircleImageView pic = (CircleImageView) header.findViewById(R.id.pic);
        TextView nameTextView = (TextView) header.findViewById(R.id.nameTextView);

        nameTextView.setText(User.getUserName());
        if (User.getImageLink()!=null){
            // pic.setImageURI(Uri.parse(picUrl+ User.getImageLink()));
            String picUrl = "http://app.clynpro.com/image/";
            new DownLoadImageTask(pic).execute(picUrl +User.getImageLink());
        }

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            finish();
            Intent intent = new Intent(this, MainActivity.class);
            //drawer.closeDrawers();
            startActivity(intent);
        } else if (id == R.id.nav_calender) {

        } else if (id == R.id.nav_gift) {

        } else if (id == R.id.nav_payments) {

        } else if (id == R.id.nav_support) {
            Intent intent = new Intent(this, SupporMainActivity.class);
            //drawer.closeDrawers();
            startActivity(intent);
        } else if (id == R.id.nav_clean) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.viewProfileButton:
                new DialogShow(ProfileActivity.this,"Where am I?","You are at Your Profile",getResources().getDrawable(R.drawable.clyn));
                break;

        }
    }
}
