package com.okapi.stalker.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.okapi.stalker.R;
import com.okapi.stalker.activity.MainActivity;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Interval;
import com.okapi.stalker.data.storage.type.Section;
import com.okapi.stalker.data.storage.type.Student;

import java.util.Set;


public class ProgramFragment extends Fragment {
    View rootView;
    private String key;
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

        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_program, container, false);

            int pixels = dpToPx(getContext(), 60);

            Button[] buttons = new Button[78];
            LinearLayout linearLayout =
                    (LinearLayout)rootView.findViewById(R.id.calendarSplitterRelativeLayout);

            LinearLayout.LayoutParams prm = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.MATCH_PARENT, 2);

            RelativeLayout relativeLayout = null;
            for (int i = 0; i< buttons.length; i++){
                Button button = new Button(getActivity());


                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
                if(i % 13 != 0) {
                    params.addRule(RelativeLayout.BELOW, i);
                }else{
                    relativeLayout = new RelativeLayout(getActivity());
                    relativeLayout.setLayoutParams(prm);
                    linearLayout.addView(relativeLayout);
                }

                button.setLayoutParams(params);
                button.setTextSize(10);
                button.setHeight(pixels);
                button.setText("-");
                button.setId(i + 1);
                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO
                    }
                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    button.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                }

                relativeLayout.addView(button);
                buttons[i] = button;
            }

            // Intervals are coming...
            Student student = stash.getStudent(key);
            Set<String> sectionKeys = student.sectionKeys;
            for (String sectionKey: sectionKeys){
                Section section = stash.getSection(sectionKey);
                Set<String> intervalKeys = section.getIntervalKeys();
                for (String intervalKey: intervalKeys){
                    Interval interval = stash.getInterval(intervalKey);
                    int indeks = (interval.day.ordinal() * 13) + interval.time.ordinal();
                    buttons[indeks].setText(section.course + " - " + interval.classRoom.name);
                }
            }


            rootView.findViewById(R.id.refreshLinearLayout).setVisibility(View.INVISIBLE);
            rootView.findViewById(R.id.refreshView).setVisibility(View.INVISIBLE);
            if(getActivity() instanceof MainActivity)
                ((ObservableScrollView)(rootView.findViewById(R.id.calendarScrollView))).setScrollViewCallbacks((ObservableScrollViewCallbacks)getActivity());
        }
        // Time line initializing
        Time time = new Time();
        time.setToNow();
        int minutes = (time.hour * 60) + time.minute - 510;
        LinearLayout currentTimeLine = (LinearLayout)rootView.findViewById(R.id.currentTimeMarkerLinearLayout);
        RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, dpToPx(getActivity(), minutes), 0, 0);
        currentTimeLine.setLayoutParams(params);
        return rootView;
    }


    private int dpToPx(Context context, int dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    public void setKey(String key){
        this.key = key;
    }
}
