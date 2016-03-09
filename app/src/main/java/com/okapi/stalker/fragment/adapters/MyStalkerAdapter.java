package com.okapi.stalker.fragment.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Student;
import com.okapi.stalker.search.FragmentarySearch;
import com.okapi.stalker.search.FragmentarySearch.Heuristic;
import com.okapi.stalker.search.SearchAssist;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class MyStalkerAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater mInflater;

    private Stash stash;
    private List<Student> arrayListFilter;
    private Context context;

    public MyStalkerAdapter(Activity activity) {
        context = activity;
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        stash = Stash.get();
        arrayListFilter = new ArrayList<>();
        for(String key: stash.getStudentKeys()){
            arrayListFilter.add(stash.getStudent(key));
        }
        System.out.println("zaaaaa: " + arrayListFilter.size());

    }


    @Override
    public int getCount() {
        return arrayListFilter.size();
    }

    @Override
    public Student getItem(int position) {
        return arrayListFilter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        rowView = mInflater.inflate(R.layout.fragment_stalker_list, null);

        TextView textView =
                (TextView) rowView.findViewById(R.id.name);
        ImageView imageView =
                (ImageView) rowView.findViewById(R.id.thumb);

        final Student student = getItem(position);
        textView.setText(student.name);
        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentActivity.class);
                intent.putExtra("student", (Serializable) student);
                context.startActivity(intent);
            }
        });
        imageView.setImageResource(R.drawable.app_icon);

        return rowView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayListFilter = (List<Student>)results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                List<Student> filteredArrayNames = new ArrayList<>();
                String searchKey = constraint.toString().trim();
                if(searchKey == null){
                    for(String key: stash.getStudentKeys()){
                        filteredArrayNames.add(stash.getStudent(key));
                    }
                }
                else{
                    System.out.println(searchKey);
                    FragmentarySearch frag = new FragmentarySearch(searchKey
                            , Heuristic.CONSECUTIVE_DISTANCE, stash.getStudentKeys());
                    for(FragmentarySearch.SearchResult s: frag.getSearchResults()) {
                        filteredArrayNames.add(stash.getStudent(s.key));
                    }
                }


                results.count = filteredArrayNames.size();
                results.values = filteredArrayNames;

                return results;
            }
        };

        return filter;
    }
}