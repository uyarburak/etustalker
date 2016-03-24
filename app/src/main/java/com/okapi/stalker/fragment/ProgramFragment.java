package com.okapi.stalker.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.DataBaseHandler;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Instructor;
import com.okapi.stalker.data.storage.type.Interval;
import com.okapi.stalker.data.storage.type.Section;
import com.okapi.stalker.data.storage.type.Student;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;


public class ProgramFragment extends Fragment{

    private View rootView;
    private Set<String> keys;

    public ProgramFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Stash stash = Stash.get();

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_program, container, false);

            int pixels = dpToPx(getContext(), 50);
            int pixels10 = dpToPx(getContext(), 10);
            int pixels2 = dpToPx(getContext(), 2);

            TextView[] buttons = new TextView[78];
            LinearLayout linearLayout =
                    (LinearLayout) rootView.findViewById(R.id.calendarSplitterRelativeLayout);

            LinearLayout.LayoutParams prm = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 2);
            prm.setMargins(pixels2, 0, 0, 0);

            RelativeLayout relativeLayout = null;
            for (int i = 0; i < buttons.length; i++) {
                final int index = i;
                TextView button = new TextView(getActivity());


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, 0, pixels10);
                if (i % 13 != 0) {
                    params.addRule(RelativeLayout.BELOW, i);
                } else {
                    relativeLayout = new RelativeLayout(getActivity());
                    relativeLayout.setLayoutParams(prm);
                    linearLayout.addView(relativeLayout);
                }

                button.setLayoutParams(params);
                button.setHeight(pixels);
                button.setGravity(Gravity.CENTER);
                button.setBackgroundColor(Color.parseColor("#ececec"));
                button.setId(i + 1);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        freeFriendsDialog(index);
                    }
                });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }

                relativeLayout.addView(button);
                buttons[i] = button;
            }

            // Intervals are coming...

            String[] colors = {"#63b526", "#009AE3", "#f584d4", "#ff7800",
                    "#f74448", "#c3903f", "#a5de5b", "black"};
            int colorIndex = 0;
            for (String sectionKey : keys) {
                final Section section = stash.getSection(sectionKey);
                Set<String> intervalKeys = section.getIntervalKeys();
                if(intervalKeys.isEmpty())
                    continue;
                int color = Color.parseColor(colors[colorIndex % colors.length]);
                for (String intervalKey : intervalKeys) {
                    Interval interval = stash.getInterval(intervalKey);
                    int indeks = (interval.day.ordinal() * 13) + interval.time.ordinal();
                    buttons[indeks].setText(section.course + " (" + interval.classRoom.name + ")");
                    buttons[indeks].setBackgroundColor(color);
                    buttons[indeks].setTextSize(13);
                    buttons[indeks].setTextColor(Color.WHITE);
                    buttons[indeks].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getActivity(), SectionActivity.class);
                            intent.putExtra("section", section);
                            getActivity().startActivity(intent);
                        }
                    });
                }
                colorIndex++;
            }


            rootView.findViewById(R.id.refreshLinearLayout).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.refreshView).setVisibility(View.INVISIBLE);


            final LinearLayout currentTimeLine =
                    (LinearLayout) rootView.findViewById(R.id.currentTimeMarkerLinearLayout);

            final RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            Timer timer = new Timer();
            TimerTask updateClock = new TimerTask() {
                @Override
                public void run() {
                    if(getActivity() != null){
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // Time line initializing
                                Time time = new Time();
                                time.setToNow();
                                if(time.hour < 21){
                                    currentTimeLine.setVisibility(View.VISIBLE);
                                    int minutes = (time.hour * 60) + time.minute - 510;
                                    System.out.println(time.second);
                                    params.setMargins(0, dpToPx(getActivity(), minutes), 0, 0);
                                    currentTimeLine.setLayoutParams(params);
                                }else{
                                    currentTimeLine.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
                    }

                }
            };
            timer.scheduleAtFixedRate(updateClock, 0, 60000);
        }

        return rootView;
    }


    private int dpToPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private void freeFriendsDialog(int index){
        final HashMap<String, Student> freeGuys = new HashMap<>();
        Stash stash = Stash.get();
        DataBaseHandler db = new DataBaseHandler(getActivity());
        List<String> friends = db.getAllFriends();
        etiket:
        for (String key: friends){
            Student friend = stash.getStudent(key);
            Set<String> sectionKeys = friend.sectionKeys;
            for(String sectionKey: sectionKeys){
                Section section = stash.getSection(sectionKey);
                Set<String> intervalKeys = section.getIntervalKeys();
                for (String intervalKey: intervalKeys){
                    Interval interval = stash.getInterval(intervalKey);
                    int indeks = (interval.day.ordinal() * 13) + interval.time.ordinal();
                    if(indeks == index)
                        continue etiket;
                }
            }
            freeGuys.put(friend.name, friend);
        }

        //Create sequence of items
        final CharSequence[] freeGuyNames = freeGuys.keySet().toArray(new String[freeGuys.size()]);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setTitle("Free Friends");
        dialogBuilder.setItems(freeGuyNames, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                Student friend = freeGuys.get(freeGuyNames[item].toString());  //Selected item in listview
                Intent intent = new Intent(getActivity(), StudentActivity.class);
                intent.putExtra("student", (Serializable) friend);
                getActivity().startActivity(intent);
            }
        });
        //Create alert dialog object via builder
        AlertDialog alertDialogObject = dialogBuilder.create();
        //Show the dialog
        alertDialogObject.show();
    }
    public void setSectionKeys(Set<String> keys) {
        this.keys = keys;
    }
}
