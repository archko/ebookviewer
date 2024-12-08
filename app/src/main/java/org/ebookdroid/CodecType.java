package org.ebookdroid;

import org.ebookdroid.core.codec.CodecContext;
import org.ebookdroid.droids.djvu.codec.DjvuContext;
import org.ebookdroid.droids.mupdf.codec.EpubContext;
import org.ebookdroid.droids.mupdf.codec.MuPdfContext;
import org.ebookdroid.droids.mupdf.codec.PdfContext;
import org.emdev.utils.LengthUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public enum CodecType {

    PDF(PdfContext.class, true, Arrays.asList("pdf"), Arrays.asList("application/pdf")),

    DJVU(DjvuContext.class, false, Arrays.asList("djvu", "djv"), Arrays.asList("image/djvu",
        "image/vnd.djvu",
        "image/x-djvu")),

    XPS(MuPdfContext.class, true, Arrays.asList("xps", "oxps"), Arrays.asList("application/vnd.ms-xpsdocument",
        "application/oxps")),

    EPUB(EpubContext.class, true, Arrays.asList("epub"), Arrays.asList("application/epub+zip")),
    MOBI(EpubContext.class, true, Arrays.asList("mobi"), Arrays.asList("application/x-mobipocket-ebook")),
    DOCX(EpubContext.class, true, Arrays.asList("docx"), Arrays.asList("application/vnd.openxmlformats-officedocument.wordprocessingml.document")),
    PPTX(EpubContext.class, true, Arrays.asList("pptx"), Arrays.asList("application/vnd.openxmlformats-officedocument.presentationml.presentation")),

    CBZ(MuPdfContext.class, false, Arrays.asList("cbz"), Arrays.asList("application/x-cbz")),

    FB2(MuPdfContext.class, true, Arrays.asList("fb2"), Arrays.asList("application/x-fb2"));

    private final static Map<String, CodecType> extensionToActivity;

    private final static Map<String, CodecType> mimeTypesToActivity;

    static {
        extensionToActivity = new HashMap<>();
        for (final CodecType a : values()) {
            for (final String ext : a.extensions) {
                extensionToActivity.put(ext.toLowerCase(Locale.ROOT), a);
            }
        }
        mimeTypesToActivity = new HashMap<>();
        for (final CodecType a : values()) {
            for (final String type : a.mimeTypes) {
                mimeTypesToActivity.put(type.toLowerCase(Locale.ROOT), a);
            }
        }
    }

    private final Class<? extends CodecContext> contextClass;

    public final boolean useCustomFonts;

    public final List<String> extensions;

    public final List<String> mimeTypes;

    CodecType(final Class<? extends CodecContext> contextClass, final boolean useCustomFonts,
              final List<String> extensions, final List<String> mimeTypes) {
        this.contextClass = contextClass;
        this.useCustomFonts = useCustomFonts;
        this.extensions = extensions;
        this.mimeTypes = mimeTypes;
    }

    public Class<? extends CodecContext> getContextClass() {
        return contextClass;
    }

    public static Set<String> getAllExtensions() {
        return extensionToActivity.keySet();
    }

    public static Set<String> getAllMimeTypes() {
        return mimeTypesToActivity.keySet();
    }

    public static CodecType getByUri(final String uri) {
        if (LengthUtils.isEmpty(uri)) {
            return null;
        }
        final String uriString = uri.toLowerCase(Locale.ROOT);
        for (final String ext : extensionToActivity.keySet()) {
            if (uriString.endsWith("." + ext)) {
                return extensionToActivity.get(ext);
            }
        }
        return null;
    }

    public static CodecType getByExtension(final String ext) {
        return extensionToActivity.get(ext.toLowerCase(Locale.ROOT));
    }

    public static CodecType getByMimeType(final String type) {
        return mimeTypesToActivity.get(type.toLowerCase(Locale.ROOT));
    }

    public String getDefaultExtension() {
        return extensions.get(0);
    }
}
