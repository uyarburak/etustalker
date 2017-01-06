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
import android.preference.PreferenceManager;
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
public class FinalsActivity extends AppCompatActivity {

    public static int status;

    private String studentId;
    Map<String, Integer> studentsCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentsCourses = new HashMap<>();
        setContentView(R.layout.activity_midterms);
        studentId = getIntent().getExtras().getString("studentId");

        setTitle(getString(R.string.final_title) + " " + studentId);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeButtonEnabled(true);

        perseFinals(studentId);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_final, menu);
        return true;
    }
    private void perseFinals(String studentId) {
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        for (Section section: db.getSectionsOfStudent(studentId)){
            section = db.getSectionWithoutStudents(section.getId());
            studentsCourses.put(section.getCourse().getCode(), section.getSectionNo());
        }
        new FinalParse(this).execute();
    }

    private void parseDone(List<Final> finals){
        PinnedSectionListView midtermListView = (PinnedSectionListView)findViewById(R.id.midterm_listview);
        FinalAdapter adapter = new FinalAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, finals);
        midtermListView.setAdapter(adapter);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        switch (menuItem.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_show_final_manual:
                Intent intent = new Intent(getBaseContext(), WebBrowserActivity.class);
                intent.putExtra("url", "http://kayit.etu.edu.tr/final/_Final_prg_start.php");
                intent.putExtra("title", getString(R.string.final_title)+" (ETU-BIS)");
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private class FinalParse extends AsyncTask<String, Void, Integer> {
        public static final int NO_CONNECTION = 1;

        private ProgressDialog dialog;
        private Activity activity;
        private SharedPreferences sharedPreferences;
        private String html;
        @Override
        protected void onPostExecute(Integer aVoid) {
            super.onPostExecute(aVoid);
            if(!finals.isEmpty()){
                parseDone(finals);
                dialog.dismiss();
                return;
            }
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            AlertDialog alertDialog = new AlertDialog.Builder(FinalsActivity.this).create();
            if(aVoid != null && aVoid == NO_CONNECTION){
                alertDialog.setTitle(getString(R.string.alert));
                if(sharedPreferences.contains(studentId+"time")){
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    Date date = new Date(sharedPreferences.getLong(studentId+"time", 0));
                    alertDialog.setMessage(getString(R.string.connection_error, format.format(date).toString()));
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    new FinalParse(activity, sharedPreferences.getString(studentId+"html", "")).execute();
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
                alertDialog.setMessage(getString(R.string.cannot_find_final));
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
                                intent.putExtra("url", "http://kayit.etu.edu.tr/final/_Final_prg_start.php");
                                intent.putExtra("title", getString(R.string.final_title)+"(ETU-BIS)");
                                startActivity(intent);
                            }
                        });
            }

            alertDialog.show();
        }

        public FinalParse(Activity activity) {
            super();
            this.activity = activity;
            dialog = new ProgressDialog(activity);
            finals = new ArrayList<>();
            sharedPreferences = getSharedPreferences("finals_cache", MODE_PRIVATE);
        }
        public FinalParse(Activity activity, String html) {
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
            mPrefsEditor.putString(studentId+"html", html);
            mPrefsEditor.putLong(studentId+"time", System.currentTimeMillis());
            mPrefsEditor.commit();
        }

        List<Final> finals;
        @Override
        protected Integer doInBackground(String... params) {
            Document doc;
            if(html == null){
                try {
                    doc  = Jsoup.connect("http://kayit.etu.edu.tr/final/Final_prg.php")
                            .data("ogrencino", studentId)
                            .data("btn_ogrenci", "Programı Göster")
                            .post();
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

            String[] hours;
            {
                Element info = rows.get(0);
                Elements cols = info.select("th");
                hours = new String[cols.size() - 1];
                for (int i = 0; i < hours.length; i++){
                    hours[i] = cols.get(i+1).text().trim();
                }

            }

            for (int i = 1; i < rows.size(); i++) { //first row is the col names so skip it.
                Element row = rows.get(i);
                Element date = row.select("th").get(0);
                Elements cols = row.select("td");
                for (int j = 0; j < hours.length; j++){
                    String text = cols.get(j).html().trim();
                    if(text.length() < 3)
                        continue;
                    Final myFinal = new Final();
                    myFinal.date = date.text().trim();
                    String[] split = text.split("<br>");
                    Final pinnedFinal = new Final(1);
                    myFinal.room = split[0].substring(split[0].indexOf(" ") + 1);
                    pinnedFinal.courseCode = split[1].substring(split[1].indexOf(" ") + 1);
                    pinnedFinal.sectionNo = split[2].substring(split[2].indexOf(" ") + 1);
                    myFinal.hour = hours[j];
                    finals.add(pinnedFinal);
                    finals.add(myFinal);

                }
            }
            return null;
        }
    }
}
class Final{
    String courseCode;
    String sectionNo;
    String room;
    String date;
    String hour;
    int pinned;

    public Final(){
        this(0);
    }
    public Final(int pinned){
        this.pinned = pinned;
    }
    public Final(String courseCode, String sectionNo){
        this(1);
        this.courseCode = courseCode;
        this.sectionNo = sectionNo;
    }

    @Override
    public String toString() {
        return "Final{" +
                "courseCode='" + courseCode + '\'' +
                ", sectionNo='" + sectionNo + '\'' +
                ", room='" + room + '\'' +
                ", date='" + date + '\'' +
                ", hour='" + hour + '\'' +
                ", pinned=" + pinned +
                '}';
    }
}
class FinalAdapter extends ArrayAdapter<Final> implements PinnedSectionListView.PinnedSectionListAdapter {
    List<Final> finals;
    ColorGenerator cg;

    public FinalAdapter(Context context, int resource, int textViewResourceId, List<Final> finals) {
        super(context, resource, textViewResourceId);
        this.finals = finals;

        cg = ColorGenerator.MATERIAL;
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
        Final item = getItem(position);
        if (isItemViewTypePinned(item.pinned)) {
            //view.setOnClickListener(PinnedSectionListActivity.this);
            view.setBackgroundColor(cg.getColor(item.courseCode));
            view.setText(item.courseCode);
        }else{
            view.setText(item.date + " (" + item.hour + ") - @" + item.room);
            view.setBackgroundColor(Color.WHITE);
        }
        return view;
    }

    @Override
    public Final getItem(int position) {
        return finals.get(position);
    }

    @Override
    public int getCount() {
        return finals.size();
    }
}

