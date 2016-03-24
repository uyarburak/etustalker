package com.okapi.stalker.fragment.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.DataBaseHandler;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Student;

import java.util.ArrayList;
import java.util.List;

public class MyFriendsAdapter extends BaseAdapter {
    public static MyFriendsAdapter myFriendsAdapter;
    private LayoutInflater mInflater;

    private Stash stash;
    private List<Student> list;
    private Context context;

    public MyFriendsAdapter(Activity activity) {
        context = activity;
        stash = Stash.get();
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        init();
        System.out.println("zaaaaa: " + getCount());

    }

    public void init() {

        DataBaseHandler db = new DataBaseHandler(context);
        list = new ArrayList<>();
        for (String key : db.getAllFriends()) {
            list.add(stash.getStudent(key));
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Student getItem(int position) {
        return list.get(position);
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

        return rowView;
    }
}