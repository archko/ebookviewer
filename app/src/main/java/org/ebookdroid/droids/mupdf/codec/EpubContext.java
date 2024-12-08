package org.ebookdroid.droids.mupdf.codec;

import org.ebookdroid.core.codec.CodecDocument;

public class EpubContext extends MuPdfContext {

    @Override
    public CodecDocument openDocument(final String fileName) {
        return new EpubDocument(this, fileName);
    }
}
