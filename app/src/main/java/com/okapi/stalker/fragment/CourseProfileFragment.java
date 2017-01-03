package com.okapi.stalker.fragment;

/**
 * Created by burak on 10/4/2016.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.data.storage.model.Section;

public class CourseProfileFragment
        extends Fragment {
    private Course course;
    public CourseProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_course_profile, container, false);
        TextView courseCode = (TextView) rootView.findViewById(R.id.course_code);
        courseCode.setText(course.getCode());
        TextView courseTitle = (TextView) rootView.findViewById(R.id.course_title);
        courseTitle.setText(course.getTitle());

        TextView numberOfSections = (TextView) rootView.findViewById(R.id.section_numbers);
        numberOfSections.setText(Integer.toString(course.getSections().size()));

        TextView totalSize = (TextView) rootView.findViewById(R.id.total_size_of_course);
        Integer iTotalSize = 0;
        for (Section section : course.getSections()){
            iTotalSize += section.getSize();
        }
        totalSize.setText(iTotalSize.toString());

//        MainDataBaseHandler db = new MainDataBaseHandler(getActivity());
//        Section akaSection = null;
//        tag:
//        for (Section sec : db.getSectionsOfInstructorsWithIntervals(section.getInstructor().getId())){
//            if(sec.getCourse().getCode().equals(section.getCourse().getCode()))
//                continue;
//            for (Interval interval: section.getIntervals()){
//                Iterator<Interval> iter = sec.getIntervals().iterator();
//                if(iter.hasNext()) {
//                    Interval interval1 = iter.next();
//                    if(interval.getDay().equals(interval1.getDay()) && interval.getHour().equals(interval1.getHour())) {
//                        akaSection = sec;
//                        break tag;
//                    }
//
//                }
//
//            }
//
//        }
//        if(akaSection != null){
//            TextView akaText = (TextView) rootView.findViewById(R.id.akaText);
//            akaText.setVisibility(View.VISIBLE);
//
//            Button akaButton = (Button) rootView.findViewById(R.id.akaButton);
//            akaButton.setVisibility(View.VISIBLE);
//            akaButton.setText(akaSection.getCourse().getCode());
//
//        }
        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.course = (Course)args.getSerializable("course");
    }
}
