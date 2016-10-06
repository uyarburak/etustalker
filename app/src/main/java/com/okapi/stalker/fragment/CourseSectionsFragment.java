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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.CourseActivity;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.fragment.adapters.CourseListAdapter;
import com.okapi.stalker.fragment.adapters.MySectionAdapter;

/**
 * Created by burak on 10/4/2016.
 */
/**
 * Created by burak on 9/27/2016.
 */
public class CourseSectionsFragment extends Fragment {

    private MySectionAdapter myAdapter;
    private View rootView;
    public CourseSectionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAdapter = new MySectionAdapter(getActivity(), ((CourseActivity)getActivity()).getCourse().getSections());
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
                            Intent intent = new Intent(getActivity(), SectionActivity.class);
                            intent.putExtra("section", ((Section)a.getAdapter().getItem(position)).getId());
                            getActivity().startActivity(intent);
                        }
                    });

        }
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_section_list, menu);
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

