package com.okapi.stalker.activity;

/**
 * Created by burak on 9/27/2016.
 */

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.okapi.stalker.R;

public class ListActivity extends AppCompatActivity {
    public static final int INSTRUCTOR_LIST = 0;
    public static final int COURSE_LIST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        switch (getIntent().getExtras().getInt("listType")){
            case INSTRUCTOR_LIST:
                setContentView(R.layout.activity_instructor_list);
                setTitle("Instructors");
                break;
            case COURSE_LIST:
                setContentView(R.layout.activity_course_list);
                setTitle("Courses");
                break;


        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
}
