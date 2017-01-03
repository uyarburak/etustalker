package com.okapi.stalker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Course;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import agency.tango.materialintroscreen.SlideFragment;

/**
 * Created by burak on 9/30/2016.
 */
public class ProgramSchedulerFragment2 extends SlideFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {
    RelativeLayout relativeLayout;
    public List<SearchableSpinner> spinners;
    public ArrayList<String> courseList;
    int counter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scheduler_2, container, false);
        counter = 0;
        spinners = new ArrayList<SearchableSpinner>();
        relativeLayout = (RelativeLayout) view.findViewById(R.id.relaaa);
        MainDataBaseHandler db = new MainDataBaseHandler(getActivity());
        Set<Course> courses = db.getAllCourses();
        courseList = new ArrayList<>(courses.size()+1);
        courseList.add("Choose a course");
        for (Course course: courses){
            courseList.add(course.getCode().concat(" - ").concat(course.getTitle()));
        }
        onClick(null);
        FloatingActionButton button = (FloatingActionButton) view.findViewById(R.id.removeLast);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter > 1) {
                    relativeLayout.removeView(relativeLayout.findViewById(counter--));
                    spinners.remove(spinners.size()-1);
                }
            }
        });
        FloatingActionButton button2 = (FloatingActionButton) view.findViewById(R.id.addCourse);
        button2.setOnClickListener(this);
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.custom_slide_background;
    }

    @Override
    public int buttonsColor() {
        return R.color.custom_slide_buttons;
    }

    @Override
    public boolean canMoveFurther() {
        for(Spinner spinner: spinners){
            if(spinner.getSelectedItemPosition() == 0)
                return false;
        }
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "Dont leave any empty course";
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        System.out.println(parent.getId() + " ? " + counter);
        if(position != 0 && parent.getId() == counter)
            onClick(null);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        SearchableSpinner spinner = new SearchableSpinner(getContext());
        spinner.setTitle("Choose a course");
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, courseList);
        spinner.setAdapter(spinnerArrayAdapter);
        final RelativeLayout.LayoutParams params =
                new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, counter);
        spinner.setLayoutParams(params);
        spinner.setId(++counter);
        spinner.setOnItemSelectedListener(this);
        relativeLayout.addView(spinner);
        spinners.add(spinner);
    }
}
