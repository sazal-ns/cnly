/*
 * Copyright (c) 2016. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.github.aakira.expandablelayout.ExpandableWeightLayout;

public class SupporMainActivity extends AppCompatActivity implements View.OnClickListener {

    public static void startActivity(Context context) {
        context.startActivity(new Intent(context, SupporMainActivity.class));
    }

    private Button about, legal;
    private ExpandableWeightLayout mExpandLayout, expandableWeightLayoutLeagal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppor_main);

        about = (Button) findViewById(R.id.aboutButton);
        mExpandLayout = (ExpandableWeightLayout) findViewById(R.id.expandableLayout);
        about.setOnClickListener(this);

        legal = (Button) findViewById(R.id.LegalButton);
        expandableWeightLayoutLeagal = (ExpandableWeightLayout) findViewById(R.id.expandableLayoutLegal);
        legal.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.aboutButton:
                if (expandableWeightLayoutLeagal.isExpanded()) expandableWeightLayoutLeagal.toggle();
                mExpandLayout.toggle();
                break;
            case R.id.LegalButton:
                if (mExpandLayout.isExpanded()) mExpandLayout.toggle();
                expandableWeightLayoutLeagal.toggle();
                break;
        }
    }
}
