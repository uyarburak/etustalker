package com.okapi.stalker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.DataBaseHandler;
import com.okapi.stalker.fragment.adapters.MyFriendsAdapter;


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
        ListView liste = (ListView )rootView.findViewById(R.id.list_friends);
        liste.setAdapter(myFriendsAdapter);
        DataBaseHandler.myFriendsAdapter = myFriendsAdapter;
        return rootView;
    }

}
