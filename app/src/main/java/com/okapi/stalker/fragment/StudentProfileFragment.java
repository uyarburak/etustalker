package com.okapi.stalker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.DataBaseHandler;
import com.okapi.stalker.data.storage.type.Student;

import java.util.List;


public class StudentProfileFragment
        extends Fragment {

    public StudentProfileFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_student_profile, container, false);
        final Student student = ((StudentActivity) getActivity()).getStudent();
        //ImageView image = (ImageView) rootView.findViewById(R.id.imageView2);
        //image.setImageResource(R.drawable.app_icon);
        TextView textAd = (TextView) rootView.findViewById(R.id.profile_name);
        textAd.setText(student.name);
        TextView textNo = (TextView) rootView.findViewById(R.id.profile_id);
        textNo.setText("" + student.id);
        TextView textMail = (TextView) rootView.findViewById(R.id.profile_email);
        textMail.setText(student.mail);
        TextView textMajor = (TextView) rootView.findViewById(R.id.profile_major);
        textMajor.setText(student.major);

        final Button button = (Button) rootView.findViewById(R.id.profile_add_friend_button);

        final DataBaseHandler db = new DataBaseHandler(getActivity());
        List<String> friendsList = db.getAllFriends();
        if(friendsList.contains(student.key())){
            button.setText("Remove friend");
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteFriend(student.key());
                    button.setEnabled(false);
                    DataBaseHandler.myFriendsAdapter.init();
                }
            });
        }
        else{
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.addFriend(student.key());
                    button.setEnabled(false);
                    DataBaseHandler.myFriendsAdapter.init();
                }
            });
        }
        return rootView;
    }

}
