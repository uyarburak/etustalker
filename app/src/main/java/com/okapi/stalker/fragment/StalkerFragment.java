package com.okapi.stalker.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.LoginActivity;
import com.okapi.stalker.activity.MainActivity;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.MainDataBaseHandler;
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
        MainDataBaseHandler db = new MainDataBaseHandler(getActivity());

        if(getActivity() instanceof MainActivity){
            myStalkerAdapter = new MyStalkerAdapter(getActivity(), db.getAllStudents());
        }else if(getActivity() instanceof SectionActivity){
            myStalkerAdapter = new MyStalkerAdapter(getActivity(),
                    ((SectionActivity)getActivity()).getSection().getStudents());
        }
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
        }
        return true;

    }
}
