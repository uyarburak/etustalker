package com.okapi.stalker.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Section;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by burak on 9/27/2016.
 */
public class GPAFragment extends Fragment {
    public GPAFragment() {
    }
    Map<Spinner, Spinner> courseViews;
    RelativeLayout rl;
    ViewGroup container;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        courseViews = new LinkedHashMap<Spinner, Spinner>();
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_gpa, container, false);
        // get your outer relative layout
        rl = (RelativeLayout) rootView.findViewById(R.id.rl);
        this.container = container;
        Button button = (Button) rootView.findViewById(R.id.gpaCalculate);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int totalCredit = 0;
                float totalPoint = 0f;
                for (Map.Entry<Spinner, Spinner> entry : courseViews.entrySet()){
                    totalCredit += entry.getKey().getSelectedItemPosition();
                    totalPoint += spinnerToGrade(entry.getValue().getSelectedItemPosition()) * entry.getKey().getSelectedItemPosition();
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());

                // set title
                alertDialogBuilder.setTitle(getString(R.string.gpa));

                // set dialog message
                alertDialogBuilder
                        .setMessage(Float.toString(totalPoint / totalCredit));

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();
            }
        });
        return rootView;
    }
    private float spinnerToGrade(int pos){
        switch (pos){
            case 0:
                return 4f;
            case 1:
                return 3.5f;
            case 2:
                return 3f;
            case 3:
                return 2.5f;
            case 4:
                return 2f;
            case 5:
                return 1.5f;
            case 6:
                return 1f;
            default:
                return 0f;
        }
    }
    public void setStudentId(String studentId) {

        // inflate content layout and add it to the relative layout as second child
// add as second child, therefore pass index 1 (0,1,...)

        LayoutInflater layoutInflater = (LayoutInflater)
                getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        MainDataBaseHandler db = new MainDataBaseHandler(getActivity());
        int i= 0;
        for (Section section: db.getSectionsOfStudent(studentId)){
            View row = layoutInflater.inflate(R.layout.gpa_row, container, false);
            final RelativeLayout.LayoutParams params =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW, i);
            row.setLayoutParams(params);
            EditText courseName = (EditText)row.findViewById(R.id.course_name);
            courseName.setText(db.getSectionWithoutStudents(section.getId()).getCourse().getCode());
            rl.addView(row);
            row.setId(++i);
            courseViews.put((Spinner) row.findViewById(R.id.numberPicker), (Spinner) row.findViewById(R.id.gradePicker));
        }
    }


}
