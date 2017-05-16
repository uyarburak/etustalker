package com.okapi.stalker.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filterable;

import com.okapi.stalker.R;
import com.okapi.stalker.activity.CourseActivity;
import com.okapi.stalker.activity.DepartmentActivity;
import com.okapi.stalker.activity.MainActivity;
import com.okapi.stalker.activity.SectionActivity;
import com.okapi.stalker.activity.StudentActivity;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.data.storage.model.Course;
import com.okapi.stalker.data.storage.model.Department;
import com.okapi.stalker.data.storage.model.Person;
import com.okapi.stalker.data.storage.model.Section;
import com.okapi.stalker.data.storage.model.Student;
import com.okapi.stalker.data.storage.model.Tag;
import com.okapi.stalker.fragment.adapters.StalkerRecylerAdapter;
import com.okapi.stalker.fragment.comparators.AbstractComparator;
import com.okapi.stalker.fragment.comparators.DepartmentComparator;
import com.okapi.stalker.fragment.comparators.IdComparator;
import com.okapi.stalker.fragment.comparators.NameComparator;
import com.okapi.stalker.fragment.comparators.SexComparator;
import com.okapi.stalker.util.ColorGenerator;
import com.okapi.stalker.util.RecyclerItemClickListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.yalantis.filter.adapter.FilterAdapter;
import com.yalantis.filter.listener.FilterListener;
import com.yalantis.filter.widget.Filter;
import com.yalantis.filter.widget.FilterItem;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.text.Collator;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class StalkerFragment extends Fragment implements FilterListener<Tag>, Filterable {
    public enum OrderBy{NONE, NAME, ID, SEX, DEPARTMENT};
    public enum SearchingType{NAME, ID};
    private OrderBy orderBy;

    Filter<Tag> mFilter;
    Map<String, Tag> tags;
    private StalkerRecylerAdapter mAdapter;
    private View rootView;
    private Set<Student> students;
    private Set<Student> filteredStudents;
    private List<Student> studentList;
    ColorGenerator generator;
    private String lastSearch = "";
    private String preSearch;

    ProgressDialog dialog;

    FastScrollRecyclerView recyclerView;
    public StalkerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dialog = new ProgressDialog(getContext());

        //myStalkerAdapter = new MyStalkerAdapter(getActivity(), students);
        mAdapter = new StalkerRecylerAdapter(getContext(), new ArrayList<Student>(1));
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if(rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_stalker_recy, container, false);

            mFilter = (Filter<Tag>) rootView.findViewById(R.id.filter);

            recyclerView = (FastScrollRecyclerView) rootView.findViewById(R.id.recy_stalker_list);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(mAdapter);
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            StalkerRecylerAdapter adapter = (StalkerRecylerAdapter)recyclerView.getAdapter();
                            adapter.setDiscovered(position);
                            Intent intent = new Intent(getActivity(), StudentActivity.class);
                            intent.putExtra("student", (Serializable) adapter.getQuestions().get(position));
                            getActivity().startActivity(intent);
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );

            new LoadListTask(this).execute();
        }
        return rootView;
    }

    private List<Tag> getTags() {
        List<Tag> mTags = new ArrayList<>();
        mTags.add(new Tag(getString(R.string.filter_everyone)));
        for(Tag tag: tags.values()){
            mTags.add(tag);
        }
        return mTags;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id){
            case R.id.action_sort_by_name:
                sort(OrderBy.NAME);
                break;
            case R.id.action_sort_by_id:
                sort(OrderBy.ID);
                break;
            case R.id.action_sort_by_sex:
                sort(OrderBy.SEX);
                break;
            case R.id.action_sort_by_department:
                sort(OrderBy.DEPARTMENT);
                break;
            case R.id.action_stats:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                        getContext());
                int male , female, unisex;
                male = female = unisex = 0;
                for (Student student : mAdapter.getQuestions()){
                    switch (student.getGender()) {
                        case 'M':
                            male++;
                            break;
                        case 'F':
                            female++;
                            break;
                        case 'U':
                            unisex++;
                            break;
                    }
                }
                DecimalFormat df = new DecimalFormat("0.##");
                float total = male + female + unisex;
                StringBuilder sb = new StringBuilder();
                sb.append(getString(R.string.male) + ": ").append(male).append(" (").append(df.format(male/total * 100)).append("%)\n");
                sb.append(getString(R.string.female)+ ": ").append(female).append(" (").append(df.format(female/total * 100)).append("%)\n");
                sb.append(getString(R.string.unisex)+ ": ").append(unisex).append(" (").append(df.format(unisex/total * 100)).append("%)\n");
                sb.append(getString(R.string.total)+ ": ").append(male + female + unisex).append("\n");
                alertDialogBuilder.setTitle(getString(R.string.stats));
                alertDialogBuilder.setMessage(sb.toString());
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
        }
        return true;

    }



    public void sort(OrderBy order){
        AbstractComparator.carpan = orderBy == order ? -1 : 1;
        Set<Student> sortedSet;
        mAdapter.searchingType = SearchingType.NAME;
        switch (order){
            case NAME:
                sortedSet = new TreeSet<Student>(new NameComparator());
                break;
            case ID:
                sortedSet = new TreeSet<Student>(new IdComparator());
                mAdapter.searchingType = SearchingType.ID;
                break;
            case SEX:
                sortedSet = new TreeSet<Student>(new SexComparator());
                break;
            case DEPARTMENT:
                sortedSet = new TreeSet<Student>(new DepartmentComparator());
                break;
            default:
                sortedSet = new TreeSet<Student>(new NameComparator());
        }
        sortedSet.addAll(filteredStudents);
        filteredStudents = sortedSet;
        studentList = filter(false);
        mAdapter.setQuestions(studentList);
        orderBy = order;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.stalker_fragment_menu, menu);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.action_search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                StalkerFragment.this.getFilter().filter(newText);
                return false;
            }
        });


    }

    private void calculateDiff(final List<Student> oldList, final List<Student> newList) {
        DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return oldList.size();
            }

            @Override
            public int getNewListSize() {
                return newList.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
            }
        }).dispatchUpdatesTo(mAdapter);
    }


    @Override
    public void onNothingSelected() {
        if (recyclerView != null) {
            filteredStudents.clear();
            filteredStudents.addAll(students);
            mAdapter.setQuestions(filter(true));
        }

        Log.e("onNothingSelected", "zaa");
    }

    private List<Student> findByTags(List<Tag> tags) {
        List<Student> newList = new ArrayList<>();
        List<Tag> typless = new ArrayList<>();
        List<Tag> gender = new ArrayList<>();
        List<Tag> year = new ArrayList<>();
        List<Tag> enterYear = new ArrayList<>();
        List<Tag> department = new ArrayList<>();
        List<Tag> active = new ArrayList<>();
        for (Tag tag : tags) {
            switch (tag.getTagType()){
                case GENDER:
                    gender.add(tag);
                    break;
                case YEAR:
                    year.add(tag);
                    break;
                case ENTER_YEAR:
                    enterYear.add(tag);
                    break;
                case DEPARTMENT:
                    department.add(tag);
                    break;
                case ACTIVITY:
                    active.add(tag);
                    break;
                default:
                    typless.add(tag);
                    break;
            }
        }

        for (Student student : students) {
            if(hasTagAny(gender, student) && hasTagAny(year, student) && hasTagAny(enterYear, student) && hasTagAny(department, student) && hasTagAny(active, student))
                newList.add(student);
        }

        return newList;
    }
    private boolean hasTagAny(List<Tag> tags, Student student){
        if(tags.isEmpty()) return true;
        for (Tag tag : tags) {
            if (student.hasTag(tag.getText())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onFiltersSelected(@NotNull ArrayList<Tag> filters) {
        List<Student> newQuestions = findByTags(filters);
        filteredStudents.clear();
        filteredStudents.addAll(newQuestions);
        mAdapter.setQuestions(filter(true));
        Log.e("onFiltersSelected", filters.toString());
    }

    @Override
    public void onFilterSelected(Tag item) {
        if (item.getText().equals(getString(R.string.filter_everyone))) {
            mFilter.deselectAll();
            mFilter.collapse();
            filteredStudents.clear();
            filteredStudents.addAll(students);
            mAdapter.setQuestions(filter(true));
            Log.e("onFilter", "ICERDE");
        }
        Log.e("onFilter", item.getText());
    }


    @Override
    public void onFilterDeselected(Tag tag) {
        Log.e("onFilterDeselected", tag.getText());
    }

    @Override
    public android.widget.Filter getFilter() {

        android.widget.Filter filter = new android.widget.Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                studentList = (List<Student>) results.values;
                mAdapter.setQuestions(studentList);
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                preSearch = lastSearch;
                lastSearch = constraint.toString().trim();
                List<Student> filteredArrayNames = StalkerFragment.this.filter(false);
                results.count = filteredArrayNames.size();
                results.values = filteredArrayNames;

                return results;
            }
        };

        return filter;
    }

    private void resetTags(List<Student> students){
        Tag maleTag = new Tag(getString(R.string.filter_male), Color.RED, Tag.TagType.GENDER);
        Tag femaleTag = new Tag(getString(R.string.filter_female), Color.BLUE, Tag.TagType.GENDER);
        Tag unisexTag = new Tag(getString(R.string.filter_unisex), Color.GREEN, Tag.TagType.GENDER);
        tags = new LinkedHashMap<String, Tag>();
        tags.put(maleTag.getText(), maleTag);
        tags.put(femaleTag.getText(), femaleTag);
        tags.put(unisexTag.getText(), unisexTag);
        generator = ColorGenerator.MATERIAL;
        if(getActivity() instanceof MainActivity || getActivity() instanceof DepartmentActivity){
            Tag active = new Tag(getString(R.string.filter_active), Color.GREEN, Tag.TagType.ACTIVITY);
            Tag inactive = new Tag(getString(R.string.filter_inactive), Color.GRAY, Tag.TagType.ACTIVITY);
            tags.put(active.getText(), active);
            tags.put(inactive.getText(), inactive);
            for(Student student: students) {
                if(student.getActive()) {
                    student.addTag(active);
                }else{
                    student.addTag(inactive);
                }
            }
        }
        String yearPrefix = getString(R.string.filter_year) + ": ";
        for(Student student: students) {
            switch (student.getGender()) {
                case 'M':
                    student.addTag(maleTag);
                    break;
                case 'F':
                    student.addTag(femaleTag);
                    break;
                default:
                    student.addTag(unisexTag);
                    break;
            }
            String enterYear = "20" + student.getId().charAt(0) + student.getId().charAt(1);
            if (tags.containsKey(enterYear)) {
                student.addTag(tags.get(enterYear));
            } else {
                Tag tag = new Tag(enterYear, Tag.TagType.ENTER_YEAR);
                tags.put(enterYear, tag);
                student.addTag(tag);
            }
            String year = yearPrefix + student.getYear();
            if(tags.containsKey(year)){
                student.addTag(tags.get(year));
            }else{
                Tag tag = new Tag(year, Tag.TagType.YEAR);
                tags.put(year, tag);
                student.addTag(tag);
            }

            String department = student.getDepartment().getName();
            String department2 = student.getDepartment2() == null?"":student.getDepartment2().getName();
            if(tags.containsKey(department)){
                student.addTag(tags.get(department));
            }else{
                Tag tag = new Tag(department, Tag.TagType.DEPARTMENT);
                tags.put(department, tag);
                student.addTag(tag);
            }
            if(tags.containsKey(department2)){
                student.addTag(tags.get(department2));
            }else{
                Tag tag = new Tag(department2, Tag.TagType.DEPARTMENT);
                tags.put(department2, tag);
                student.addTag(tag);
            }
        }
        Set<Tag> treeTags = new TreeSet(new Comparator<Tag>() {

            @Override
            public int compare(Tag lhs, Tag rhs) {
                Integer l = lhs.getTagType().ordinal();
                Integer r = rhs.getTagType().ordinal();
                int compare = l.compareTo(r);
                if(compare == 0){
                    compare = lhs.getText().compareTo(rhs.getText());
                }
                return compare;
            }
        });
        treeTags.addAll(tags.values());
        tags.clear();
        for(Tag tag: treeTags){
            if(!tag.getText().isEmpty())
                tags.put(tag.getText(), tag);
        }
    }

    private List<Student> filter(boolean fromFiltired) {
        List<Student> tmp = new ArrayList<Student>();
        if (lastSearch == null || lastSearch.isEmpty()) {
            tmp.addAll(filteredStudents);
            mAdapter.searchingType = SearchingType.NAME;
        }else{
            if (lastSearch.matches("\\d+")){
                for (Student student: filteredStudents){
                    if(student.getId().startsWith(lastSearch)){
                        tmp.add(student);
                    }
                }
                mAdapter.searchingType = SearchingType.ID;
            }else{
                String word = clearTurkishChars(lastSearch.toUpperCase(new Locale("tr", "TR")));
                if(lastSearch.startsWith(preSearch) && !fromFiltired){
                    for (Student student: mAdapter.getQuestions()){
                        if(clearTurkishChars(student.getName()).contains(word)){
                            tmp.add(student);
                        }
                    }
                }else{
                    for (Student student: filteredStudents){
                        if(clearTurkishChars(student.getName()).contains(word)){
                            tmp.add(student);
                        }
                    }
                }
                mAdapter.searchingType = SearchingType.NAME;
            }

        }
        return tmp;
    }

    private String clearTurkishChars(String word){
        return word.replace('Ğ', 'G').replace('Ü', 'U').replace('Ş', 'S').replace('İ', 'I').replace('Ö', 'O').replace('Ç', 'C').replaceAll("\\s+", "");
    }

    class Adapter extends FilterAdapter<Tag> {

        Adapter(@NotNull List<? extends Tag> items) {
            super(items);
        }

        @NotNull
        @Override
        public FilterItem createView(int position, Tag item) {
            FilterItem filterItem = new FilterItem(getActivity());

            filterItem.setStrokeColor(Color.parseColor("#827f93"));
            filterItem.setTextColor(Color.parseColor("#827f93"));
            filterItem.setCheckedTextColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            filterItem.setColor(ContextCompat.getColor(getActivity(), android.R.color.white));
            filterItem.setCheckedColor(generator.getColor(position));
            filterItem.setText(item.getText());
            filterItem.deselect();

            return filterItem;
        }
    }
    private class LoadListTask extends AsyncTask<String, Void, Integer> {
        StalkerFragment fragment;

        public LoadListTask(StalkerFragment fragment){
            this.fragment = fragment;
        }
        protected void onPreExecute() {
            dialog.show();
        }

        protected Integer doInBackground(String... params) {
            MainDataBaseHandler db = new MainDataBaseHandler(getActivity());
            students = new HashSet<>();
            filteredStudents = new HashSet<>();
            if(getActivity() instanceof MainActivity){
                students = db.getAllStudents();
            }else if(getActivity() instanceof SectionActivity){
                students = ((SectionActivity)getActivity()).getSection().getStudents();
            }else if(getActivity() instanceof CourseActivity){
                Course course = ((CourseActivity)getActivity()).getCourse();
                for (Section section: course.getSections()){
                    students.addAll(db.getStudentsOfSection(section.getId()));
                }
            }else if(getActivity() instanceof DepartmentActivity){
                Department department = ((DepartmentActivity)getActivity()).getDepartment();
                students = department.getStudents();
            }


            filteredStudents.addAll(students);
            studentList = Arrays.asList(students.toArray(new Student[0]));
            orderBy = OrderBy.NONE;
            return 0;
        }

        protected void onPostExecute(Integer result) {
            dialog.dismiss();
            if (result == 0) {
                resetTags(studentList);
                sort(OrderBy.NAME);
                mAdapter.setQuestions(studentList);
                mFilter.setAdapter(new Adapter(getTags()));
                mFilter.setListener(fragment);

                //the text to show when there's no selected items
                mFilter.setNoSelectedItemText(getString(R.string.filter_choose));
                mFilter.build();
                //mFilter.collapse();
            }
        }
    }
}