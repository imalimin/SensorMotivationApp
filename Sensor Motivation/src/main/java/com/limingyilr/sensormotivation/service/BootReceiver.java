package com.limingyilr.sensormotivation.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by lanqx on 2014/4/26.
 */
public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent newIntent = new Intent(context,SensorMotivationService.class);
            context.startService(newIntent);
        }
    }

}