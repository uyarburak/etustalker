package com.okapi.stalker.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.DataBaseHandler;
import com.okapi.stalker.data.storage.type.Student;
import com.okapi.stalker.fragment.adapters.MyFriendsAdapter;

import java.io.Serializable;

public class FriendsFragment extends Fragment {

    MyFriendsAdapter myFriendsAdapter;

    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        myFriendsAdapter = new MyFriendsAdapter(getActivity());
        ListView listView = (ListView) rootView.findViewById(R.id.list_friends);
        listView.setAdapter(myFriendsAdapter);
        DataBaseHandler.myFriendsAdapter = myFriendsAdapter;
        registerForContextMenu(listView);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long l) {
                        Intent intent = new Intent(getActivity(), StudentActivity.class);
                        intent.putExtra("student", (Serializable) a.getAdapter().getItem(position));
                        getActivity().startActivity(intent);
                    }
                });
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, "Remove from friends");
        menu.add(0, v.getId(), 0, "Remove all");
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if(item.getTitle()=="Remove from friends"){
            Student student = (Student)myFriendsAdapter.getItem(info.position);
            DataBaseHandler db = new DataBaseHandler(getActivity());
            db.deleteFriend(student.key());
            Toast.makeText(getContext(),student.name + " has removed.",Toast.LENGTH_LONG).show();
            myFriendsAdapter.init();
        }
        else if(item.getTitle()=="Remove all"){
            DataBaseHandler db = new DataBaseHandler(getActivity());
            db.deleteAllFriends();
            Toast.makeText(getContext(),"All your friends has removed.",Toast.LENGTH_LONG).show();
            myFriendsAdapter.init();
        }else{
            return false;
        }
        return true;
    }


}
