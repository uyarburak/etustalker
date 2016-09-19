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
import com.okapi.stalker.data.storage.model.Instructor;
import com.okapi.stalker.fragment.InstructorProfileFragment;
import com.okapi.stalker.fragment.ProgramFragment;

public class InstructorActivity extends AppCompatActivity {

    private Instructor instructor;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private InstructorProfileFragment instructorProfileFragment;
    private ProgramFragment programFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        Integer instructorId = getIntent().getExtras().getInt("instructor");
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        instructor = db.getInstructorFull(instructorId);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setTitle(instructor.getName());
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setupViewPager(ViewPager viewPager) {

        programFragment = new ProgramFragment();
        programFragment.setOwner(instructor);
        instructorProfileFragment = new InstructorProfileFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(instructorProfileFragment, getString(R.string.title_profile));
        adapter.addFragment(programFragment, getString(R.string.title_program));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_student, menu);

        return true;
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

    public Instructor getInstructor() {
        return instructor;
    }
}
