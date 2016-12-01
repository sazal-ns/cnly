/*
 * Copyright (c) 2016.
 */

package com.ns.siddiqui.sazal.clny_v20.helpingHand;

import android.content.Context;
import android.net.ConnectivityManager;

/**
 * Created by sazal on 2016-11-26.
 */

public class ConnectionDetector {

    private Context context;

    public ConnectionDetector(Context context){
        this.context = context;
    }

    public boolean isConnected(){

        return ((ConnectivityManager) this.context.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo() != null;
    }
}
