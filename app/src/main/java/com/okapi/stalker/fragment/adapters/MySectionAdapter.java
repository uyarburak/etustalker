package com.okapi.stalker.fragment.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.storage.model.Section;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by burak on 6/24/2016.
 */
public class MySectionAdapter extends BaseAdapter implements Filterable {
    private LayoutInflater mInflater;

    private Set<Section> allSections;
    private List<Section> arrayListFilter;
    private HashSet<String> discoveredSections;

    public MySectionAdapter(Activity activity, Set<Section> sectionSet) {
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        arrayListFilter = new ArrayList<>();
        arrayListFilter.addAll(sectionSet);
        discoveredSections = new HashSet<>();
        allSections = sectionSet;
    }


    @Override
    public int getCount() {
        return arrayListFilter.size();
    }

    @Override
    public Section getItem(int position) {
        return arrayListFilter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        rowView = mInflater.inflate(R.layout.section_row, null);

        TextView textName =
                (TextView) rowView.findViewById(R.id.name);
        TextView textInstructorName =
                (TextView) rowView.findViewById(R.id.instructor_name);
        TextView textSectionNo =
                (TextView) rowView.findViewById(R.id.section_no);

        final Section section = getItem(position);
        textName.setText(section.getCourse().getCode() + " ("+ section.getCourse().getTitle() +") (" + section.getSize() + ")");
        textInstructorName.setText(section.getInstructor().getName());
        textSectionNo.setText(section.getSectionNo().toString());
        return rowView;
    }
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayListFilter = (List<Section>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String searchCode = constraint.toString().trim();
                List<Section> filteredArrayNames = new ArrayList<Section>();
                if (searchCode == null || searchCode.isEmpty()) {
                    filteredArrayNames.addAll(allSections);
                }else{
                    for (Section section: allSections){
                        if(section.getCourse().getCode().contains(searchCode.toUpperCase(new Locale("tr", "TR")))){
                            filteredArrayNames.add(section);
                        }
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