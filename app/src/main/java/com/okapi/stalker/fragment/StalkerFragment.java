package com.okapi.stalker.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.MainActivity;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.fragment.adapters.MyStalkerAdapter;

import java.io.Serializable;

public class StalkerFragment extends Fragment implements SearchView.OnQueryTextListener {

    private MyStalkerAdapter myStalkerAdapter;
    private View rootView;
    public StalkerFragment() {
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        myStalkerAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getActivity() instanceof MainActivity){
            myStalkerAdapter = new MyStalkerAdapter(getActivity(), Stash.get().getStudentKeys());
        }else if(getActivity() instanceof SectionActivity){
            myStalkerAdapter = new MyStalkerAdapter(getActivity(),
                    ((SectionActivity)getActivity()).getSection().getStudentKeys());
        }
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
}
