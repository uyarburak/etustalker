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
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.InstructorActivity;
import com.okapi.stalker.data.storage.model.Instructor;


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
        if(!instructor.getLab().isEmpty()){
            textLabUrl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(instructor.getLab()));
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
        return rootView;
    }

}
