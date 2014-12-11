package com.romantymchyk.omguwreader.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.romantymchyk.omguwreader.utilities.ChangeLog;
import com.romantymchyk.omguwreader.utilities.PostCheckerUtilities;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ChangeLog changeLog = new ChangeLog(context);
        if (!changeLog.firstRun()) {
            PostCheckerUtilities.startPostCheckerAlarm(context);
        }
    }
    
}