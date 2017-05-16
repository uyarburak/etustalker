package com.okapi.stalker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.ProgramSchedulerActivity;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Course;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import agency.tango.materialintroscreen.SlideFragment;

/**
 * Created by burak on 9/30/2016.
 */
public class ProgramSchedulerFragment2 extends SlideFragment {
    public ListView listView;
    public ArrayList<String> courseList;
    ArrayList<String> courseList2;
    int id;
    int lastIndex;

    public SlideFragment setId(int val){
        this.id = val;
        lastIndex = 0;
        return this;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scheduler_2, container, false);

        TextView descriptionView = (TextView) view.findViewById(R.id.textView7);
        if(id == 0){
            descriptionView.setText(getString(R.string.program_scheduler_have_to));
        }else{
            descriptionView.setText(getString(R.string.program_scheduler_not_have_to));
        }
        MainDataBaseHandler db = new MainDataBaseHandler(getActivity());
        Set<Course> courses = db.getAllCourses();
        courseList = new ArrayList<>(courses.size());
        courseList2 = new ArrayList<>(courses.size());
        for (Course course: courses){
            courseList.add(course.getCode());
            courseList2.add(course.getCode().concat(" - ").concat(course.getTitle()));
        }
        listView = (ListView) view.findViewById(R.id.multiselect_list);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setAdapter(new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_list_item_multiple_choice, courseList2));
        EditText searchText = (EditText) view.findViewById(R.id.program_course_search);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int i = 0;
                String searchKey = clearTurkishChars(s.toString().toUpperCase(new Locale("tr", "TR")));
                for(String courseName : courseList){
                    if(clearTurkishChars(courseName).startsWith(searchKey)){
                        listView.setSelection(i);
                        lastIndex = i;
                        return;
                    }
                    i++;
                }
                listView.setSelection(lastIndex);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return view;
    }
    private String clearTurkishChars(String word){
        return word.replace('Ğ', 'G').replace('Ü', 'U').replace('Ş', 'S').replace('İ', 'I').replace('Ö', 'O').replace('Ç', 'C').replaceAll("\\s+", "");
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
        if(id==1){ //secmelileri sectigin yer ise
            ProgramSchedulerActivity activity = (ProgramSchedulerActivity)getActivity();
            return activity.fragment2.listView.getCheckedItemCount() > 0 || listView.getCheckedItemCount() > 0;
        }
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.program_scheduler_choose_at_least);
    }
}
