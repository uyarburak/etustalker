package com.okapi.stalker.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.adapter.ViewPagerAdapter;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.fragment.CourseProfileFragment;
import com.okapi.stalker.fragment.CourseSectionsFragment;
import com.okapi.stalker.fragment.StalkerFragment;

/**
 * Created by burak on 10/4/2016.
 */

public class CourseActivity extends AppCompatActivity {

    private Course course;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CourseProfileFragment courseProfileFragment;
    private CourseSectionsFragment courseSectionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        setContentView(R.layout.activity_main);
        String courseId = getIntent().getExtras().getString("course");
        course = db.getCourseFull(courseId);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setTitle(course.getCode() + " - "  + course.getTitle());
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setupViewPager(ViewPager viewPager) {

        Bundle args = new Bundle();
        args.putSerializable("course", course);
        courseSectionsFragment = new CourseSectionsFragment();
        courseSectionsFragment.setArguments(args);

        courseProfileFragment = new CourseProfileFragment();
        courseProfileFragment.setArguments(args);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(courseProfileFragment, getString(R.string.title_overview));
        adapter.addFragment(courseSectionsFragment, getString(R.string.title_sections));
        adapter.addFragment(new StalkerFragment(), getString(R.string.title_students));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Course getCourse() {
        return course;
    }


}
