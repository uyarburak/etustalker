package com.okapi.stalker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by burak on 10/14/2016.
 */
public class BootAndTimeSetReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        context.startService(new Intent(context,CourseNotificationService.class));
    }
}
