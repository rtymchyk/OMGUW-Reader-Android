package com.romantymchyk.omguwreader.receivers;

import com.romantymchyk.omguwreader.services.PostCheckerService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Boolean postCheckedEnabled = settings.getBoolean("pref_post_checker", true);

        if (postCheckedEnabled) {
            Intent postCheckerService = new Intent(context, PostCheckerService.class);
            context.startService(postCheckerService);
        }
    }

}