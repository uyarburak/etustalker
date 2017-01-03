package com.okapi.stalker.fragment;

/**
 * Created by burak on 10/12/2016.
 */

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.DepartmentActivity;
import com.okapi.stalker.data.storage.model.Department;
import com.okapi.stalker.fragment.adapters.DepartmentListAdapter;

public class DepartmentsFragment extends Fragment {

    private DepartmentListAdapter myAdapter;
    private View rootView;
    public DepartmentsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAdapter = new DepartmentListAdapter(getActivity());
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
                            Intent intent = new Intent(getActivity(), DepartmentActivity.class);
                            intent.putExtra("department", ((Department)a.getAdapter().getItem(position)).getName());
                            getActivity().startActivity(intent);
                        }
                    });

        }
        return rootView;
    }
}

