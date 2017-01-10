package com.okapi.stalker.fragment.comparators;

import com.okapi.stalker.data.storage.model.Person;

import java.io.Serializable;

/**
 * Created by burak on 1/7/2017.
 */
public class IdComparator extends AbstractComparator implements Serializable {
    private static final long serialVersionUID = 1L;
    @Override
    public int compare(Person lhs, Person rhs) {
        return carpan * coll.compare(lhs.getId(), rhs.getId());
    }
}