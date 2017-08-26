package com.okapi.stalker.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.eggheadgames.siren.Siren;
import com.eggheadgames.siren.SirenAlertType;
import com.okapi.stalker.R;
import com.okapi.stalker.activity.adapter.ViewPagerAdapter;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.FriendsFragment;
import com.okapi.stalker.fragment.ProgramFragment;
import com.okapi.stalker.fragment.StalkerFragment;

public class MainActivity extends AppCompatActivity {
    public static final String SIREN_JSON_URL = "http://185.118.140.5/android/last_version.json";

    private Student student;
    private String user_student_key;
    private DrawerLayout drawerLayout;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private ProgramFragment programFragment;
    private StalkerFragment stalkerFragment;

    @Override
    protected void onPostResume() {
        super.onPostResume();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkCurrentAppVersion();

        setContentView(R.layout.activity_main_with_navigation);
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
            MainDataBaseHandler db = new MainDataBaseHandler(this);
            student = db.getStudent(user_student_key);
            View headerView = navView.getHeaderView(0);
            ((TextView) headerView.findViewById(R.id.header_user_name)).setText(student.getName());
            ((TextView) headerView.findViewById(R.id.header_user_id)).setText(student.getId());
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.nav_help:
                        Intent intent = new Intent(getBaseContext(), WebBrowserActivity.class);
                        intent.putExtra("url", "http://185.118.140.5/help");
                        intent.putExtra("title", getString(R.string.title_activity_help));
                        startActivity(intent);
                        break;
                    case R.id.nav_bus_program:
                        Intent intent14 = new Intent(getBaseContext(), DummyActivity.class);
                        startActivity(intent14);
                        break;
                    case R.id.nav_bus_program_old:
                        Intent intent2 = new Intent(getBaseContext(), WebBrowserActivity.class);
                        intent2.putExtra("url", "https://www.etu.edu.tr/tr/ulasim");
                        intent2.putExtra("title", getString(R.string.title_activity_bus));
                        startActivity(intent2);
                        break;
                    case R.id.nav_gpa_calculator:
                        Intent intent3 = new Intent(getBaseContext(), GPAActivity.class);
                        intent3.putExtra("studentId", user_student_key);
                        startActivity(intent3);
                        break;
                    case R.id.nav_instructors:
                        Intent intent4 = new Intent(getBaseContext(), ListActivity.class);
                        intent4.putExtra("listType", ListActivity.INSTRUCTOR_LIST);
                        startActivity(intent4);
                        break;
                    case R.id.nav_courses:
                        Intent intent5 = new Intent(getBaseContext(), ListActivity.class);
                        intent5.putExtra("listType", ListActivity.COURSE_LIST);
                        startActivity(intent5);
                        break;
                    case R.id.nav_program_scheduler:
                        Intent intent6 = new Intent(getBaseContext(), ProgramSchedulerActivity.class);
                        startActivity(intent6);
                        //Toast.makeText(getBaseContext(), getString(R.string.not_ready_yet), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_about_us:
                        Intent intent7 = new Intent(getBaseContext(), AboutActivity.class);
                        startActivity(intent7);
                        break;
                    case R.id.nav_midterm_schedule:
                        Intent intent8 = new Intent(getBaseContext(), MidtermActivity.class);
                        intent8.putExtra("studentId", student.getId());
                        startActivity(intent8);
                        break;
                    case R.id.nav_departments:
                        Intent intent9 = new Intent(getBaseContext(), ListActivity.class);
                        intent9.putExtra("listType", ListActivity.DEPARTMENT_LIST);
                        startActivity(intent9);
                        break;
                    case R.id.nav_facts:
                        Toast.makeText(getBaseContext(), getString(R.string.not_ready_yet), Toast.LENGTH_LONG).show();
                        break;
                    case R.id.nav_free_rooms:
                        Intent intent11 = new Intent(getBaseContext(), FreeRoomsActivity.class);
                        startActivity(intent11);
                        break;
                    case R.id.nav_final_schedule:
                        Intent intent13 = new Intent(getBaseContext(), FinalsActivity.class);
                        intent13.putExtra("studentId", student.getId());
                        startActivity(intent13);
                        break;
                    case R.id.nav_feedback:
                        Intent intent10 = new Intent(getBaseContext(), WebBrowserActivity.class);
                        intent10.putExtra("url", "http://185.118.140.5/feedback");
                        intent10.putExtra("title", getString(R.string.title_activity_feedback));
                        startActivity(intent10);
                        break;
                    case R.id.nav_academic_calendar:
                        Intent intent15 = new Intent(getBaseContext(), WebBrowserActivity.class);
                        intent15.putExtra("url", "https://www.etu.edu.tr/tr/akademik-takvim");
                        intent15.putExtra("title", getString(R.string.academic_calendar));
                        startActivity(intent15);
                        break;
                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        student = db.getStudent(user_student_key);
        programFragment = new ProgramFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("owner", student);
        programFragment.setArguments(bundle);
        stalkerFragment = new StalkerFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(programFragment, getString(R.string.title_program));
        adapter.addFragment(new FriendsFragment(), getString(R.string.title_friends));
        adapter.addFragment(stalkerFragment, getString(R.string.title_stalker));
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);

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
            Intent intent = new Intent(MainActivity.this, PrefsActivity.class);
            startActivity(intent);
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


    private void checkCurrentAppVersion() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        int checkPeriod = Integer.parseInt(sharedPreferences.getString("list_auto_update_check_cycle", "1"));
        Siren siren = Siren.getInstance(getApplicationContext());
        siren.setMajorUpdateAlertType(SirenAlertType.FORCE);
        siren.setVersionCodeUpdateAlertType(SirenAlertType.SKIP);
        siren.checkVersion(this, checkPeriod, SIREN_JSON_URL);
    }
}
