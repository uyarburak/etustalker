package com.okapi.stalker.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.app.AlertDialog;
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
import com.okapi.stalker.data.FriendsDataBaseHandler;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.adapters.MyFriendsAdapter;

import java.io.Serializable;

public class FriendsFragment extends Fragment {

    private MyFriendsAdapter myFriendsAdapter;

    public FriendsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myFriendsAdapter = new MyFriendsAdapter(getActivity());
        FriendsDataBaseHandler.myFriendsAdapter = myFriendsAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        ListView listView = (ListView) rootView.findViewById(R.id.list_friends);
        listView.setAdapter(myFriendsAdapter);
        registerForContextMenu(listView);
        listView.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> a, View v, int position, long l) {
                        if(((Student)a.getAdapter().getItem(position)).getDepartment() != null){
                            Intent intent = new Intent(getActivity(), StudentActivity.class);
                            intent.putExtra("student", (Serializable) a.getAdapter().getItem(position));
                            getActivity().startActivity(intent);
                        }
                    }
                });
        return rootView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, v.getId(), 0, getString(R.string.remove_friend));
        menu.add(0, v.getId(), 0, getString(R.string.remove_all));
    }
    @Override
    public boolean onContextItemSelected(MenuItem item){
        AdapterView.AdapterContextMenuInfo info =
                (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        if(item.getTitle()==getString(R.string.remove_friend)){
            Student student = (Student)myFriendsAdapter.getItem(info.position);
            FriendsDataBaseHandler db = new FriendsDataBaseHandler(getActivity());
            db.deleteFriend(student.getId());
            Toast.makeText(getContext(), getString(R.string.xxx_has_removed, student.getName()), Toast.LENGTH_LONG).show();
        }
        else if(item.getTitle()==getString(R.string.remove_all)){
            dialogRemoveAll();
        }else{
            return false;
        }
        return true;
    }

    private void dialogRemoveAll(){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

        // Setting Dialog Title
        alertDialog.setTitle(getString(R.string.comfirm_deletion));

        // Setting Dialog Message
        alertDialog.setMessage(getString(R.string.comfirm_delete_all));

        // Setting Icon to Dialog
        // alertDialog.setIcon(R.drawable.delete);

        // Setting Positive "Yes" Button
        alertDialog.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                FriendsDataBaseHandler db = new FriendsDataBaseHandler(getActivity());
                db.deleteAllFriends();
                Toast.makeText(getContext(), getString(R.string.all_friends_removed), Toast.LENGTH_LONG).show();
            }
        });

        // Setting Negative "NO" Button
        alertDialog.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }


}
