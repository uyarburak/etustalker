package com.okapi.stalker.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.okapi.stalker.R;
import com.okapi.stalker.activity.adapter.ViewPagerAdapter;
import com.okapi.stalker.app.AppController;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.fragment.ProgramFragment;
import com.okapi.stalker.fragment.StudentProfileFragment;
import com.okapi.stalker.util.ParserX14;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class StudentActivity extends AppCompatActivity {

    private Student student;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private StudentProfileFragment studentProfileFragment;
    private ProgramFragment programFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        student = (Student) getIntent().getExtras().getSerializable("student");
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        student = db.getStudent(student.getId());
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setTitle(student.getName());
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setupViewPager(ViewPager viewPager) {

        programFragment = new ProgramFragment();
        programFragment.setOwner(student);
        studentProfileFragment = new StudentProfileFragment();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(studentProfileFragment, getString(R.string.title_profile));
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
            case R.id.action_show_midterms_of_student:
                Intent intent = new Intent(getBaseContext(), MidtermActivity.class);
                intent.putExtra("studentId", student.getId());
                startActivity(intent);
                return true;
            case R.id.action_show_finals_of_student:
                Intent intent2 = new Intent(getBaseContext(), FinalsActivity.class);
                intent2.putExtra("studentId", student.getId());
                startActivity(intent2);
                return true;
            case R.id.action_show_gpas_of_student:
                showGPA();
                return true;
            case R.id.action_settings:
                return true;
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * function to verify login details in mysql db
     * */
    private void showGPA() {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                "http://kayit.etu.edu.tr/rapor/x14.php", new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    ParserX14 parser = new ParserX14();
                    JSONObject obj = parser.parseIt(response);
                    JSONArray arr = (JSONArray)obj.get("departments");
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < arr.size(); i++){
                        JSONObject dep = (JSONObject)arr.get(i);
                        sb.append(dep.get("department") + "\n");
                        sb.append("GPA: " + dep.get("gpa")+"\n");
                        sb.append("Credit: " + dep.get("credit")+"\n");
                    }
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(StudentActivity.this);
                    dialogBuilder.setMessage(sb.toString());
                    //Create alert dialog object via builder
                    AlertDialog alertDialogObject = dialogBuilder.create();
                    //Show the dialog
                    alertDialogObject.show();
                } catch (Exception e) {
                    // JSON error
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("giris01", student.getId());
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, "gpa_show");
    }
    public Student getStudent() {
        return student;
    }
}
