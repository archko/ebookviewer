package org.ebookdroid.core.codec;

import android.graphics.Bitmap;
import android.graphics.RectF;

import java.util.Collections;
import java.util.List;

public abstract class AbstractCodecDocument implements CodecDocument {

    protected final AbstractCodecContext context;

    protected AbstractCodecDocument(final AbstractCodecContext context) {
        this.context = context;
    }

    @Override
    protected final void finalize() throws Throwable {
        recycle();
        super.finalize();
    }

    @Override
    public final void recycle() {
        if (!isRecycled()) {
            context.recycle();
            freeDocument();
        }
    }

    @Override
    public final boolean isRecycled() {
        return context == null || context.isRecycled();
    }

    protected void freeDocument() {
    }

    @Override
    public Boolean needsPassword() {
        return false;
    }

    @Override
    public Boolean authenticate(final String password) {
        return true;
    }

    @Override
    public final CodecContext getContext() {
        return context;
    }

    @Override
    public final boolean isFeatureSupported(final int feature) {
        return (context.supportedFeatures & feature) != 0;
    }

    @Override
    public List<OutlineLink> getOutline() {
        return Collections.emptyList();
    }

    @Override
    public CodecPageInfo getUnifiedPageInfo() {
        return null;
    }

    @Override
    public List<? extends RectF> searchText(final int pageNuber, final String pattern) {
        return Collections.emptyList();
    }

    @Override
    public Bitmap getEmbeddedThumbnail() {
        return null;
    }
}
