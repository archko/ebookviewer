package org.ebookdroid.ui.settings.fragments;

import org.ebookdroid.R;

import android.annotation.TargetApi;

public class NavFragment extends BasePreferenceFragment {
    public NavFragment() {
        super(R.xml.fragment_navigation);
    }

    @Override
    public void decorate() {
        super.decorate();
        decorator.decorateUISettings();
    }
}
