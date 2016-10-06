package com.okapi.stalker.data.storage.model;

import com.okapi.stalker.data.storage.model.Section;

import java.io.Serializable;
import java.util.Set;

/**
 * Created by burak on 6/12/2016.
 */
public interface Person extends Serializable {
    public Set<Section> getSections();

    public Object getId();

    public String getName();

    public String getMail();

    public Department getDepartment();

    public Character getGender();

    public String getImage();

}
