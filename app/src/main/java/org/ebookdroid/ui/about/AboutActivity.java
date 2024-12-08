package org.ebookdroid.ui.about;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.TabHost;

import com.google.android.material.appbar.MaterialToolbar;

import org.ebookdroid.R;
import org.emdev.utils.CompareUtils;
import org.emdev.utils.LayoutUtils;
import org.emdev.utils.LengthUtils;
import org.emdev.utils.wiki.Wiki;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import androidx.appcompat.app.AppCompatActivity;

public class AboutActivity extends AppCompatActivity {

    private static final Part[] PARTS = {
        // Start
        new Part(R.string.about_commmon_title, Format.HTML, "common.html"),
        new Part(R.string.about_license_title, Format.HTML, "license.html"),
        new Part(R.string.about_3dparty_title, Format.HTML, "3rdparty.html"),
        new Part(R.string.about_changelog_title, Format.WIKI, "changelog.wiki"),
        new Part(R.string.about_thanks_title, Format.HTML, "thanks.html"),
        //new Part(R.string.about_donations, Format.HTML, "donations.html"),
        // End
    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.about);

        LayoutUtils.maximizeWindow(getWindow());

        TabHost browserTabHost = findViewById(R.id.browserTabHost);
        browserTabHost.setup();

        for (Part part : PARTS) {
            View appView = initTabView(part);
            browserTabHost.addTab(
                browserTabHost
                    .newTabSpec(part.fileName)
                    .setIndicator(getString(part.labelId))
                    .setContent(tag -> appView)
            );
        }

        String name = getResources().getString(R.string.app_name);
        String version = "";
        try {
            final PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = packageInfo.versionName;
            name = getResources().getString(packageInfo.applicationInfo.labelRes);
        } catch (final NameNotFoundException e) {
            e.printStackTrace();
        }

        String text = name;
        if (LengthUtils.isNotEmpty(version)) {
            name += " version";
        } else {
        }
        //MaterialToolbar toolbar = findViewById(R.id.toolbar);
        //toolbar.setNavigationOnClickListener(v -> finish());
        //toolbar.setTitle(text);
    }

    private View initTabView(Part part) {
        CharSequence content = part.getContent(this);
        return initView(content);
    }

    private WebView initView(CharSequence content) {
        WebView view = new WebView(this);

        view.loadData(content.toString(), "text/html", "UTF-8");

        return view;
    }

    private static class Part {

        final int labelId;
        final Format format;
        final String fileName;
        CharSequence content;
        String actualFileName;

        public Part(final int labelId, final Format format, final String fileName) {
            this.labelId = labelId;
            this.format = format;
            this.fileName = fileName;
        }

        public CharSequence getContent(final Context context) {
            final String aName = getActualFileName(context);
            if (content == null || !CompareUtils.equals(aName, actualFileName)) {
                content = null;
                actualFileName = null;
                try {
                    InputStream input;
                    try {
                        input = context.getAssets().open(aName);
                        actualFileName = aName;
                    } catch (final FileNotFoundException e) {
                        actualFileName = getDefaultFileName();
                        input = context.getAssets().open(actualFileName);
                    }
                    final int size = input.available();
                    final byte[] buffer = new byte[size];
                    input.read(buffer);
                    input.close();
                    final String text = new String(buffer, StandardCharsets.UTF_8);
                    content = format.format(text);
                } catch (final IOException e) {
                    e.printStackTrace();
                    content = "";
                }
            }
            return content;
        }

        public String getActualFileName(final Context context) {
            return getActualFileName(context.getString(R.string.about_prefix));
        }

        public String getDefaultFileName() {
            return getActualFileName("en");
        }

        public String getActualFileName(final String lang) {
            final StringBuilder actualName = new StringBuilder("about");
            actualName.append("/").append(lang);
            actualName.append("/");
            actualName.append(fileName);
            final String s = actualName.toString();
            return s;
        }

    }

    private enum Format {
        /**
         *
         */
        TEXT,

        /**
         *
         */
        HTML,

        /**
         *
         */
        WIKI {
            @Override
            public CharSequence format(final String text) {
                return Wiki.fromWiki(text);
            }
        };

        public CharSequence format(final String text) {
            return text;
        }
    }
}
