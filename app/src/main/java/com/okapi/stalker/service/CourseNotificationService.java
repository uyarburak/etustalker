package com.okapi.stalker.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Interval;
import com.okapi.stalker.data.storage.model.Section;

import java.util.Calendar;
import java.util.Set;

/**
 * Created by burak on 10/14/2016.
 */
public class CourseNotificationService  extends IntentService {
    public static final String TAG = CourseNotificationService.class.getSimpleName();

    public CourseNotificationService()
    {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        clearAlarms();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        if (!sharedPreferences.contains("id")
                || !sharedPreferences.getBoolean("checkbox_notification_allow", true)) {
            return;
        }

        int counter = 0;
        String studentId = sharedPreferences.getString("id", "0");
        String xMinuteBefore = sharedPreferences.getString("edittext_notification_x_minute", "10");
        int intXMinuteBefore;
        try{
            intXMinuteBefore = Integer.parseInt(xMinuteBefore);
        }catch (NumberFormatException e){
            intXMinuteBefore = 10;
        }
        MainDataBaseHandler db = new MainDataBaseHandler(getBaseContext());

        for (Section section : db.getSectionsOfStudent(studentId)){
            section = db.getSectionWithoutStudents(section.getId());
            for (Interval interval: db.getIntervalsOfSection(section.getId())){

                Calendar calendar = Calendar.getInstance();
                calendar.setFirstDayOfWeek(Calendar.MONDAY);
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, interval.getHour()+8);
                calendar.set(Calendar.MINUTE, 30 - intXMinuteBefore);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.DAY_OF_WEEK, interval.getDay()+2);
                if(calendar.getTimeInMillis() <= System.currentTimeMillis()){
                    calendar.setTimeInMillis(calendar.getTimeInMillis() + AlarmManager.INTERVAL_DAY * 7);
                }
                Log.e(TAG, "Notification set to: " + calendar.getTime().toString());
                Log.e(TAG, "Ders bilgileri: " + section.getCourse().getCode() + " " + section.getId() + " " + section.getInstructor().getName());
                Intent alarmIntent = new Intent(this, CourseAlarmReceiver.class);
                Bundle bundle = new Bundle();
                bundle.putString("courseCode", section.getCourse().getCode());
                bundle.putString("courseTitle", section.getCourse().getTitle());
                bundle.putString("instructorName", section.getInstructor().getName());
                bundle.putInt("sectionId", section.getId());
                bundle.putInt("hour", interval.getHour());
                bundle.putString("room", interval.getRoom());
                alarmIntent.putExtras(bundle);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        this.getApplicationContext(), counter++, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY * 7, pendingIntent);
            }
        }
    }

    private void clearAlarms(){
        for(int i = 0; i < 100; i++){
            Intent alarmIntent = new Intent(this, CourseAlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    this.getApplicationContext(), i, alarmIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            alarmManager.cancel(pendingIntent);
        }
    }
}
