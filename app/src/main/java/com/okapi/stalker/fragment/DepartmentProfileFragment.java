package com.okapi.stalker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.storage.model.Department;

/**
 * Created by burak on 10/12/2016.
 */
public class DepartmentProfileFragment extends Fragment {
    private Department department;
    public DepartmentProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_department_profile, container, false);
        TextView departmentName = (TextView) rootView.findViewById(R.id.dep_name);
        departmentName.setText(department.getName());
        TextView facultyName = (TextView) rootView.findViewById(R.id.dep_faculty_name);
        facultyName.setText(department.getFaculty());

        TextView webLink = (TextView) rootView.findViewById(R.id.dep_web);
        webLink.setText(department.getMainURL());
        if(department.getMainURL() != null && URLUtil.isValidUrl(department.getMainURL())){
            webLink.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(department.getMainURL()));
                    startActivity(browserIntent);
                }
            });
        }

        TextView totalSize = (TextView) rootView.findViewById(R.id.total_size_of_dep);
        totalSize.setText(Integer.toString(department.getStudents().size()));
        return rootView;
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        this.department = (Department) args.getSerializable("department");
    }
}
