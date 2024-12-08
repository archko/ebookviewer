package org.ebookdroid.droids.mupdf.codec;

import com.artifex.mupdf.fitz.Document;
import com.artifex.mupdf.fitz.Outline;

import org.ebookdroid.core.codec.OutlineLink;

import java.util.ArrayList;
import java.util.List;

public class MuPdfOutline {

    /*public static List<OutlineLink> getOutline(final MuPdfDocument doc) {
        final Outline[] ol = doc.document.loadOutline();
        final int allocCount = ol != null ? 5 * ol.length : 0;
        final ArrayList<OutlineLink> ls = new ArrayList<>(allocCount);
        if (ol != null) {
            for (Outline o : ol) {
                ttOutline(doc, o, ls, 0);
            }
        }
        ls.trimToSize();
        return ls;
    }

    private static void ttOutline(final MuPdfDocument doc, final Outline ol,
                                  final List<OutlineLink> list, int level) {
        final String[] parts = ol.uri.split(",");
        OutlineLink link = new OutlineLink(ol.title, parts[0], level);
        link.targetRect = new RectF();
        if (parts.length >= 3) {
            link.targetRect.left = Float.parseFloat(parts[1]);
            link.targetRect.top = Float.parseFloat(parts[2]);
        }
        doc.normalizeLinkTargetRect(Integer.parseInt(parts[0].substring(1)), link.targetRect);
        list.add(link);
        if (ol.down != null) {
            for (Outline o : ol.down) {
                ttOutline(doc, o, list, level++);
            }
        }
    }*/

    public static List<OutlineLink> getOutline(MuPdfDocument doc) {
        Document document = doc.document;
        Outline[] outlines = document.loadOutline();
        List<OutlineLink> links = new ArrayList<>();
        if (outlines != null) {
            int level = 0;
            downOutline(document, outlines, links, level);
        }
        return links;
    }

    public static void downOutline(Document core, Outline[] outlines, List<OutlineLink> links, int level) {
        if (null != outlines) {
            for (Outline outline : outlines) {
                int page = core.pageNumberFromLocation(core.resolveLink(outline));
                OutlineLink link = new OutlineLink(outline.title, page, level);
                if (outline.down != null) {
                    Outline[] child = outline.down;
                    downOutline(core, child, links, level + 1);
                }
                links.add(link);
            }
        }
    }
}
