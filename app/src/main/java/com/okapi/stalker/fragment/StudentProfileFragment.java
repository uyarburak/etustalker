package com.okapi.stalker.fragment;

import android.content.Context;
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
import android.widget.Toast;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.DepartmentActivity;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.FriendsDataBaseHandler;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.adapters.MySectionAdapter;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;


public class StudentProfileFragment extends Fragment {
    private static final int NO_FRIEND = 0;
    private static final int FRIEND = 1;
    private int isFriend;

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
        final TextView textNo = (TextView) rootView.findViewById(R.id.profile_id);
        textNo.setText("" + student.getId());
        textNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Text Label", student.getId());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity().getApplicationContext(), getString(R.string.copied_id), Toast.LENGTH_SHORT).show();
            }
        });
        TextView textMail = (TextView) rootView.findViewById(R.id.profile_email);
        textMail.setText(student.getMail());
        TextView textYear = (TextView) rootView.findViewById(R.id.student_year);
        textYear.setText(student.getYear().toString());
        TextView textMajor = (TextView) rootView.findViewById(R.id.profile_major);
        textMajor.setText(student.getDepartment().getName());
        textMajor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DepartmentActivity.class);
                intent.putExtra("department", student.getDepartment().getName());
                getActivity().startActivity(intent);
            }
        });
        if(student.getDepartment2() != null){
            TextView textMinor = (TextView) rootView.findViewById(R.id.profile_minor);
            TextView textMinorTitle = (TextView) rootView.findViewById(R.id.profile_minor_title);
            textMinor.setText(student.getDepartment2().getName());
            textMinor.setVisibility(View.VISIBLE);
            textMinorTitle.setVisibility(View.VISIBLE);
            textMinor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), DepartmentActivity.class);
                    intent.putExtra("department", student.getDepartment2().getName());
                    getActivity().startActivity(intent);
                }
            });
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
            isFriend = FRIEND;
            button.setText(getString(R.string.remove_friend));
        }
        else{
            isFriend = NO_FRIEND;
            button.setText(getString(R.string.add_friend));
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isFriend == FRIEND){
                    isFriend = NO_FRIEND;
                    button.setText(getString(R.string.add_friend));
                    db.deleteFriend(student.getId());
                }else{
                    isFriend = FRIEND;
                    button.setText(getString(R.string.remove_friend));
                    db.addFriend(student.getId());
                }
            }
        });
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
