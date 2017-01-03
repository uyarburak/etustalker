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
import android.widget.TextView;

import com.github.pavlospt.roundedletterview.RoundedLetterView;
import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Department;
import com.okapi.stalker.util.ColorGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by burak on 10/12/2016.
 */
public class DepartmentListAdapter extends BaseAdapter {

    private String lastSearch;
    private LayoutInflater mInflater;

    private List<Department> allDepartment;
    private Activity activity;

    public DepartmentListAdapter(Activity activity) {
        MainDataBaseHandler db = new MainDataBaseHandler(activity);
        this.activity = activity;
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        allDepartment = new ArrayList<>(db.getAllDepartments());


    }


    @Override
    public int getCount() {
        return allDepartment.size();
    }

    @Override
    public Department getItem(int position) {
        return allDepartment.get(position);
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

        final Department department = getItem(position);
        textName.setText(department.getName());
        textTitle.setText(department.getMainURL());
        char firstLetter = department.getName().charAt(0);
        roundedLetterView.setTitleText(Character.toString(firstLetter));
        roundedLetterView.setTitleSize(getPixels(TypedValue.COMPLEX_UNIT_SP, 35));
        ColorGenerator generator = ColorGenerator.MATERIAL;
        roundedLetterView.setBackgroundColor(generator.getColor(firstLetter));
        return rowView;
    }
    private float getPixels(int unit, float size) {
        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        return TypedValue.applyDimension(unit, size, metrics);
    }

}