package com.okapi.stalker.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

import com.okapi.stalker.R;

import agency.tango.materialintroscreen.SlideFragment;

/**
 * Created by burak on 9/30/2016.
 */
public class ProgramSchedulerFragment1 extends SlideFragment {
    public NumberPicker numberPicker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scheduler_1, container, false);
        numberPicker = (NumberPicker) view.findViewById(R.id.cakismaNumberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(10);
        return view;
    }

    @Override
    public int backgroundColor() {
        return R.color.first_slide_background;
    }

    @Override
    public int buttonsColor() {
        return R.color.first_slide_buttons;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return "";
    }
}
