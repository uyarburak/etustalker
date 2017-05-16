package com.okapi.stalker.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.adapter.ViewPagerAdapter;
import com.okapi.stalker.data.storage.model.BusSchedule;
import com.okapi.stalker.fragment.CourseProfileFragment;
import com.okapi.stalker.fragment.CourseSectionsFragment;
import com.okapi.stalker.fragment.adapters.BusScheduleAdapter;
import com.okapi.stalker.fragment.adapters.DistrictServiceAdapter;
import com.okapi.stalker.util.RecyclerItemClickListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 * Created by burak on 5/16/2017.
 */

public class BusScheduleActivity extends AppCompatActivity {
    private String html;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    private CourseProfileFragment courseProfileFragment;
    private CourseSectionsFragment courseSectionsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        html = getIntent().getExtras().getString("html");
        setContentView(R.layout.activity_main);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        final ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        ab.setDisplayHomeAsUpEnabled(true);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        getSupportActionBar().setTitle(getString(R.string.title_activity_bus));
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    private void setupViewPager(ViewPager viewPager) {

        Bundle args = new Bundle();
        args.putInt("index", 0);
        args.putString("html", html);
        BusFragment1 fragment0 = new BusFragment1();
        fragment0.setArguments(args);
        BusFragment1 fragment1 = new BusFragment1();
        args.putInt("index", 1);
        fragment1.setArguments(args);
        BusFragment1 fragment2 = new BusFragment1();
        args.putInt("index", 2);
        fragment2.setArguments(args);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(fragment0, getString(R.string.bus_district_bus));
        adapter.addFragment(fragment1, getString(R.string.bus_ring));
        adapter.addFragment(fragment2, getString(R.string.bus_saturday));
        viewPager.setAdapter(adapter);

        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date(System.currentTimeMillis()));
        if(cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
            viewPager.setCurrentItem(2);
        }
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

    public static class BusFragment1 extends Fragment {
        int index;
        String html;
        public BusFragment1() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
        }

        @Override
        public void setArguments(Bundle args) {
            super.setArguments(args);
            this.index = args.getInt("index");
            this.html = args.getString("html");
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_bus_schedule, container, false);
            final RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.bus_schedule_recycler);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            RecyclerView.Adapter adapter = null;
            if (this.index > 0) {
                List<BusSchedule> busses = parse(this.index);
                adapter = new BusScheduleAdapter(busses);

                // Saati yaklasan servise scroll etsin
                Calendar cal = Calendar.getInstance();
                cal.setTime(new Date(System.currentTimeMillis()));
                int minutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
                int i = 0;
                for(BusSchedule bus: busses){
                    String[] parts = bus.getTime().split(":");
                    int busMinutes = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]);
                    if(busMinutes >= minutes)
                        break;
                    i++;
                }
                if(i <= busses.size())
                    recyclerView.scrollToPosition(i);

            }
            else {
                adapter = new DistrictServiceAdapter(parse2());
                recyclerView.addOnItemTouchListener(
                        new RecyclerItemClickListener(getContext(), recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                DistrictServiceAdapter adapter = (DistrictServiceAdapter)recyclerView.getAdapter();
                                BusSchedule bus = adapter.getItem(position);
                                if(bus.getRouteURL() != null && bus.getRouteURL().length() > 0){
                                    Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                            Uri.parse(bus.getRouteURL()));
                                    startActivity(intent);
                                }
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                // do whatever
                            }
                        })
                );
            }
            recyclerView.setAdapter(adapter);
            return rootView;
        }
        private List<BusSchedule> parse(int index){
            List<BusSchedule> busses = new ArrayList<BusSchedule>();
            String last = "";
            Document doc = Jsoup.parse(html);
            Element table = doc.getElementsByTag("table").get(index);

            Elements elements = table.getElementsByTag("tr");
            Iterator<Element> iter = elements.iterator();
            iter.next();
            while(iter.hasNext()) {
                Element element = iter.next();
                Elements atts = element.getElementsByTag("td");
                BusSchedule bus = new BusSchedule();
                bus.setTime(atts.get(1).text());
                bus.setFrom(atts.get(2).text());
                bus.setTo(atts.get(3).text());
                if(bus.toString().equals(last))
                    continue;
                busses.add(bus);
                last = bus.toString();
            }
            return busses;
        }
        private List<BusSchedule> parse2(){
            List<BusSchedule> busses = new ArrayList<BusSchedule>();
            Document doc = Jsoup.parse(html);
            Element table = doc.getElementsByTag("table").get(0);

            Elements elements = table.getElementsByTag("tr");
            Iterator<Element> iter = elements.iterator();
            iter.next();
            int x;
            while(iter.hasNext()) {
                Element element = iter.next();
                Elements atts = element.getElementsByTag("td");
                BusSchedule bus = new BusSchedule();
                bus.setFrom(atts.get(1).text());
                if(!atts.get(2).getElementsByAttribute("href").isEmpty())
                    bus.setRouteURL(atts.get(2).getElementsByAttribute("href").first().attr("href"));
                busses.add(bus);
            }
            return busses;
        }
    }
}