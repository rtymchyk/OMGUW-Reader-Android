package com.romantymchyk.omguwreader.utilities;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.romantymchyk.omguwreader.receivers.AlarmReceiver;
import com.romantymchyk.omguwreader.resources.BloggerHelper;

public class PostCheckerUtilities {
	
	public static final String notificationContent = "Touch to open OMGUW Reader";
	
	public static final String notificationSubContent = "Turn these updates off in settings";
	
	public static SharedPreferences getMostRecentlyReadDates(Context context) {
		return context.getSharedPreferences(BloggerHelper.MOST_RECENTLY_READ_PREF_FILE, Context.MODE_PRIVATE);
	}
	
	public static void startPostCheckerAlarm(Context context) {
		Intent myAlarm = new Intent(context.getApplicationContext(), AlarmReceiver.class);
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(context.getApplicationContext(), 0, myAlarm, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Calendar updateTime = Calendar.getInstance();
		alarms.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY / 3, recurringAlarm);
	}
	
	public static void stopPostCheckerAlarm(Context context) {
		Intent myAlarm = new Intent(context.getApplicationContext(), AlarmReceiver.class);
		PendingIntent recurringAlarm = PendingIntent.getBroadcast(context.getApplicationContext(), 0, myAlarm, 0);
		AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarms.cancel(recurringAlarm);
	}
	
}
