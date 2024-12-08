package org.ebookdroid.ui.library.adapters;

import android.widget.ImageView;
import android.widget.ProgressBar;

import org.ebookdroid.R;
import org.ebookdroid.common.settings.books.BookSettings;
import org.emdev.utils.FileUtils;

import java.io.File;

public class AdapterUtils {

    public static void setIcon(File file, boolean wasRead, ImageView imageView) {
        int drawableId = R.drawable.recent_item_book;
        String ext = FileUtils.getExtensionWithDot(file);
        if (".pdf".equalsIgnoreCase(ext)) {
            drawableId = R.drawable.browser_icon_pdf;
        } else if (".epub".equalsIgnoreCase(ext) || ".mobi".equalsIgnoreCase(ext)) {
            drawableId = R.drawable.browser_icon_epub;
        }
        //if (wasRead) {
        //    //drawableId = R.drawable.recent_item_book_watched;
        //    drawableId = R.drawable.recent_item_book;
        //}
        imageView.setImageResource(drawableId);
    }

    public static void setProgress(ProgressBar progressbar, BookSettings settings) {
        if (null == settings) {
            progressbar.setMax(0);
            progressbar.setProgress(0);
            return;
        }
        int pc = settings.pageCount;
        if (pc > 1) {
            progressbar.setMax(pc);
            progressbar.setProgress(settings.currentPage.docIndex);
        } else {
            progressbar.setMax(0);
            progressbar.setProgress(0);
        }
    }
}
