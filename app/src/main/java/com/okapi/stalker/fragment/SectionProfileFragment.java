package com.okapi.stalker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.DataBaseHandler;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Course;
import com.okapi.stalker.data.storage.type.Section;
import com.okapi.stalker.data.storage.type.Student;

import java.util.List;


public class SectionProfileFragment
        extends Fragment {
    private Section section;
    private Course course;
    public SectionProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_section_profile, container, false);
        //ImageView image = (ImageView) rootView.findViewById(R.id.imageView2);
        //image.setImageResource(R.drawable.app_icon);
        //TextView courseName = (TextView) rootView.findViewById(R.id.section_course_name);
        //courseName.setText(course.title);
        TextView sectionCode = (TextView) rootView.findViewById(R.id.section_code);
        sectionCode.setText(section.course + " - " + section.number);
        TextView lecturer = (TextView) rootView.findViewById(R.id.section_lecturer_name);
        lecturer.setText(section.instructor);
        TextView size = (TextView) rootView.findViewById(R.id.section_size);
        size.setText(section.size+"");

        return rootView;
    }
    public void setCourse(Section section){
        this.section = section;
    }
}
