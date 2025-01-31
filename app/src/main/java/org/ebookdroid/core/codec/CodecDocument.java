package org.ebookdroid.core.codec;

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.List;

public interface CodecDocument extends CodecFeatures {

    void recycle();

    boolean isRecycled();

    CodecContext getContext();

    int getPageCount();

    CodecPage getPage(int pageNuber);

    CodecPageInfo getPageInfo(int pageNuber);

    CodecPageInfo getUnifiedPageInfo();

    Boolean needsPassword();

    Boolean authenticate(String password);

    List<? extends RectF> searchText(int pageNuber, String pattern);

    List<OutlineLink> getOutline();

    Bitmap getEmbeddedThumbnail();
}
