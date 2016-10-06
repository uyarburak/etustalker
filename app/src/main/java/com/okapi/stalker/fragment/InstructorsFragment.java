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
import com.okapi.stalker.activity.InstructorActivity;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Instructor;
import com.okapi.stalker.data.storage.model.Person;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.adapters.InstructorsListAdapter;
import com.okapi.stalker.fragment.adapters.MyStalkerAdapter;

import java.io.Serializable;
import java.text.DecimalFormat;

/**
 * Created by burak on 9/27/2016.
 */
public class InstructorsFragment extends Fragment {

    private InstructorsListAdapter myAdapter;
    private View rootView;
    public InstructorsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myAdapter = new InstructorsListAdapter(getActivity());
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
                            Intent intent = new Intent(getActivity(), InstructorActivity.class);
                            intent.putExtra("instructor", ((Instructor)a.getAdapter().getItem(position)).getId());
                            getActivity().startActivity(intent);
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
                myAdapter.sort(InstructorsListAdapter.OrderBy.NAME);
                break;
            case R.id.action_sort_by_sex:
                myAdapter.sort(InstructorsListAdapter.OrderBy.SEX);
                break;
            case R.id.action_sort_by_department:
                myAdapter.sort(InstructorsListAdapter.OrderBy.DEPARTMENT);
                break;
            case R.id.action_stats:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());
                int male , female, unisex, withPhoto;
                male = female = unisex = withPhoto = 0;
                for (Person person : myAdapter.getAllInstructors()){
                    switch (person.getGender()) {
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
                    if(person.getImage() != null && person.getImage().length() > 5){
                        withPhoto++;
                    }
                }
                DecimalFormat df = new DecimalFormat("0.##");
                float total = male + female + unisex;
                StringBuilder sb = new StringBuilder();
                sb.append("Male: ").append(male).append(" (").append(df.format(male/total * 100)).append("%)\n");
                sb.append("Female: ").append(female).append(" (").append(df.format(female/total * 100)).append("%)\n");
                sb.append("Unisex: ").append(unisex).append(" (").append(df.format(unisex/total * 100)).append("%)\n");
                sb.append("Total: ").append(male + female + unisex).append("\n");

                sb.append("With Photo: ").append(withPhoto).append(" (").append(df.format(withPhoto/total * 100)).append("%)\n");;
                alertDialogBuilder.setTitle("Stats");
                alertDialogBuilder.setMessage(sb.toString());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                break;

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
                myAdapter.getFilter().filter(newText);
                return false;
            }
        });


    }
}
