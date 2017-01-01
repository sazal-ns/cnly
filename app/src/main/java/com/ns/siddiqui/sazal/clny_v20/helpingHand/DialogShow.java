/*
 * Copyright (c) 2016. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20.helpingHand;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.materialdialogs.MaterialDialog;
import com.ns.siddiqui.sazal.clny_v20.R;

/**
 * Created by sazal on 2016-12-27.
 */

public class DialogShow extends AppCompatActivity {
    Context context;
    String  content,title;
    Drawable icon;

    public DialogShow( Context context,String title,String content, Drawable icon) {
        this.title = title;
        this.context = context;
        this.content = content;
        this.icon = icon;
        /*this.builder = builder;
        this.dialog = dialog;*/
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .icon(icon);
        MaterialDialog dialog = builder.build();
        dialog.show();
    }
}
