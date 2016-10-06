package com.okapi.stalker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.CourseActivity;
import com.okapi.stalker.activity.InstructorActivity;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.data.storage.model.Interval;
import com.okapi.stalker.data.storage.model.Section;

import java.util.Iterator;

public class SectionProfileFragment
        extends Fragment {
    private Section section;
    Section akaSection;
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
        courseName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), CourseActivity.class);
                intent.putExtra("course", section.getCourse().getCode());
                getActivity().startActivity(intent);
            }
        });
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
        MainDataBaseHandler db = new MainDataBaseHandler(getActivity());
        akaSection = null;
        tag:
        for (Section sec : db.getSectionsOfInstructorsWithIntervals(section.getInstructor().getId())){
            if(sec.getCourse().getCode().equals(section.getCourse().getCode()))
                continue;
            for (Interval interval: section.getIntervals()){
                Iterator<Interval> iter = sec.getIntervals().iterator();
                if(iter.hasNext()) {
                    Interval interval1 = iter.next();
                    if(interval.getDay().equals(interval1.getDay()) && interval.getHour().equals(interval1.getHour())) {
                        akaSection = sec;
                        break tag;
                    }

                }

            }

        }
        if(akaSection != null){
            TextView akaText = (TextView) rootView.findViewById(R.id.akaText);
            akaText.setVisibility(View.VISIBLE);

            Button akaButton = (Button) rootView.findViewById(R.id.akaButton);
            akaButton.setVisibility(View.VISIBLE);
            akaButton.setText(akaSection.getCourse().getCode());
            akaButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), CourseActivity.class);
                    intent.putExtra("course", akaSection.getCourse().getCode());
                    getActivity().startActivity(intent);
                }
            });

        }
        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.section = (Section)args.getSerializable("section");
    }
}
