package com.ns.siddiqui.sazal.clny_v20;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.ns.siddiqui.sazal.clny_v20.helpingHand.ConnectionDetector;

public class Loading extends AppCompatActivity {

    ProgressBar loading;
    TextView textView;
    private int progress =20;
    Typeface typeface;

    ConnectionDetector cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        textView = (TextView) findViewById(R.id.loadingTextView);
        typeface = Typeface.createFromAsset(getAssets(), "fonts/Roboto.ttf");
        textView.setTypeface(typeface);

        loading = (ProgressBar) findViewById(R.id.loading);
        loading.getBackground().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        loading.getProgressDrawable().setColorFilter(Color.parseColor("#6dc390"), PorterDuff.Mode.SRC_IN);

        cd = new ConnectionDetector(this);

        init();
    }

    public void init(){
        if (cd.isConnected()) {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (progress < 100) {
                        loading.setProgress(progress += 2);

                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    if (progress>=100) {
                        Intent intent = new Intent(Loading.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
            }).start();

        }else{
            showNetDisabledAlertToUser(this);
        }
    }

    public void showNetDisabledAlertToUser(final Context context){

         Drawable error_icon = getResources().getDrawable(R.drawable.error_icon);

        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.error)
                .content(R.string.noNetMess)
                .positiveText("Enable")
                .positiveColor(Color.parseColor("#6dc390"))
                .negativeText("Exit")
                .negativeColor(Color.RED)
                .neutralText("Try Again")
                .neutralColor(Color.BLUE)
                .icon(error_icon)
                .typeface(typeface,Typeface.defaultFromStyle(Typeface.BOLD))
                .cancelable(false)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                        System.exit(0);
                    }
                })
                .onNeutral(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                })
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog materialDialog, @NonNull DialogAction dialogAction) {
                        Intent dialogIntent = new Intent(android.provider.Settings.ACTION_SETTINGS);
                        context.startActivity(dialogIntent);
                    }
                });

        MaterialDialog dialog = builder.build();
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        this.finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //this.finish();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
       // this.init();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        this.init();
    }
}
