package org.ebookdroid.droids.mupdf.codec;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import org.ebookdroid.EBookDroidApp;
import org.ebookdroid.core.codec.AbstractCodecContext;

public class EpubDocument extends MuPdfDocument {

    EpubDocument(final MuPdfContext context, final String fname) {
        super(context, fname);
        int w = getScreenWidthPixelWithOrientation(EBookDroidApp.context);
        int h = getScreenHeightPixelWithOrientation(EBookDroidApp.context);
        int fontSize = 8 * AbstractCodecContext.getDensityDPI() / 72;
        System.out.printf("font:%s, open:%s%n", fontSize, fname);
        document.layout(w, h, fontSize);
    }

    public static int getScreenWidthPixelWithOrientation(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        return width;
    }

    public static int getScreenHeightPixelWithOrientation(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        int width = dm.heightPixels;
        return width;
    }

    public static int getDensityDpi(Context context) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.densityDpi;
    }
}
