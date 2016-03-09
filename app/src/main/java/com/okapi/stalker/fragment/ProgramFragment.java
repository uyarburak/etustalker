package com.okapi.stalker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.okapi.stalker.R;
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
            Student student = stash.getStudent(key);
            System.out.println(key);
            System.out.println(student);
            final Set<String> sectionKeys = student.sectionKeys;
            for(String section_key: sectionKeys){
                System.out.println(section_key);
                Section section = stash.getSection(section_key);
                final Set<String> intervalKeys = section.getIntervalKeys();
                for(String interval_key: intervalKeys){
                    System.out.println(interval_key);
                    Interval interval = stash.getInterval(interval_key);
                    Button button = (Button)rootView.findViewById(getId(interval.day, interval.time));
                    button.setText(section.course + " - " + interval.classRoom.name);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                        }
                    });
                }

            }
        }
        return rootView;
    }
    int getId(Interval.Day day, Interval.Time time){
        int sonuc = 0;
        if(day == Interval.Day.MONDAY){
            if(time == Interval.Time.ONE){
                sonuc = R.id.MONDAY0;
            }else if(time == Interval.Time.TWO){
                sonuc = R.id.MONDAY1;
            }else if(time == Interval.Time.THREE){
                sonuc = R.id.MONDAY2;
            }else if(time == Interval.Time.FOUR){
                sonuc = R.id.MONDAY3;
            }else if(time == Interval.Time.FIVE){
                sonuc = R.id.MONDAY4;
            }else if(time == Interval.Time.SIX){
                sonuc = R.id.MONDAY5;
            }else if(time == Interval.Time.SEVEN){
                sonuc = R.id.MONDAY6;
            }else if(time == Interval.Time.EIGHT){
                sonuc = R.id.MONDAY7;
            }else if(time == Interval.Time.NINE){
                sonuc = R.id.MONDAY8;
            }else if(time == Interval.Time.TEN){
                sonuc = R.id.MONDAY9;
            }else if(time == Interval.Time.ELEVEN){
                sonuc = R.id.MONDAY10;
            }else{
                sonuc = R.id.MONDAY11;
            }
        }else if(day == Interval.Day.TUESDAY){
            if(time == Interval.Time.ONE){
                sonuc = R.id.TUESDAY0;
            }else if(time == Interval.Time.TWO){
                sonuc = R.id.TUESDAY1;
            }else if(time == Interval.Time.THREE){
                sonuc = R.id.TUESDAY2;
            }else if(time == Interval.Time.FOUR){
                sonuc = R.id.TUESDAY3;
            }else if(time == Interval.Time.FIVE){
                sonuc = R.id.TUESDAY4;
            }else if(time == Interval.Time.SIX){
                sonuc = R.id.TUESDAY5;
            }else if(time == Interval.Time.SEVEN){
                sonuc = R.id.TUESDAY6;
            }else if(time == Interval.Time.EIGHT){
                sonuc = R.id.TUESDAY7;
            }else if(time == Interval.Time.NINE){
                sonuc = R.id.TUESDAY8;
            }else if(time == Interval.Time.TEN){
                sonuc = R.id.TUESDAY9;
            }else if(time == Interval.Time.ELEVEN){
                sonuc = R.id.TUESDAY10;
            }else{
                sonuc = R.id.TUESDAY11;
            }
        }else if(day == Interval.Day.WEDNESDAY){
            if(time == Interval.Time.ONE){
                sonuc = R.id.WEDNESDAY0;
            }else if(time == Interval.Time.TWO){
                sonuc = R.id.WEDNESDAY1;
            }else if(time == Interval.Time.THREE){
                sonuc = R.id.WEDNESDAY2;
            }else if(time == Interval.Time.FOUR){
                sonuc = R.id.WEDNESDAY3;
            }else if(time == Interval.Time.FIVE){
                sonuc = R.id.WEDNESDAY4;
            }else if(time == Interval.Time.SIX){
                sonuc = R.id.WEDNESDAY5;
            }else if(time == Interval.Time.SEVEN){
                sonuc = R.id.WEDNESDAY6;
            }else if(time == Interval.Time.EIGHT){
                sonuc = R.id.WEDNESDAY7;
            }else if(time == Interval.Time.NINE){
                sonuc = R.id.WEDNESDAY8;
            }else if(time == Interval.Time.TEN){
                sonuc = R.id.WEDNESDAY9;
            }else if(time == Interval.Time.ELEVEN){
                sonuc = R.id.WEDNESDAY10;
            }else{
                sonuc = R.id.WEDNESDAY11;
            }
        }else if(day == Interval.Day.THURSDAY){
            if(time == Interval.Time.ONE){
                sonuc = R.id.THURSDAY0;
            }else if(time == Interval.Time.TWO){
                sonuc = R.id.THURSDAY1;
            }else if(time == Interval.Time.THREE){
                sonuc = R.id.THURSDAY2;
            }else if(time == Interval.Time.FOUR){
                sonuc = R.id.THURSDAY3;
            }else if(time == Interval.Time.FIVE){
                sonuc = R.id.THURSDAY4;
            }else if(time == Interval.Time.SIX){
                sonuc = R.id.THURSDAY5;
            }else if(time == Interval.Time.SEVEN){
                sonuc = R.id.THURSDAY6;
            }else if(time == Interval.Time.EIGHT){
                sonuc = R.id.THURSDAY7;
            }else if(time == Interval.Time.NINE){
                sonuc = R.id.THURSDAY8;
            }else if(time == Interval.Time.TEN){
                sonuc = R.id.THURSDAY9;
            }else if(time == Interval.Time.ELEVEN){
                sonuc = R.id.THURSDAY10;
            }else{
                sonuc = R.id.THURSDAY11;
            }
        }else if(day == Interval.Day.FRIDAY){
            if(time == Interval.Time.ONE){
                sonuc = R.id.FRIDAY0;
            }else if(time == Interval.Time.TWO){
                sonuc = R.id.FRIDAY1;
            }else if(time == Interval.Time.THREE){
                sonuc = R.id.FRIDAY2;
            }else if(time == Interval.Time.FOUR){
                sonuc = R.id.FRIDAY3;
            }else if(time == Interval.Time.FIVE){
                sonuc = R.id.FRIDAY4;
            }else if(time == Interval.Time.SIX){
                sonuc = R.id.FRIDAY5;
            }else if(time == Interval.Time.SEVEN){
                sonuc = R.id.FRIDAY6;
            }else if(time == Interval.Time.EIGHT){
                sonuc = R.id.FRIDAY7;
            }else if(time == Interval.Time.NINE){
                sonuc = R.id.FRIDAY8;
            }else if(time == Interval.Time.TEN){
                sonuc = R.id.FRIDAY9;
            }else if(time == Interval.Time.ELEVEN){
                sonuc = R.id.FRIDAY10;
            }else{
                sonuc = R.id.FRIDAY11;
            }
        }else{
            if(time == Interval.Time.ONE){
                sonuc = R.id.SATURDAY0;
            }else if(time == Interval.Time.TWO){
                sonuc = R.id.SATURDAY1;
            }else if(time == Interval.Time.THREE){
                sonuc = R.id.SATURDAY2;
            }else if(time == Interval.Time.FOUR){
                sonuc = R.id.SATURDAY3;
            }else if(time == Interval.Time.FIVE){
                sonuc = R.id.SATURDAY4;
            }else if(time == Interval.Time.SIX){
                sonuc = R.id.SATURDAY5;
            }else if(time == Interval.Time.SEVEN){
                sonuc = R.id.SATURDAY6;
            }else if(time == Interval.Time.EIGHT){
                sonuc = R.id.SATURDAY7;
            }else if(time == Interval.Time.NINE){
                sonuc = R.id.SATURDAY8;
            }else if(time == Interval.Time.TEN){
                sonuc = R.id.SATURDAY9;
            }else if(time == Interval.Time.ELEVEN){
                sonuc = R.id.SATURDAY10;
            }else{
                sonuc = R.id.SATURDAY11;
            }
        }
        return sonuc;
    }
    public void setKey(String key){
        this.key = key;
    }
}
