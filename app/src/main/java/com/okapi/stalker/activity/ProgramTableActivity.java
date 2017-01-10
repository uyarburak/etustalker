package com.okapi.stalker.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;

import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Interval;
import com.okapi.stalker.data.storage.model.Section;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.codecrafters.tableview.SortableTableView;
import de.codecrafters.tableview.listeners.TableDataClickListener;
import de.codecrafters.tableview.toolkit.SimpleTableDataAdapter;
import de.codecrafters.tableview.toolkit.SimpleTableHeaderAdapter;
import de.codecrafters.tableview.toolkit.SortStateViewProviders;
import de.codecrafters.tableview.toolkit.TableDataRowBackgroundProviders;

/**
 * Created by burak on 1/2/2017.
 */
public class ProgramTableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_table);
        MainDataBaseHandler db = new MainDataBaseHandler(this);

        Map<List<Integer>, Integer> sectionsssIds = (Map<List<Integer>, Integer>) getIntent().getSerializableExtra("sections");
        Map<List<Section>, Integer> sectionss = new HashMap<List<Section>, Integer>();
        Map<Integer, Section> map = new HashMap<Integer, Section>();
        for(Map.Entry<List<Integer>, Integer> entry: sectionsssIds.entrySet()){
            List<Integer> list = entry.getKey();
            Integer value = entry.getValue();

            List<Section> listSection = new ArrayList<Section>(list.size());
            for(Integer sectionId: list){
                Section sec;
                if(map.containsKey(sectionId)){
                    sec = map.get(sectionId);
                }else{
                    sec = db.getSectionWithoutStudents(sectionId);
                    map.put(sectionId, sec);
                }
                listSection.add(sec);

            }
            sectionss.put(listSection, value);
        }

        SortableTableView<String[]> tableView = (SortableTableView<String[]>) findViewById(R.id.tableView);

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(this, getString(R.string.table_column_course_number), getString(R.string.table_column_conflict), getString(R.string.table_column_morning), getString(R.string.table_column_day));
        simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(this, R.color.table_header_text));
        tableView.setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());

        final int rowColorEven = ContextCompat.getColor(this, R.color.table_data_row_even);
        final int rowColorOdd = ContextCompat.getColor(this, R.color.table_data_row_odd);
        tableView.setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));

        tableView.setHeaderAdapter(simpleTableHeaderAdapter);

        String[][] array = new String[sectionss.size()][5];
        int i = 0;
        final List<List<Section>> listt = new ArrayList<>();
        for (Map.Entry<List<Section>, Integer> entry : sectionss.entrySet()) {
            final Set<Integer> gunler = new HashSet<Integer>();
            boolean[] wow = {false, false, false, false, false, false};
            for (Section section : entry.getKey()) {
                for (Interval interval : section.getIntervals()) {
                    gunler.add(interval.getDay());
                    if(interval.getHour() == 0){
                        wow[interval.getDay()] = true;
                    }
                }
            }
            Integer sabahciGunSayisi = 0;
            for(int j = 0; j<6;j++){
                if(wow[j]){
                    sabahciGunSayisi++;
                }
            }
            Integer gunSayisi = gunler.size();
            Integer dersSayisi = entry.getKey().size();
            Integer cakisma = entry.getValue();
            array[i] = new String[]{dersSayisi.toString(), cakisma.toString(), sabahciGunSayisi.toString(), gunSayisi.toString(), i+""};
            i++;
            listt.add(entry.getKey());
            //mappb.put(dersler, entry.getKey());

        }
        tableView.setDataAdapter(new SimpleTableDataAdapter(this, array));

        tableView.addDataClickListener(new TableDataClickListener<String[]>() {
            @Override
            public void onDataClicked(int rowIndex, String[] clickedData) {
                List<Section> sections = listt.get(Integer.parseInt(clickedData[4]));
                Intent intent = new Intent(getBaseContext(), ProgramActivity.class);
                intent.putExtra("sections", (Serializable) sections);
                startActivity(intent);
            }
        });

        tableView.setColumnComparator(0, new DersSayisiComparator());
        tableView.setColumnComparator(1, new CakismaComparator());
        tableView.setColumnComparator(2, new SabahComparator());
        tableView.setColumnComparator(3, new GunComparator());

    }

    private static class DersSayisiComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] one, String[] two) {
            Integer a = Integer.parseInt(one[0]);
            Integer b = Integer.parseInt(two[0]);
            return a.compareTo(b);
        }
    }
    private static class CakismaComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] one, String[] two) {
            Integer a = Integer.parseInt(one[1]);
            Integer b = Integer.parseInt(two[1]);
            return a.compareTo(b);
        }
    }
    private static class SabahComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] one, String[] two) {
            Integer a = Integer.parseInt(one[2]);
            Integer b = Integer.parseInt(two[2]);
            return a.compareTo(b);
        }
    }
    private static class GunComparator implements Comparator<String[]> {
        @Override
        public int compare(String[] one, String[] two) {
            Integer a = Integer.parseInt(one[3]);
            Integer b = Integer.parseInt(two[3]);
            return a.compareTo(b);
        }
    }
}
