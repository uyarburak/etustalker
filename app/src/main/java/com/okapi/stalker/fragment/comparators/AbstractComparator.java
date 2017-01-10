package com.okapi.stalker.fragment.comparators;

import com.okapi.stalker.data.storage.model.Person;

import java.io.Serializable;
import java.text.Collator;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by burak on 1/7/2017.
 */
public abstract class AbstractComparator implements Comparator<Person>, Serializable {
    private static final long serialVersionUID = 1L;
    protected transient Collator coll;
    public static int carpan;

    public AbstractComparator() {
        coll = Collator.getInstance(new Locale("tr", "TR"));
        coll.setStrength(Collator.PRIMARY);
    }

    public abstract int compare(Person lhs, Person rhs);
}