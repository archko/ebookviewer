package org.emdev.ui.uimanager;

import android.annotation.TargetApi;
import android.app.Activity;

import static android.view.View.SYSTEM_UI_FLAG_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
import static android.view.View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
import static android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE;

@TargetApi(21)
public class UIManager51x extends UIManager41x {

    private static final int EXT_SYS_UI_FLAGS =
        /**/
        SYSTEM_UI_FLAG_LOW_PROFILE |
            /**/
            SYSTEM_UI_FLAG_FULLSCREEN |
            /**/
            SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
            /**/
            SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
            /**/
            SYSTEM_UI_FLAG_LAYOUT_STABLE |
            /**/
            SYSTEM_UI_FLAG_HIDE_NAVIGATION;

    @Override
    protected int getHideSysUIFlags(final Activity activity) {
        return isTabletUi(activity) ? STANDARD_SYS_UI_FLAGS : EXT_SYS_UI_FLAGS;
    }

}
