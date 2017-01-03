package com.okapi.stalker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.DepartmentActivity;
import com.okapi.stalker.activity.InstructorActivity;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Instructor;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.fragment.adapters.MySectionAdapter;

import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;


public class InstructorProfileFragment extends Fragment {

    public InstructorProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_instructor_profile, container, false);
        final Instructor instructor = ((InstructorActivity) getActivity()).getInstructor();

        TextView textName = (TextView) rootView.findViewById(R.id.profile_name);
        textName.setText(instructor.getName());
        if(instructor.getName().length() > 23)
            textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com.tr/search?q=" + instructor.getName()));
                startActivity(browserIntent);
            }
        });

        TextView textMail = (TextView) rootView.findViewById(R.id.profile_email);
        textMail.setText(instructor.getMail());
        TextView textOffice = (TextView) rootView.findViewById(R.id.profile_office);
        textOffice.setText(instructor.getOffice());
        TextView textMajor = (TextView) rootView.findViewById(R.id.profile_major);
        textMajor.setText(instructor.getDepartment().getName());
        textMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DepartmentActivity.class);
                intent.putExtra("department", instructor.getDepartment().getName());
                getActivity().startActivity(intent);
            }
        });
        TextView textWebsite = (TextView) rootView.findViewById(R.id.profile_website);
        textWebsite.setText(instructor.getWebsite());
        TextView textLabUrl = (TextView) rootView.findViewById(R.id.profile_lab_url);
        textLabUrl.setText(instructor.getLab());
        final TextView textImage = (TextView) rootView.findViewById(R.id.profile_image);
        textImage.setText(instructor.getImage());
        if(!instructor.getImage().isEmpty()){
            textImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instructor.getImage()));
                    startActivity(browserIntent);
                }
            });
        }
        if(!instructor.getWebsite().isEmpty()){
            textWebsite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instructor.getWebsite()));
                    startActivity(browserIntent);
                }
            });
        }
        FloatingActionButton floatingActionButton =
                (FloatingActionButton)rootView.findViewById(R.id.fab_email);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(instructor.getMail() == null){
                    Snackbar snackbar = Snackbar
                            .make(rootView, getString(R.string.no_mail_found), Snackbar.LENGTH_LONG);

                    snackbar.show();
                }else{
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                    emailIntent.setData(Uri.parse("mailto:" + instructor.getMail()));
                    startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
                }
            }
        });

        Set<Section> detailedSections = new TreeSet<Section>(new Comparator<Section>() {
            @Override
            public int compare(Section lhs, Section rhs) {
                return lhs.getCourse().getCode().compareTo(rhs.getCourse().getCode());
            }
        });
        MainDataBaseHandler db = new MainDataBaseHandler(getActivity());
        for (Section sectionOnlyId : instructor.getSections()) {
            detailedSections.add(db.getSectionWithoutStudents(sectionOnlyId.getId()));
        }
        ListView sectionList = (ListView) rootView.findViewById(R.id.sections_list);
        sectionList.setAdapter(new MySectionAdapter(getActivity(), detailedSections));
        sectionList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long l) {
                        Intent intent = new Intent(getActivity(), SectionActivity.class);
                        intent.putExtra("section", ((Section)a.getAdapter().getItem(position)).getId());
                        getActivity().startActivity(intent);
                    }
                });
        return rootView;
    }

}
