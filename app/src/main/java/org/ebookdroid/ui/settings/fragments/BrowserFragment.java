package org.ebookdroid.ui.settings.fragments;

import org.ebookdroid.R;

import android.annotation.TargetApi;

public class BrowserFragment extends BasePreferenceFragment {

    public BrowserFragment() {
        super(R.xml.fragment_browser);
    }

    @Override
    public void decorate() {
        super.decorate();
        decorator.decorateBrowserSettings();
    }

}
