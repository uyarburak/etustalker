package com.okapi.stalker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.okapi.stalker.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by burak on 10/19/2016.
 */
public class FreeRoomsActivity  extends AppCompatActivity {
    Elements rows;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_rooms);

        setTitle(getString(R.string.free_rooms_title));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        new FreeRoomParse(this).execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private void parseDone(Elements rows){
        this.rows = rows;

        int pixels = dpToPx(50);
        int pixels10 = dpToPx(10);
        int pixels2 = dpToPx(2);
        {
            LinearLayout linearLayout =
                    (LinearLayout) findViewById(R.id.calendarSplitterRelativeLayout);

            LinearLayout.LayoutParams prm = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 2);
            prm.setMargins(pixels2, 0, 0, 0);

            RelativeLayout relativeLayout = null;
            for (int i = 0; i < 60; i++) {
                final TextView button = new TextView(this);

                final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, pixels10);
                if (i % 10 != 0) {
                    params.addRule(RelativeLayout.BELOW, i);
                } else {
                    relativeLayout = new RelativeLayout(this);
                    relativeLayout.setLayoutParams(prm);
                    linearLayout.addView(relativeLayout);
                }

                button.setLayoutParams(params);
                button.setHeight(pixels);
                button.setGravity(Gravity.CENTER);
                button.setId(i + 1);
                button.setText(getString(R.string.click_to_see));
                button.setBackgroundColor(Color.parseColor("#ececec"));
                //button.setBackgroundColor(Color.parseColor(myButton.color));
                final int index = i;
                button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            freeRoomsDialog(index);
                            button.setBackgroundColor(Color.BLACK);
                            button.setTextColor(Color.YELLOW);
                        }
                });
                //button.setId(i + 1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }
                button.bringToFront();
                relativeLayout.addView(button);
            }
        }

        findViewById(R.id.refreshLinearLayout).setVisibility(View.INVISIBLE);
        findViewById(R.id.refreshView).setVisibility(View.INVISIBLE);
        findViewById(R.id.text1830).setVisibility(View.GONE);
        findViewById(R.id.text1930).setVisibility(View.GONE);
        findViewById(R.id.text2030).setVisibility(View.GONE);


        final LinearLayout currentTimeLine =
                (LinearLayout) findViewById(R.id.currentTimeMarkerLinearLayout);


        final RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        int[] days = {
                R.id.mondayTextView,
                R.id.tuesdayTextView,
                R.id.wednesdayTextView,
                R.id.thursdayTextView,
                R.id.fridayTextView,
                R.id.saturdayTextView,
        };
        Time time = new Time();
        time.setToNow();

        if(time.weekDay != 0){
            TextView textView = (TextView) findViewById(days[time.weekDay-1]);
            textView.setTextColor(Color.parseColor("#FF4081"));
        }
        Timer timer = new Timer();
        TimerTask updateClock = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Time line initializing
                        Time time = new Time();
                        time.setToNow();
                        if(time.hour < 18){
                            currentTimeLine.setVisibility(View.VISIBLE);
                            int minutes = (time.hour * 60) + time.minute - 510;

                            params.setMargins(0, dpToPx(minutes), 0, 0);
                            currentTimeLine.setLayoutParams(params);
                        }else{
                            currentTimeLine.setVisibility(View.INVISIBLE);
                        }
                    }
                });

            }
        };
        timer.scheduleAtFixedRate(updateClock, 0, 60000);
    }
    private int dpToPx(int dp) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private void freeRoomsDialog(int index){

        int day = index / 10;
        int hour = index % 10;
        Element row = rows.get(hour + 1);
        Elements cols = row.select("td");
        String html = cols.get(day).toString();
        html = html.substring(4, html.length() - 5);

        //Create sequence of items
        final CharSequence[] freeRooms = html.split("<br>");
        android.app.AlertDialog.Builder dialogBuilder = new android.app.AlertDialog.Builder(this);
        dialogBuilder.setTitle(getString(R.string.free_rooms_title));
        dialogBuilder.setItems(freeRooms, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                String roomName = freeRooms[item].toString();
            }
        });
        //Create alert dialog object via builder
        android.app.AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }
    private class FreeRoomParse extends AsyncTask<String, Void, Integer> {
        private ProgressDialog dialog;
        private Activity activity;
        private SharedPreferences sharedPreferences;
        private String html;

        private Elements rows;
        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            if(rows != null){
                parseDone(rows);
                dialog.dismiss();
                return;
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            AlertDialog alertDialog = new AlertDialog.Builder(FreeRoomsActivity.this).create();
            alertDialog.setTitle(getString(R.string.alert));
            if(sharedPreferences.contains("time")){
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                Date date = new Date(sharedPreferences.getLong("time", 0));
                alertDialog.setMessage(getString(R.string.connection_error, format.format(date).toString()));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                new FreeRoomParse(activity, sharedPreferences.getString("html", "")).execute();
                            }
                        });
            }else{

                alertDialog.setMessage(getString(R.string.connection_error_no_cache));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                activity.finish();
                            }
                        });
            }

            alertDialog.show();
        }

        public FreeRoomParse(Activity activity) {
            super();
            this.activity = activity;
            dialog = new ProgressDialog(activity);
            sharedPreferences = getSharedPreferences("free_rooms_cache", MODE_PRIVATE);
        }
        public FreeRoomParse(Activity activity, String html) {
            this(activity);
            this.html = html;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getString(R.string.executing));
            dialog.show();
        }

        private void updateCache(String html){
            SharedPreferences.Editor mPrefsEditor = sharedPreferences.edit();
            mPrefsEditor.putString("html", html);
            mPrefsEditor.putLong("time", System.currentTimeMillis());
            mPrefsEditor.commit();
        }
        @Override
        protected Integer doInBackground(String... params) {
            Document doc;
            if(html == null){
                try {
                    doc  = Jsoup.connect("http://kayit.etu.edu.tr/Ders/Ders_prg.php")
                            .data("btn_bosderslik", "Boş Derslikleri Göster")
                            .post();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
                doc = new Cleaner(Whitelist.relaxed()).clean(doc);
                updateCache(doc.toString());
            }else{
                doc = Jsoup.parse(html);
            }
            Element table = doc.select("table").get(0); //select the first table.
            rows = table.select("tr");

            return null;
        }
    }
}