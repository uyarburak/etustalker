package com.okapi.stalker.fragment.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.okapi.stalker.R;
import com.okapi.stalker.data.FriendsDataBaseHandler;
import com.okapi.stalker.data.storage.model.Person;
import com.okapi.stalker.data.storage.model.Student;

import java.io.Serializable;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class MyStalkerAdapter extends BaseAdapter implements Filterable {

    public enum OrderBy{NONE, NAME, ID, SEX, DEPARTMENT};
    public enum SearchingType{NAME, ID};
    private OrderBy orderBy;
    private SearchingType searchingType;

    private String lastSearch;
    private LayoutInflater mInflater;

    private Set<Student> allStudents;
    private List<Student> arrayListFilter;
    private HashSet<String> discoveredStudents;
    private HashSet<String> friends;

    public MyStalkerAdapter(Activity activity, Set<Student> students) {
        mInflater = (LayoutInflater) activity.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        arrayListFilter = new ArrayList<>();
        arrayListFilter.addAll(students);
        discoveredStudents = new HashSet<>();
        allStudents = students;
        orderBy = OrderBy.NONE;
        searchingType = SearchingType.NAME;
        sort(OrderBy.NAME);
        System.out.println("zaaaaa: " + arrayListFilter.size());
        FriendsDataBaseHandler fdb = new FriendsDataBaseHandler(activity);
        friends = new HashSet<>(fdb.getAllFriends());

    }


    @Override
    public int getCount() {
        return arrayListFilter.size();
    }

    @Override
    public Student getItem(int position) {
        return arrayListFilter.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView;

        rowView = mInflater.inflate(R.layout.fragment_stalker_list, null);

        TextView textName =
                (TextView) rowView.findViewById(R.id.name);
        TextView textMajor =
                (TextView) rowView.findViewById(R.id.department);
        ImageView imageView =
                (ImageView) rowView.findViewById(R.id.thumb);

        final Student student = getItem(position);
        textName.setText(student.getName());
        if(searchingType == SearchingType.NAME){
            if(student.getDepartment2() != null){
                textMajor.setText(student.getDepartment().getName() + " - " + student.getDepartment2().getName());
            }else{
                textMajor.setText(student.getDepartment().getName());
            }
        }else{
            textMajor.setText(student.getId());
        }

        switch (student.getGender()){
            case 'M':
                imageView.setImageResource(R.drawable.ic_gender_male);
                break;
            case 'F':
                imageView.setImageResource(R.drawable.ic_gender_female);
                break;
            default:
                imageView.setImageResource(R.drawable.ic_help);
                break;
        }

        if(discoveredStudents.contains(student.getId()))
            textName.setTextColor(Color.RED);
        else if(friends.contains(student.getId()))
            textName.setTextColor(Color.BLUE);
        return rowView;
    }

    public void changeColor(int position, View view){
        TextView textName =
                (TextView) view.findViewById(R.id.name);
        textName.setTextColor(Color.RED);
        Student student = getItem(position);
        discoveredStudents.add(student.getId());
    }
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                arrayListFilter = (List<Student>) results.values;
                notifyDataSetChanged();
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                lastSearch = constraint.toString().trim();
                List<Student> filteredArrayNames = MyStalkerAdapter.this.filter();
                results.count = filteredArrayNames.size();
                results.values = filteredArrayNames;

                return results;
            }
        };

        return filter;
    }

    private List<Student> filter() {
        List<Student> tmp = new ArrayList<Student>();
        if (lastSearch == null || lastSearch.isEmpty()) {
            tmp.addAll(allStudents);
            searchingType = SearchingType.NAME;
        }else{
            if (lastSearch.matches("\\d+")){
                for (Student student: allStudents){
                    if(student.getId().startsWith(lastSearch)){
                        tmp.add(student);
                    }
                }
                searchingType = SearchingType.ID;
            }else{
                String word = getRidOfTR(lastSearch.toUpperCase(new Locale("tr", "TR")));
                for (Student student: allStudents){
                    if(getRidOfTR(student.getName()).contains(word)){
                        tmp.add(student);
                    }
                }
                searchingType = SearchingType.NAME;
            }

        }
        return tmp;
    }

    private String getRidOfTR(String word){
        return word.replace('Ğ', 'G').replace('Ü', 'U').replace('Ş', 'S').replace('İ', 'I').replace('Ö', 'O').replace('Ç', 'C');
    }

    public void sort(OrderBy order){
        AbstractComparator.carpan = orderBy == order ? -1 : 1;
        Set<Student> sortedSet;
        searchingType = SearchingType.NAME;
        switch (order){
            case NAME:
                sortedSet = new TreeSet<Student>(new NameComparator());
                break;
            case ID:
                sortedSet = new TreeSet<Student>(new IdComparator());
                searchingType = SearchingType.ID;
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
        sortedSet.addAll(allStudents);
        allStudents = sortedSet;
        arrayListFilter = filter();
        notifyDataSetChanged();
        orderBy = order;
    }
}
abstract class AbstractComparator implements Comparator<Person>, Serializable {
    private static final long serialVersionUID = 1L;
    protected transient Collator coll;
    public static int carpan;

    public AbstractComparator() {
        coll = Collator.getInstance(new Locale("tr", "TR"));
        coll.setStrength(Collator.PRIMARY);
    }

    public abstract int compare(Person lhs, Person rhs);
}

class NameComparator extends AbstractComparator implements Serializable {
    private static final long serialVersionUID = 1L;
    @Override
    public int compare(Person lhs, Person rhs) {
        int comp = carpan * coll.compare(lhs.getName(), rhs.getName());
        if(comp != 0)
            return comp;
        comp = coll.compare(lhs.getDepartment().getName(), rhs.getDepartment().getName());
        if (comp != 0)
            return comp;
        return coll.compare(lhs.getId(), rhs.getId());
    }
}
class IdComparator extends AbstractComparator implements Serializable {
    private static final long serialVersionUID = 1L;
    @Override
    public int compare(Person lhs, Person rhs) {
        return carpan * coll.compare(lhs.getId(), rhs.getId());
    }
}
class SexComparator extends AbstractComparator implements Serializable {
    private static final long serialVersionUID = 1L;
    @Override
    public int compare(Person lhs, Person rhs) {
        int comp = carpan * lhs.getGender().compareTo(rhs.getGender());
        if(comp != 0)
            return comp;
        comp = coll.compare(lhs.getName(), rhs.getName());
        if(comp != 0)
            return comp;
        comp = coll.compare(lhs.getDepartment().getName(), rhs.getDepartment().getName());
        if(comp != 0)
            return comp;
        return coll.compare(lhs.getId(), rhs.getId());
    }
}
class DepartmentComparator extends AbstractComparator implements Serializable {
    private static final long serialVersionUID = 1L;
    @Override
    public int compare(Person lhs, Person rhs) {
        int comp = carpan * coll.compare(lhs.getDepartment().getName(), rhs.getDepartment().getName());
        if(comp != 0)
            return comp;
        comp = coll.compare(lhs.getName(), rhs.getName());
        if(comp != 0)
            return comp;
        return coll.compare(lhs.getId(), rhs.getId());
    }
}