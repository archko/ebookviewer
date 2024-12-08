package org.ebookdroid.droids.mupdf.codec;

import android.graphics.RectF;

import com.artifex.mupdf.fitz.Link;
import com.artifex.mupdf.fitz.Location;
import com.artifex.mupdf.fitz.Page;

import org.ebookdroid.core.codec.PageLink;

import java.util.ArrayList;
import java.util.List;

public class MuPdfLinks {

    static List<PageLink> getPageLinks(final MuPdfDocument doc, final Page page, final RectF pageBounds) {
        final Link[] linkArray = page.getLinks();
        final ArrayList<PageLink> links = new ArrayList<>(linkArray != null
            ? linkArray.length : 0);
        if (linkArray != null) {
            for (Link li : linkArray) {
                final PageLink link = new PageLink();
                link.sourceRect = new RectF();
                link.sourceRect.left = (li.getBounds().x0 - pageBounds.left) / pageBounds.width();
                link.sourceRect.top = (li.getBounds().y0 - pageBounds.top) / pageBounds.height();
                link.sourceRect.right = (li.getBounds().x1 - pageBounds.left) / pageBounds.width();
                link.sourceRect.bottom = (li.getBounds().y1 - pageBounds.top) / pageBounds.height();
                Location loc = doc.document.resolveLink(li);
                int pageNum = doc.document.pageNumberFromLocation(loc);
                if (pageNum < 0) {
                    //if (li.isExternal()) {
                    link.url = li.getURI();
                } else {
                    //final String[] parts = li.getURI().split(",");
                    //final int pageNum = Integer.parseInt(parts[0].substring(1)) - 1;
                    link.rectType = 1;
                    link.targetPage = pageNum;
                    link.targetRect = new RectF();
                    //if (parts.length >= 3) {
                    //    link.targetRect.left = Float.parseFloat(parts[1]);
                    //    link.targetRect.top = Float.parseFloat(parts[2]);
                    //}
                    doc.normalizeLinkTargetRect(pageNum, link.targetRect);
                }
                links.add(link);
            }
            links.trimToSize();
        }
        return links;
    }
}
