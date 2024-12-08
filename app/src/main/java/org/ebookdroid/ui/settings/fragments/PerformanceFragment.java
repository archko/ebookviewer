package org.ebookdroid.ui.settings.fragments;

import org.ebookdroid.R;

import android.annotation.TargetApi;

public class PerformanceFragment extends BasePreferenceFragment {

    public PerformanceFragment() {
        super(R.xml.fragment_performance);
    }

    @Override
    public void decorate() {
        super.decorate();
        decorator.decoratePerformanceSettings();
    }
}
