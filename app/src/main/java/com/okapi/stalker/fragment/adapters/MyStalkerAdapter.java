package com.okapi.stalker.fragment.adapters;

import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Student;
import com.okapi.stalker.search.FragmentarySearch;
import com.okapi.stalker.search.SearchParser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MyStalkerAdapter extends BaseAdapter implements Filterable {

    private LayoutInflater mInflater;

    private Stash stash;
    private List<Student> arrayListFilter;
    private HashSet<String> discoveredStudents;

    public MyStalkerAdapter(Activity activity, Set<String> studentKeys) {
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        stash = Stash.get();
        arrayListFilter = new ArrayList<>();
        for (String key : studentKeys) {
            arrayListFilter.add(stash.getStudent(key));
        }
        discoveredStudents = new HashSet<>();
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

        TextView textName =
                (TextView) rowView.findViewById(R.id.name);
        TextView textMajor =
                (TextView) rowView.findViewById(R.id.department);
        ImageView imageView =
                (ImageView) rowView.findViewById(R.id.thumb);

        final Student student = getItem(position);
        textName.setText(student.name);
        textMajor.setText(student.major);
        imageView.setImageResource(R.drawable.app_icon);
        if(discoveredStudents.contains(student.key()))
            textName.setTextColor(Color.RED);
        return rowView;
    }

    public void changeColor(int position, View view){
        TextView textName =
                (TextView) view.findViewById(R.id.name);
        textName.setTextColor(Color.RED);
        Student student = getItem(position);
        discoveredStudents.add(student.key());
    }
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayListFilter = (List<Student>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                List<Student> filteredArrayNames = new ArrayList<>();
                String searchKey = constraint.toString().trim();
                if (searchKey == null) {
                    for (String key : stash.getStudentKeys()) {
                        filteredArrayNames.add(stash.getStudent(key));
                    }
                } else {
                    SearchParser parser = new SearchParser();
                    parser.giveInput(searchKey);
                    for (String k: parser.getResults()) {
                        filteredArrayNames.add(stash.getStudent(k));
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