package com.okapi.stalker.fragment.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.github.pavlospt.roundedletterview.RoundedLetterView;
import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by burak on 9/27/2016.
 */
public class CourseListAdapter extends BaseAdapter implements Filterable {

    private String lastSearch;
    private LayoutInflater mInflater;

    private Set<Course> allCourses;
    private List<Course> arrayListFilter;
    private Activity activity;

    public CourseListAdapter(Activity activity) {
        MainDataBaseHandler db = new MainDataBaseHandler(activity);
        this.activity = activity;
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        allCourses = db.getAllCourses();
        arrayListFilter = new ArrayList<>();
        arrayListFilter.addAll(allCourses);

    }


    @Override
    public int getCount() {
        return arrayListFilter.size();
    }

    @Override
    public Course getItem(int position) {
        return arrayListFilter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        rowView = mInflater.inflate(R.layout.course_row, null);

        TextView textName =
                (TextView) rowView.findViewById(R.id.name);
        TextView textTitle =
                (TextView) rowView.findViewById(R.id.course_name);
        RoundedLetterView roundedLetterView =
                (RoundedLetterView) rowView.findViewById(R.id.rlv_name_view);

        final Course course = getItem(position);
        textName.setText(course.getCode());
        textTitle.setText(course.getTitle());
        char firstLetter = course.getCode().charAt(0);
        roundedLetterView.setTitleText(Character.toString(firstLetter));
        roundedLetterView.setTitleSize(getPixels(TypedValue.COMPLEX_UNIT_SP, 45));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        roundedLetterView.setBackgroundColor(generator.getColor(firstLetter));
        return rowView;
    }
    private float getPixels(int unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(unit, size, metrics);
    }

    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayListFilter = (List<Course>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                lastSearch = constraint.toString().trim();
                List<Course> filteredArrayNames = CourseListAdapter.this.filter();
                results.count = filteredArrayNames.size();
                results.values = filteredArrayNames;

                return results;
            }
        };

        return filter;
    }

    private List<Course> filter() {
        List<Course> tmp = new ArrayList<Course>();
        if (lastSearch == null || lastSearch.isEmpty()) {
            tmp.addAll(allCourses);
        }else{
            for (Course course: allCourses){
                if(course.getTitle().toUpperCase().contains(lastSearch.toUpperCase(new Locale("tr", "TR")))
                        ||
                        course.getCode().contains(lastSearch.toUpperCase(new Locale("tr", "TR")))){
                    tmp.add(course);
                }
            }
        }
        return tmp;
    }

}