package com.okapi.stalker.activity;

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
import com.okapi.stalker.data.storage.model.Department;
import com.okapi.stalker.fragment.DepartmentProfileFragment;
import com.okapi.stalker.fragment.InstructorsFragment;
import com.okapi.stalker.fragment.StalkerFragment;

/**
 * Created by burak on 10/12/2016.
 */
public class DepartmentActivity extends AppCompatActivity {

    private Department department;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private DepartmentProfileFragment departmentProfileFragment;
    private InstructorsFragment instructorsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        setContentView(R.layout.activity_main);
        String departmentName = getIntent().getExtras().getString("department");
        department = db.getDepartmentFull(departmentName);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setTitle(departmentName);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setupViewPager(ViewPager viewPager) {

        Bundle args = new Bundle();
        args.putSerializable("department", department);
        instructorsFragment = new InstructorsFragment();
        instructorsFragment.setArguments(args);

        departmentProfileFragment = new DepartmentProfileFragment();
        departmentProfileFragment.setArguments(args);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(departmentProfileFragment, getString(R.string.title_overview));
        adapter.addFragment(instructorsFragment, getString(R.string.title_instructors));
        adapter.addFragment(new StalkerFragment(), getString(R.string.title_students));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
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

    public Department getDepartment() {
        return department;
    }


}
