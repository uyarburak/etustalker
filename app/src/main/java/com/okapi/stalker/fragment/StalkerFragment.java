package com.okapi.stalker.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.github.ksoichiro.android.observablescrollview.ObservableListView;
import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.okapi.stalker.R;
import com.okapi.stalker.fragment.adapters.MyStalkerAdapter;

public class StalkerFragment extends Fragment
        implements SearchView.OnQueryTextListener {

    MyStalkerAdapter myStalkerAdapter;
    public StalkerFragment() {
    }
    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        myStalkerAdapter.getFilter().filter(newText);
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_stalker, container, false);

        ObservableListView liste = (ObservableListView )rootView.findViewById(R.id.listStalk);
        liste.setScrollViewCallbacks((ObservableScrollViewCallbacks)getActivity());
        myStalkerAdapter = new MyStalkerAdapter(getActivity());
        liste.setAdapter(myStalkerAdapter);

        return rootView;
    }
}
