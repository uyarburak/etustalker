package com.okapi.stalker.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.util.NotificationUtils;

import java.text.SimpleDateFormat;

/**
 * Created by burak on 10/14/2016.
 */
public class CourseAlarmReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String courseCode = intent.getStringExtra("courseCode");
        String courseTitle = intent.getStringExtra("courseTitle");
        String instructorName = intent.getStringExtra("instructorName");
        String room = intent.getStringExtra("room");
        int sectionId = intent.getIntExtra("sectionId", 0);
        int hour = intent.getIntExtra("hour", 0) + 8;
        NotificationUtils notificationUtils = new NotificationUtils(context);
        Intent sectionActivityIntent = new Intent(context, SectionActivity.class);
        sectionActivityIntent.putExtra("section", sectionId);
        sectionActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationUtils.showNotificationMessage(courseCode + " - " + courseTitle
                , "Room: " + room + " - Time: " + hour + ":30 (" + instructorName + ")"
                , new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date())
                , sectionActivityIntent);
    }
}