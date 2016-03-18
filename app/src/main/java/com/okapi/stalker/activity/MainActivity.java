package com.okapi.stalker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.adapter.ViewPagerAdapter;
import com.okapi.stalker.data.storage.Stash;
import com.okapi.stalker.data.storage.type.Student;
import com.okapi.stalker.fragment.FriendsFragment;
import com.okapi.stalker.fragment.ProgramFragment;
import com.okapi.stalker.fragment.StalkerFragment;

public class MainActivity extends AppCompatActivity {

    private Student student;
    private String user_student_key;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ProgramFragment programFragment;
    private StalkerFragment stalkerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        user_student_key = getIntent().getExtras().getString("key");

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        if (navView != null) {
            setupDrawerContent(navView);
        }

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        {
            Stash stash = Stash.get();
            student = stash.getStudent(user_student_key);
            View headerView = navView.getHeaderView(0);
            ((TextView) headerView.findViewById(R.id.header_user_name)).setText(student.name);
            ((TextView) headerView.findViewById(R.id.header_user_id)).setText(student.id);
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.nav_first:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.nav_second:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.nav_third:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.nav_forth:
                        viewPager.setCurrentItem(3);
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        Stash stash = Stash.get();
        student = stash.getStudent(user_student_key);
        programFragment = new ProgramFragment();
        programFragment.setSectionKeys(student.sectionKeys);
        stalkerFragment = new StalkerFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(programFragment, getString(R.string.title_program));
        adapter.addFragment(new FriendsFragment(), getString(R.string.title_friends));
        adapter.addFragment(stalkerFragment, getString(R.string.title_stalker));
        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.isEmpty() && viewPager.getCurrentItem() != 2)
                    viewPager.setCurrentItem(2);
                stalkerFragment.onQueryTextChange(newText);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_exit) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(getBaseContext()).edit();
            editor.remove("id");
            editor.commit();
            Intent intent = new Intent(getBaseContext(), LoginActivity.class);
            startActivity(intent);
            finish();
        }

        switch (id) {
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
