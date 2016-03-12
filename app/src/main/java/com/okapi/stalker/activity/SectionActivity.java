package com.okapi.stalker.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.adapter.ViewPagerAdapter;
import com.okapi.stalker.data.storage.type.Section;
import com.okapi.stalker.data.storage.type.Student;
import com.okapi.stalker.fragment.ProgramFragment;
import com.okapi.stalker.fragment.SectionProfileFragment;
import com.okapi.stalker.fragment.SectionProgramFragment;
import com.okapi.stalker.fragment.StalkerFragment;
import com.okapi.stalker.fragment.StudentProfileFragment;

import java.util.ArrayList;
import java.util.List;

public class SectionActivity extends AppCompatActivity {

    private Section section;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private SectionProfileFragment sectionProfileFragment;
    private SectionProgramFragment programFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        section = (Section) getIntent().getExtras().getSerializable("section");
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setTitle(section.course + " - "  + section.number);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setupViewPager(ViewPager viewPager) {

        programFragment = new SectionProgramFragment();
        programFragment.setSection(section);
        sectionProfileFragment = new SectionProfileFragment();
        sectionProfileFragment.setCourse(section);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(sectionProfileFragment, getString(R.string.title_overview));
        adapter.addFragment(new StalkerFragment(), getString(R.string.title_students));
        adapter.addFragment(programFragment, getString(R.string.title_program));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public Section getSection() {
        return section;
    }


}
