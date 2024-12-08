package org.ebookdroid.ui.settings.fragments;

import org.ebookdroid.R;

import android.annotation.TargetApi;

public class RenderFragment extends BasePreferenceFragment {

    public RenderFragment() {
        super(R.xml.fragment_render);
    }

    @Override
    public void decorate() {
        super.decorate();
        decorator.decorateRenderSettings();
    }
}
