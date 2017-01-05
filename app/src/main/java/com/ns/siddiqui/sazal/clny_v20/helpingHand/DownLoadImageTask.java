/*
 * Copyright (c) 2016. By Noor Nabiul Alam Siddiqui
 */

package com.ns.siddiqui.sazal.clny_v20.helpingHand;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.ns.siddiqui.sazal.clny_v20.R;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by sazal on 2016-12-27.
 */

public class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
    ImageView imageView;
 public static   Bitmap logo = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_account_circle_black_24dp);

    public static Bitmap getLogo() {
        return logo;
    }

    public DownLoadImageTask(ImageView imageView){
        this.imageView = imageView;
    }

    /*
        doInBackground(Params... params)
            Override this method to perform a computation on a background thread.
     */
    protected Bitmap doInBackground(String...urls){
        String urlOfImage = urls[0];
        logo = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.ic_account_circle_black_24dp);
        try{
            InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
            logo = BitmapFactory.decodeStream(is);
        }catch(Exception e){ // Catch the download exception
            e.printStackTrace();
        }
        return logo;
    }

    /*
        onPostExecute(Result result)
            Runs on the UI thread after doInBackground(Params...).
     */
    protected void onPostExecute(Bitmap result){
        imageView.setImageBitmap(result);
    }
}