package com.okapi.stalker.data.storage.model;
import android.support.annotation.NonNull;

import com.okapi.stalker.util.ColorGenerator;
import com.yalantis.filter.model.FilterModel;

/**
 * Created by galata on 16.09.16.
 */
public class Tag implements FilterModel {
    public static ColorGenerator colorGenerator = ColorGenerator.MATERIAL;

    public enum TagType {TYPLESS, ACTIVITY, GENDER, YEAR, ENTER_YEAR, DEPARTMENT}
    private TagType tagType;
    private String text;
    private int color;

    public Tag(String text) {
        this(text, colorGenerator.getColor(text), TagType.TYPLESS);
    }

    public Tag(String text, TagType tagType) {
        this(text, colorGenerator.getColor(text), tagType);
    }

    public Tag(String text, int color, TagType tagType) {
        this.text = text;
        this.color = color;
        this.tagType = tagType;
    }

    @NonNull
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public TagType getTagType() {
        return tagType;
    }

    public void setTagType(TagType tagType) {
        this.tagType = tagType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;

        Tag tag = (Tag) o;

        if (getColor() != tag.getColor()) return false;
        return getText().equals(tag.getText());

    }

    @Override
    public int hashCode() {
        int result = getText().hashCode();
        result = 31 * result + getColor();
        return result;
    }

}