package com.okapi.stalker.fragment.comparators;

import com.okapi.stalker.data.storage.model.Person;

import java.io.Serializable;

/**
 * Created by burak on 1/7/2017.
 */
public class SexComparator extends AbstractComparator implements Serializable {
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