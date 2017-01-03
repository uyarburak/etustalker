package com.okapi.stalker.activity;

/**
 * Created by burak on 9/27/2016.
 */

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.adapter.ViewPagerAdapter;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.ProgramFragment;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProgramActivity extends AppCompatActivity {
    List<Section> sections;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sections = (List<Section>) getIntent().getSerializableExtra("sections");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

    }

    private void setupViewPager(ViewPager viewPager) {
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        Student student = new Student();
        Set<Section> set = new HashSet<>(sections);
        student.setSections(set);

        ProgramFragment programFragment = new ProgramFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("owner", student);
        programFragment.setArguments(bundle);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(programFragment, getString(R.string.title_program));
        viewPager.setAdapter(adapter);
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
