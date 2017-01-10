package com.okapi.stalker.fragment.adapters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.FriendsDataBaseHandler;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.data.storage.model.Tag;
import com.okapi.stalker.fragment.StalkerFragment;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.HashSet;
import java.util.List;

/**
 * Created by burak on 1/6/2017.
 */
public class StalkerRecylerAdapter extends RecyclerView.Adapter<StalkerRecylerAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {
    private List<Student> mStudents;
    private Context mContext;

    public StalkerFragment.SearchingType searchingType;

    private HashSet<String> discoveredStudents;
    private HashSet<String> friends;

    public StalkerRecylerAdapter(Context context, List<Student> students) {
        mContext = context;
        mStudents = students;


        searchingType = StalkerFragment.SearchingType.NAME;

        discoveredStudents = new HashSet<>();
        FriendsDataBaseHandler fdb = new FriendsDataBaseHandler(context);
        friends = new HashSet<>(fdb.getAllFriends());
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_stalker_list, null, false));
    }

    public List<Student> getQuestions() {
        return mStudents;
    }

    public void setQuestions(List<Student> students) {
        this.mStudents = students;
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Student student = mStudents.get(position);

        holder.textName.setText(student.getName());
        if(searchingType == StalkerFragment.SearchingType.NAME){
            if(student.getDepartment2() != null){
                holder.textMajor.setText(student.getDepartment().getName() + " - " + student.getDepartment2().getName());
            }else{
                holder.textMajor.setText(student.getDepartment().getName());
            }
        }else{
            holder.textMajor.setText(student.getId());
        }

        switch (student.getGender()){
            case 'M':
                holder.imageView.setImageResource(R.drawable.ic_gender_male);
                break;
            case 'F':
                holder.imageView.setImageResource(R.drawable.ic_gender_female);
                break;
            default:
                holder.imageView.setImageResource(R.drawable.ic_help);
                break;
        }

        if(discoveredStudents.contains(student.getId()))
            holder.textName.setTextColor(Color.RED);
        else if(friends.contains(student.getId()))
            holder.textName.setTextColor(Color.BLUE);
        else if(student.getActive()!= null && !student.getActive())
            holder.textName.setTextColor(Color.LTGRAY);
        else
            holder.textName.setTextColor(Color.DKGRAY);
    }

    private int getColor(int color) {
        return ContextCompat.getColor(mContext, color);
    }

    public void setDiscovered(int position){
        discoveredStudents.add(mStudents.get(position).getId());
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mStudents.size();
    }

    @NonNull
    @Override
    public String getSectionName(int position) {
        return mStudents.get(position).getName().substring(0, 3);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView textName;
        TextView textMajor;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);

            textName = (TextView) itemView.findViewById(R.id.name);
            textMajor = (TextView) itemView.findViewById(R.id.department);
            imageView = (ImageView) itemView.findViewById(R.id.thumb);
        }
    }
}
