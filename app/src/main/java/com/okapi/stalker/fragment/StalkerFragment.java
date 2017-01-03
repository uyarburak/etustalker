package com.okapi.stalker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.CourseActivity;
import com.okapi.stalker.activity.DepartmentActivity;
import com.okapi.stalker.activity.MainActivity;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.data.storage.model.Department;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.adapters.MyStalkerAdapter;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Set;

public class StalkerFragment extends Fragment {

    private MyStalkerAdapter myStalkerAdapter;
    private View rootView;
    private Set<Student> students;
    public StalkerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainDataBaseHandler db = new MainDataBaseHandler(getActivity());
        students = new HashSet<>();
        if(getActivity() instanceof MainActivity){
            students = db.getAllStudents();
        }else if(getActivity() instanceof SectionActivity){
            students = ((SectionActivity)getActivity()).getSection().getStudents();
        }else if(getActivity() instanceof CourseActivity){
            Course course = ((CourseActivity)getActivity()).getCourse();
            for (Section section: course.getSections()){
                students.addAll(db.getStudentsOfSection(section.getId()));
            }
        }else if(getActivity() instanceof DepartmentActivity){
            Department department = ((DepartmentActivity)getActivity()).getDepartment();
            students = department.getStudents();
        }
        myStalkerAdapter = new MyStalkerAdapter(getActivity(), students);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_stalker, container, false);


            ListView listView = (ListView) rootView.findViewById(R.id.listStalk);
            listView.setAdapter(myStalkerAdapter);
            listView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long l) {
                            Intent intent = new Intent(getActivity(), StudentActivity.class);
                            intent.putExtra("student", (Serializable) a.getAdapter().getItem(position));
                            getActivity().startActivity(intent);
                            myStalkerAdapter.changeColor(position, v);
                        }
                    });

        }
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_sort_by_name:
                myStalkerAdapter.sort(MyStalkerAdapter.OrderBy.NAME);
                break;
            case R.id.action_sort_by_id:
                myStalkerAdapter.sort(MyStalkerAdapter.OrderBy.ID);
                break;
            case R.id.action_sort_by_sex:
                myStalkerAdapter.sort(MyStalkerAdapter.OrderBy.SEX);
                break;
            case R.id.action_sort_by_department:
                myStalkerAdapter.sort(MyStalkerAdapter.OrderBy.DEPARTMENT);
                break;
            case R.id.action_stats:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());
                int male , female, unisex;
                male = female = unisex = 0;
                for (Student student : students){
                    switch (student.getGender()) {
                        case 'M':
                            male++;
                            break;
                        case 'F':
                            female++;
                            break;
                        case 'U':
                            unisex++;
                            break;
                    }
                }
                DecimalFormat df = new DecimalFormat("0.##");
                float total = male + female + unisex;
                StringBuilder sb = new StringBuilder();
                sb.append(getString(R.string.male) + ": ").append(male).append(" (").append(df.format(male/total * 100)).append("%)\n");
                sb.append(getString(R.string.female)+ ": ").append(female).append(" (").append(df.format(female/total * 100)).append("%)\n");
                sb.append(getString(R.string.unisex)+ ": ").append(unisex).append(" (").append(df.format(unisex/total * 100)).append("%)\n");
                sb.append(getString(R.string.total)+ ": ").append(male + female + unisex).append("\n");
                alertDialogBuilder.setTitle(getString(R.string.stats));
                alertDialogBuilder.setMessage(sb.toString());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }
        return true;

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stalker_fragment_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myStalkerAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }
}
