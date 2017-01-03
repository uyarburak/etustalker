package com.okapi.stalker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.util.ColorGenerator;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Cleaner;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * Created by burak on 10/7/2016.
 */
public class MidtermActivity extends AppCompatActivity {

    public static int status;

    private String studentId;
    Map<String, Integer> studentsCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentsCourses = new HashMap<>();
        setContentView(R.layout.activity_midterms);
        studentId = getIntent().getExtras().getString("studentId");

        setTitle(getString(R.string.midterms_title));
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        parseMidterms(studentId);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_midterm, menu);
        return true;
    }
    private void parseMidterms(String studentId) {
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        for (Section section: db.getSectionsOfStudent(studentId)){
            section = db.getSectionWithoutStudents(section.getId());
            studentsCourses.put(section.getCourse().getCode(), section.getSectionNo());
        }
        new MidtermParse(this).execute();
    }

    private void parseDone(List<Midterm> midterms){
        PinnedSectionListView midtermListView = (PinnedSectionListView)findViewById(R.id.midterm_listview);
        MidtermAdapter adapter = new MidtermAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, midterms);
        midtermListView.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_show_all_midterms:
                Intent intent = new Intent(getBaseContext(), WebBrowserActivity.class);
                intent.putExtra("url", "http://kayit.etu.edu.tr/ara_sinav_programi.php");
                intent.putExtra("title", getString(R.string.midterms_title) + " (ETU-BIS)");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private class MidtermParse extends AsyncTask<String, Void, Integer> {
        public static final int NO_CONNECTION = 1;

        private ProgressDialog dialog;
        private Activity activity;
        private SharedPreferences sharedPreferences;
        private String html;
        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            if(!midterms.isEmpty()){
                parseDone(midterms);
                dialog.dismiss();
                return;
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            AlertDialog alertDialog = new AlertDialog.Builder(MidtermActivity.this).create();
            if(aVoid != null && aVoid == NO_CONNECTION){
                alertDialog.setTitle(getString(R.string.alert));
                if(sharedPreferences.contains("time")){
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date(sharedPreferences.getLong("time", 0));
                    alertDialog.setMessage(getString(R.string.connection_error, format.format(date).toString()));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    new MidtermParse(activity, sharedPreferences.getString("html", "")).execute();
                                }
                            });
                }else{

                    alertDialog.setMessage(getString(R.string.connection_error_no_cache));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    activity.finish();
                                }
                            });
                }
            }else{
                alertDialog.setTitle(getString(R.string.alert));
                alertDialog.setMessage(getString(R.string.cannot_find_midterm));
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.not_now),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.go_etu_bis),
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                Intent intent = new Intent(activity, WebBrowserActivity.class);
                                intent.putExtra("url", "http://kayit.etu.edu.tr/ara_sinav_programi.php");
                                intent.putExtra("title", getString(R.string.midterms_title) + " (ETU-BIS)");
                                startActivity(intent);
                            }
                        });
            }

            alertDialog.show();
        }

        public MidtermParse(Activity activity) {
            super();
            this.activity = activity;
            dialog = new ProgressDialog(activity);
            midterms = new ArrayList<>();
            sharedPreferences = getSharedPreferences("midterms_cache", MODE_PRIVATE);
        }
        public MidtermParse(Activity activity, String html) {
            this(activity);
            this.html = html;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage(getString(R.string.executing));
            dialog.show();
        }

        private void updateCache(String html){
            SharedPreferences.Editor mPrefsEditor = sharedPreferences.edit();
            mPrefsEditor.putString("html", html);
            mPrefsEditor.putLong("time", System.currentTimeMillis());
            mPrefsEditor.commit();
        }

        List<Midterm> midterms;
        @Override
        protected Integer doInBackground(String... params) {
            Document doc;
            if(html == null){
                try {
                    doc  = Jsoup.connect("http://kayit.etu.edu.tr/ara_sinav_programi.php").get();
                } catch (IOException e) {
                    e.printStackTrace();
                    return NO_CONNECTION;
                }
                doc = new Cleaner(Whitelist.relaxed()).clean(doc);
                updateCache(doc.toString());
            }else{
                doc = Jsoup.parse(html);
            }

            if(doc == null) return NO_CONNECTION;
            Element table = doc.select("table").get(0); //select the first table.
            Elements rows = table.select("tr");

            Midterm lastMidterm = null;
            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Elements cols = row.select("td");
                Midterm midterm = new Midterm();
                midterm.courseCode = cols.get(0).text();
                if(!studentsCourses.containsKey(midterm.courseCode))
                    continue;
                midterm.sectionNo = cols.get(2).text();
                midterm.courseTitle = cols.get(1).text();
                midterm.room = cols.get(4).text();
                midterm.date = cols.get(5).text();
                midterm.day = cols.get(6).text();
                midterm.hour = cols.get(7).text();
                midterm.observer = cols.get(8).text();
                if(lastMidterm == null || !lastMidterm.courseCode.equals(midterm.courseCode)){
                    midterms.add(new Midterm(midterm.courseCode, midterm.courseTitle, midterm.sectionNo));
                }else if(lastMidterm.date.equals(midterm.date) && lastMidterm.hour.equals(midterm.hour)){
                    if(!lastMidterm.room.contains(midterm.room))
                        lastMidterm.room = lastMidterm.room.concat(", ").concat(midterm.room);
                    continue;
                }
                midterms.add(midterm);
                lastMidterm = midterm;
            }
            return null;
        }
    }
}
class Midterm{
    String courseCode;
    String courseTitle;
    String sectionNo;
    String room;
    String date;
    String day;
    String hour;
    String observer;
    int pinned;

    public Midterm(){
        this(0);
    }
    public Midterm(int pinned){
        this.pinned = pinned;
    }
    public Midterm(String courseCode, String courseTitle, String sectionNo){
        this(1);
        this.courseCode = courseCode;
        this.courseTitle = courseTitle;
        this.sectionNo = sectionNo;
    }

    @Override
    public String toString() {
        return "Midterm{" +
                "courseCode='" + courseCode + '\'' +
                ", courseTitle='" + courseTitle + '\'' +
                ", sectionNo='" + sectionNo + '\'' +
                ", room='" + room + '\'' +
                ", date='" + date + '\'' +
                ", day='" + day + '\'' +
                ", hour='" + hour + '\'' +
                ", observer='" + observer + '\'' +
                ", pinned=" + pinned +
                '}';
    }
}
class MidtermAdapter extends ArrayAdapter<Midterm> implements PinnedSectionListView.PinnedSectionListAdapter {
    List<Midterm> midterms;
    ColorGenerator cg;
    DateFormat sourceFormat;

    public MidtermAdapter(Context context, int resource, int textViewResourceId, List<Midterm> midterms) {
        super(context, resource, textViewResourceId);
        this.midterms = midterms;

        cg = ColorGenerator.MATERIAL;
        sourceFormat = new SimpleDateFormat("dd.MM.yyyy");
    }

    @Override
    public boolean isItemViewTypePinned(int viewType) {
        return viewType == 1;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override public View getView(int position, View convertView, ViewGroup parent) {
        TextView view = (TextView) super.getView(position, convertView, parent);
        view.setTextColor(Color.DKGRAY);
        view.setTag("" + position);
        Midterm item = getItem(position);
        if (isItemViewTypePinned(item.pinned)) {
            //view.setOnClickListener(PinnedSectionListActivity.this);
            view.setBackgroundColor(cg.getColor(item.courseTitle));
            view.setText(item.courseCode + " - " + item.courseTitle);
        }else{
            view.setText(item.date + " (" + item.day + ") - " + item.hour + " - @" + item.room);
            Date date = null;
            try {
                date = sourceFormat.parse(item.date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(date != null && date.compareTo(new Date(System.currentTimeMillis() - 86400000)) == -1){
                view.setBackgroundColor(Color.LTGRAY);
            }else{
                view.setBackgroundColor(Color.WHITE);
            }
        }
        return view;
    }

    @Override
    public Midterm getItem(int position) {
        return midterms.get(position);
    }

    @Override
    public int getCount() {
        return midterms.size();
    }
}

