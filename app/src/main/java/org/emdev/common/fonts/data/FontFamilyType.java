package org.emdev.common.fonts.data;

import android.graphics.Typeface;

import org.emdev.utils.enums.ResourceConstant;

public enum FontFamilyType implements ResourceConstant {
    /**
     *
     */
    SANS("sans", Typeface.SANS_SERIF),
    /**
     *
     */
    SERIF("serif", Typeface.SERIF),
    /**
     *
     */
    MONO("mono", Typeface.MONOSPACE),
    /**
     *
     */
    SYMBOL("symbol", Typeface.DEFAULT),
    /**
     *
     */
    DINGBAT("dingbat", Typeface.DEFAULT);

    private final String value;

    private final Typeface system;

    FontFamilyType(String value, Typeface system) {
        this.value = value;
        this.system = system;
    }

    @Override
    public String getResValue() {
        return value;
    }

    public Typeface getSystem() {
        return system;
    }
}
