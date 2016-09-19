package com.okapi.stalker.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.FriendsDataBaseHandler;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.adapters.MySectionAdapter;

import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class StudentProfileFragment extends Fragment {

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
        TextView textName = (TextView) rootView.findViewById(R.id.profile_name);
        textName.setText(student.getName());
        if(student.getName().length() > 23)
            textName.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        textName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com.tr/search?q="+student.getName()));
                startActivity(browserIntent);
            }
        });
        TextView textNo = (TextView) rootView.findViewById(R.id.profile_id);
        textNo.setText("" + student.getId());
        TextView textMail = (TextView) rootView.findViewById(R.id.profile_email);
        textMail.setText(student.getMail());
        TextView textMajor = (TextView) rootView.findViewById(R.id.profile_major);
        textMajor.setText(student.getDepartment().getName());
        if(student.getDepartment2() != null){
            TextView textMajor2 = (TextView) rootView.findViewById(R.id.profile_major_2);
            TextView textMajor2Title = (TextView) rootView.findViewById(R.id.profile_major_2_title);
            textMajor2.setText(student.getDepartment2().getName());
            textMajor2.setVisibility(View.VISIBLE);
            textMajor2Title.setVisibility(View.VISIBLE);
        }

        final Button button = (Button) rootView.findViewById(R.id.profile_add_friend_button);

        MainDataBaseHandler dbMain = new MainDataBaseHandler(getActivity());
        Set<Section> detailedSections = new TreeSet<Section>(new Comparator<Section>() {
            @Override
            public int compare(Section lhs, Section rhs) {
                return lhs.getCourse().getCode().compareTo(rhs.getCourse().getCode());
            }
        });
        for (Section sectionOnlyId : student.getSections()) {
            detailedSections.add(dbMain.getSectionWithoutStudents(sectionOnlyId.getId()));
        }
        ListView sectionList = (ListView) rootView.findViewById(R.id.sectionsOfStudent);
        sectionList.setAdapter(new MySectionAdapter(getActivity(), detailedSections));
        sectionList.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long l) {
                        Intent intent = new Intent(getActivity(), SectionActivity.class);
                        intent.putExtra("section", ((Section)a.getAdapter().getItem(position)).getId());
                        getActivity().startActivity(intent);
                    }
                });



        final FriendsDataBaseHandler db = new FriendsDataBaseHandler(getActivity());
        List<String> friendsList = db.getAllFriends();
        if(friendsList.contains(student.getId())){
            button.setText(getString(R.string.remove_friend));
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.deleteFriend(student.getId());
                    button.setEnabled(false);
                }
            });
        }
        else{
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    db.addFriend(student.getId());
                    button.setEnabled(false);
                }
            });
        }

        FloatingActionButton floatingActionButton =
                (FloatingActionButton)rootView.findViewById(R.id.fab_email);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:" + student.getMail()));
                startActivity(Intent.createChooser(emailIntent, getString(R.string.send_mail)));
            }
        });
        return rootView;
    }

}
