package com.okapi.stalker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.InstructorActivity;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.data.storage.model.Section;

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
        TextView courseName = (TextView) rootView.findViewById(R.id.section_course_name);
        courseName.setText(section.getCourse().getTitle());
        TextView sectionCode = (TextView) rootView.findViewById(R.id.section_code);
        sectionCode.setText(section.getCourse().getCode() + " - " + section.getSectionNo());

        TextView lecturer = (TextView) rootView.findViewById(R.id.section_lecturer_name);
        lecturer.setText(section.getInstructor().getName());

        TextView size = (TextView) rootView.findViewById(R.id.section_size);
        size.setText(section.getSize().toString());

        lecturer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), InstructorActivity.class);
                intent.putExtra("instructor", section.getInstructor().getId());
                getActivity().startActivity(intent);
            }
        });

        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.section = (Section)args.getSerializable("section");
    }
}
