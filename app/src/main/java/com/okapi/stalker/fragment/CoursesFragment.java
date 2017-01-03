package com.okapi.stalker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.CourseActivity;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.fragment.adapters.CourseListAdapter;

/**
 * Created by burak on 9/27/2016.
 */
public class CoursesFragment extends Fragment {

    private CourseListAdapter myAdapter;
    private View rootView;
    public CoursesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAdapter = new CourseListAdapter(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null){
            rootView = inflater.inflate(R.layout.fragment_stalker, container, false);


            ListView listView = (ListView) rootView.findViewById(R.id.listStalk);
            listView.setAdapter(myAdapter);
            listView.setOnItemClickListener(
                    new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> a, View v, int position, long l) {
                            Intent intent = new Intent(getActivity(), CourseActivity.class);
                            intent.putExtra("course", ((Course)a.getAdapter().getItem(position)).getCode());
                            getActivity().startActivity(intent);
                        }
                    });

        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.courses_list_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }
}

