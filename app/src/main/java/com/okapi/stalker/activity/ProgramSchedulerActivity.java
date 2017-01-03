package com.okapi.stalker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Toast;

import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.data.storage.model.Interval;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.fragment.ProgramSchedulerFragment1;
import com.okapi.stalker.fragment.ProgramSchedulerFragment2;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;

/**
 * Created by burak on 9/30/2016.
 */
public class ProgramSchedulerActivity extends MaterialIntroActivity {
    int maxConflict;
    ProgramSchedulerFragment1 fragment1;
    ProgramSchedulerFragment2 fragment2;
    ProgramSchedulerFragment2 fragment3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        getNextButtonTranslationWrapper()
                .setEnterTranslation(new IViewTranslation() {
                    @Override
                    public void translate(View view, @FloatRange(from = 0, to = 1.0) float percentage) {
                        view.setAlpha(percentage);
                    }
                });

        addSlide(fragment1 = new ProgramSchedulerFragment1());
        addSlide(fragment2 = new ProgramSchedulerFragment2());
        addSlide(fragment3 = new ProgramSchedulerFragment2());
        addSlide(new SlideFragmentBuilder()
                        .backgroundColor(R.color.third_slide_background)
                        .buttonsColor(R.color.third_slide_buttons)
                        .title("We provide best tools")
                        .description("ever")
                        .build());
    }
    Map<List<Section>, Integer> sectionsss = new HashMap<List<Section>, Integer>();
    @Override
    public void onFinish() {
        super.onFinish();
        maxConflict = fragment1.numberPicker.getValue();
        Set<String> list1 = new HashSet<>();
        for(SearchableSpinner spinner: fragment2.spinners){
            int position = spinner.getSelectedItemPosition();
            String s = fragment2.courseList.get(position);
            s = s.substring(0, s.indexOf(" - "));
            list1.add(s);
        }
        Set<String> list2 = new HashSet<>();
        for(SearchableSpinner spinner: fragment3.spinners){
            int position = spinner.getSelectedItemPosition();
            String s = fragment3.courseList.get(position);
            s = s.substring(0, s.indexOf(" - "));
            list2.add(s);
        }
        MainDataBaseHandler db = new MainDataBaseHandler(this);

        sectionsss.clear();
        List<Course> courses = new ArrayList<Course>();
        List<String> htmlProgramList = new ArrayList<String>();

        for (String courseId : list1) {
            Course course = db.getCourseForProgram(courseId);
            courses.add(course);
        }

        recursive(courses, 0, new ArrayList<Section>());

        if(sectionsss.isEmpty()){
            Toast.makeText(this, "No way to make that program!", Toast.LENGTH_SHORT).show();

        }else{
            courses.clear();;
            for (String courseId : list2) {
                Course course = db.getCourseForProgram(courseId);
                courses.add(course);
            }
            Map<List<Section>, Integer> sectionss2 = new HashMap<List<Section>, Integer>(sectionsss);
            for (List<Section> sections : sectionss2.keySet()) {
                recursive2(courses, 0, sections);
            }

            Toast.makeText(this, "Isteklerinize uygun " + sectionsss.size() + " adet program yapilabilir.", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, ProgramTableActivity.class);
            intent.putExtra("sections", (Serializable) sectionsss);
            startActivity(intent);
        }
    }

    private Boolean recursive (List<Course> courses, int index, List<Section> sections){
        if(index == courses.size()){
            int kontrol = kontrolEt(sections);
            if(kontrol <= maxConflict){
                sectionsss.put(sections, kontrol);
                return true;
            }
            else
                return null;
        }
        Course course = courses.get(index);
        for (Section section : course.getSections()) {
            List<Section> sectionn = new ArrayList<>(sections);
            sectionn.add(section);
            recursive(courses, index+1, sectionn);
        }
        return null;
    }

    private Boolean recursive2 (List<Course> courses, int index, List<Section> sections){
        if(index == courses.size()){
            return null;
        }
        Course course = courses.get(index);
        for (Section section : course.getSections()) {
            List<Section> sectionn = new ArrayList<>(sections);
            sectionn.add(section);
            int kontrol = kontrolEt(sectionn);
            if(kontrol <= maxConflict){
                sectionsss.put(sectionn, kontrol);
                recursive2(courses, index+1, sectionn);
                recursive2(courses, index+1, sections);
            }
        }
        return null;
    }

    private int kontrolEt(List<Section> sections){
        Set<Interval> intervals = new HashSet<Interval>();
        int sayac = 0;
        for (Section section : sections) {
            sayac += section.getIntervals().size();
            intervals.addAll(section.getIntervals());
        }
        return sayac - intervals.size();

    }


}
