package com.okapi.stalker.activity;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.halfbit.pinnedsection.PinnedSectionListView;

/**
 * Created by burak on 10/7/2016.
 */
public class MidtermActivity extends AppCompatActivity {

    private String studentId;
    Map<String, Integer> studentsCourses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        studentsCourses = new HashMap<>();
        setContentView(R.layout.activity_midterms);
        studentId = getIntent().getExtras().getString("studentId");

        parseMidterms(studentId);

    }

    private void parseMidterms(String studentId) {
        MainDataBaseHandler db = new MainDataBaseHandler(this);
        for (Section section: db.getSectionsOfStudent(studentId)){
            section = db.getSectionWithoutStudents(section.getId());
            studentsCourses.put(section.getCourse().getCode(), section.getSectionNo());
        }
        new MidtermParse().execute();
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
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }

    private class MidtermParse extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            parseDone(midterms);
        }

        List<Midterm> midterms;
        @Override
        protected Void doInBackground(Void... params) {
            midterms = new ArrayList<>();
            Document doc = null;
            try {
                doc  = Jsoup.connect("http://kayit.etu.edu.tr/ara_sinav_programi.php").get();
            } catch (IOException e) {
                e.printStackTrace();
            }
            doc = new Cleaner(Whitelist.relaxed()).clean(doc);

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

    public MidtermAdapter(Context context, int resource, int textViewResourceId, List<Midterm> midterms) {
        super(context, resource, textViewResourceId);
        this.midterms = midterms;

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
        Midterm item = getItem(position);
        if (isItemViewTypePinned(item.pinned)) {
            //view.setOnClickListener(PinnedSectionListActivity.this);
            view.setBackgroundColor(cg.getColor(item));
            view.setText(item.courseCode + " - " + item.sectionNo + " - " + item.courseTitle);
        }else{
            view.setText(item.date + " (" + item.day + ") - " + item.hour + " - @" + item.room);
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

