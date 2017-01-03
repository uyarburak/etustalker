package com.okapi.stalker.fragment.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Instructor;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by burak on 9/27/2016.
 */
public class InstructorsListAdapter extends BaseAdapter implements Filterable {

    public enum OrderBy{NAME, SEX, DEPARTMENT};
    private OrderBy orderBy;

    private String lastSearch;
    private LayoutInflater mInflater;

    public Set<Instructor> getAllInstructors() {
        return allInstructors;
    }

    private Set<Instructor> allInstructors;
    private List<Instructor> arrayListFilter;
    private Activity activity;

    public InstructorsListAdapter(Activity activity) {
        this(activity, new MainDataBaseHandler(activity).getAllInstructors());
    }
    public InstructorsListAdapter(Activity activity, Set<Instructor> instructors) {
        this.activity = activity;
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        allInstructors = instructors;
        arrayListFilter = new ArrayList<>();
        arrayListFilter.addAll(allInstructors);
        orderBy = OrderBy.NAME;
        System.out.println("zaaaaa: " + arrayListFilter.size());

    }


    @Override
    public int getCount() {
        return arrayListFilter.size();
    }

    @Override
    public Instructor getItem(int position) {
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

        final Instructor instructor = getItem(position);
        textName.setText(instructor.getName());
        textMajor.setText(instructor.getDepartment().getName());
        if(instructor.getImage().isEmpty()){
            switch (instructor.getGender()){
                case 'M':
                    imageView.setImageResource(R.drawable.ic_gender_male);
                    break;
                case 'F':
                    imageView.setImageResource(R.drawable.ic_gender_female);
                    break;
                default:
                    imageView.setImageResource(R.drawable.ic_help);
                    break;
            }
        }else{
            Picasso.with(activity)
                    .load(instructor.getImage())
                    .resize(50, 50)
                    .centerCrop()
                    .into(imageView);
        }
        return rowView;
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayListFilter = (List<Instructor>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                lastSearch = constraint.toString().trim();
                List<Instructor> filteredArrayNames = InstructorsListAdapter.this.filter();
                results.count = filteredArrayNames.size();
                results.values = filteredArrayNames;

                return results;
            }
        };

        return filter;
    }

    private List<Instructor> filter() {
        List<Instructor> tmp = new ArrayList<Instructor>();
        if (lastSearch == null || lastSearch.isEmpty()) {
            tmp.addAll(allInstructors);
        }else{
            for (Instructor instructor: allInstructors){
                if(instructor.getName().toUpperCase().contains(lastSearch.toUpperCase(new Locale("tr", "TR")))){
                    tmp.add(instructor);
                }
            }
        }
        return tmp;
    }

    public void sort(OrderBy order){
        AbstractComparator.carpan = orderBy == order ? -1 : 1;
        Set<Instructor> sortedSet;
        switch (order){
            case NAME:
                sortedSet = new TreeSet<Instructor>(new NameComparator());
                break;
            case SEX:
                sortedSet = new TreeSet<Instructor>(new SexComparator());
                break;
            case DEPARTMENT:
                sortedSet = new TreeSet<Instructor>(new DepartmentComparator());
                break;
            default:
                sortedSet = new TreeSet<Instructor>(new NameComparator());
        }
        sortedSet.addAll(allInstructors);
        allInstructors = sortedSet;
        arrayListFilter = filter();
        notifyDataSetChanged();
        orderBy = order;
    }
}