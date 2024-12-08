package org.ebookdroid.droids.mupdf.codec;

import org.ebookdroid.core.codec.AbstractCodecDocument;
import org.ebookdroid.core.codec.CodecPageInfo;
import org.ebookdroid.core.codec.OutlineLink;

import com.artifex.mupdf.fitz.Document;

import android.graphics.RectF;

import java.util.List;

import java.io.File;

public class MuPdfDocument extends AbstractCodecDocument {

    protected final Document document;
    protected final String acceleratorPath;

    private List<OutlineLink> docOutline;

    MuPdfDocument(final MuPdfContext context, final String fname) {
        super(context);
        acceleratorPath = getAcceleratorPath(fname);
        if (acceleratorValid(fname, acceleratorPath)) {
            document = Document.openDocument(fname, acceleratorPath);
        } else {
            document = Document.openDocument(fname);
        }

        document.saveAccelerator(acceleratorPath);
    }

    @Override
    public List<OutlineLink> getOutline() {
        if (docOutline == null) {
            docOutline = MuPdfOutline.getOutline(this);
            document.saveAccelerator(acceleratorPath);
        }
        return docOutline;
    }

    @Override
    public MuPdfPage getPage(final int pageNumber) {
        final MuPdfPage page = new MuPdfPage(this, document.loadPage(pageNumber));
        document.saveAccelerator(acceleratorPath);
        return page;
    }

    @Override
    public int getPageCount() {
        final int pages = document.countPages();
        document.saveAccelerator(acceleratorPath);
        return pages;
    }

    @Override
    public CodecPageInfo getPageInfo(final int pageNumber) {
        final CodecPageInfo info = new CodecPageInfo();
        final MuPdfPage page = getPage(pageNumber);
        info.width = page.getWidth();
        info.height = page.getHeight();
        info.dpi = 0;
        info.rotation = 0;
        info.version = 0;
        document.saveAccelerator(acceleratorPath);
        return info;
    }

    @Override
    protected void freeDocument() {
        document.destroy();
    }

    @Override
    public Boolean needsPassword() {
        return document.needsPassword();
    }

    @Override
    public Boolean authenticate(final String password) {
        return document.authenticatePassword(password);
    }

    public void normalizeLinkTargetRect(final int page, final RectF rect) {

        final CodecPageInfo cpi = getPageInfo(page);

        final float left = rect.left;
        final float top = rect.top;

        if (((cpi.rotation / 90) % 2) != 0) {
            rect.right = rect.left = left / cpi.height;
            rect.bottom = rect.top = top / cpi.width;
        } else {
            rect.right = rect.left = left / cpi.width;
            rect.bottom = rect.top = top / cpi.height;
        }
    }

    protected static String getAcceleratorPath(String documentPath) {
        String acceleratorName = documentPath.substring(1);
        acceleratorName = acceleratorName.replace(File.separatorChar, '%');
        acceleratorName = acceleratorName.replace('\\', '%');
        acceleratorName = acceleratorName.replace(':', '%');
        String tmpdir = System.getProperty("java.io.tmpdir");
        return new StringBuffer(tmpdir).append(File.separatorChar).append(acceleratorName).append(".accel").toString();
    }

    protected static boolean acceleratorValid(String documentPath, String acceleratorPath) {
        long documentModified = new File(documentPath).lastModified();
        long acceleratorModified = new File(acceleratorPath).lastModified();
        return acceleratorModified != 0 && acceleratorModified > documentModified;
    }
}
