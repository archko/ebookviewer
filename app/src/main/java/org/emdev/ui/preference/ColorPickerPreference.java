/*
 * Copyright (C) 2010 Daniel Nilsson
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.emdev.ui.preference;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.preference.DialogPreference;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.afzkl.colorpicker.views.ColorPanelView;
import org.afzkl.colorpicker.views.ColorPickerView;
import org.afzkl.colorpicker.views.ColorPickerView.OnColorChangedListener;
import org.ebookdroid.R;
import org.emdev.utils.LengthUtils;

import java.util.Locale;

public class ColorPickerPreference extends DialogPreference implements ColorPickerView.OnColorChangedListener {

    private ColorPickerView mColorPicker;

    private ColorPanelView mOldColor;
    private ColorPanelView mNewColor;

    private OnColorChangedListener mListener;

    private int color;
    private EditText mEtHex;
    private ColorStateList mHexDefaultTextColor;

    public ColorPickerPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        setWidgetLayoutResource(R.layout.color_picker_preference_widget);
    }

    @Override
    protected void onSetInitialValue(final boolean restoreValue, final Object defaultValue) {
        readColor(defaultValue);
    }

    private void readColor(final Object defaultValue) {
        try {
            color = Integer.parseInt(getPersistedString(LengthUtils.toString(defaultValue)));
        } catch (final Exception ex) {
            color = 0x00FFFFFF;
        }
    }

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        View v = view.findViewById(R.id.color_picke_preference_view);
        if (v != null) {
            Drawable background = v.getBackground();
            if (background instanceof GradientDrawable) {
                GradientDrawable grad = (GradientDrawable) background;
                grad.setColor(color);
            }
        }
    }

    @Override
    protected View onCreateDialogView() {
        final LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout = inflater.inflate(R.layout.dialog_color_picker, null);
        readColor(0x00FFFFFF);
        mColorPicker = layout.findViewById(R.id.color_picker_view);
        mOldColor = layout.findViewById(R.id.old_color_panel);
        mNewColor = layout.findViewById(R.id.new_color_panel);
        mEtHex = layout.findViewById(R.id.hexValue);
        mEtHex.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        mHexDefaultTextColor = mEtHex.getTextColors();

        //当点击软键盘上的【完成】按钮时触发监听
        mEtHex.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                String hexVal = mEtHex.getText().toString();
                if (hexVal.length() >= 0 || hexVal.length() < 7) {
                    try {
                        int c = convertToColorInt(hexVal);
                        mColorPicker.setColor(c, true);
                        mEtHex.setTextColor(mHexDefaultTextColor);
                    } catch (IllegalArgumentException e) {
                        mEtHex.setTextColor(color);
                    }
                } else {
                    mEtHex.setTextColor(color);
                }
                return true;
            }
            return false;
        });

        ((LinearLayout) mOldColor.getParent()).setPadding(Math.round(mColorPicker.getDrawingOffset()), 0,
            Math.round(mColorPicker.getDrawingOffset()), 0);

        mColorPicker.setOnColorChangedListener(this);

        mOldColor.setColor(color);
        mColorPicker.setColor(color, true);
        setAlphaSliderVisible(true);
        return layout;
    }

    @Override
    protected void onDialogClosed(final boolean positiveResult) {
        if (positiveResult) {
            color = getColor();
            final String value = Integer.toString(color);
            if (callChangeListener(value)) {
                if (shouldPersist()) {
                    persistString(value);
                }
                notifyChanged();
            }
        }
    }

    @Override
    public void onColorChanged(final int color) {
        mNewColor.setColor(color);
        updateHexValue(color);

        if (mListener != null) {
            mListener.onColorChanged(color);
        }
    }

    private void updateHexValue(int color) {
        mEtHex.setText(convertToRGB(color).toUpperCase(Locale.getDefault()));
        mEtHex.setTextColor(mHexDefaultTextColor);
    }

    public void setAlphaSliderVisible(final boolean visible) {
        mColorPicker.setAlphaSliderVisible(visible);
    }

    public int getColor() {
        return mColorPicker.getColor();
    }

    /**
     * 16进制颜色 转 RGB(String)类型颜色(无#号)
     *
     * @param color 16进制颜色
     * @return RGB颜色(无透明度值)
     */
    static String convertToRGB(int color) {
        String red = Integer.toHexString(Color.red(color));
        String green = Integer.toHexString(Color.green(color));
        String blue = Integer.toHexString(Color.blue(color));
        if (red.length() == 1) red = "0" + red;
        if (green.length() == 1) green = "0" + green;
        if (blue.length() == 1) blue = "0" + blue;
        return red + green + blue;
    }

    /**
     * ARGB(含RGB)颜色 转 16进制颜色
     *
     * @param argb ARGB(含RGB)颜色
     * @return 16进制颜色
     * @throws NumberFormatException 当{@param argb}不是一个正确的颜色格式的字符串时
     */
    static int convertToColorInt(String argb) throws IllegalArgumentException {
        if (argb.matches("[0-9a-fA-F]{1,6}")) {
            switch (argb.length()) {
                case 1:
                    return Color.parseColor("#00000" + argb);
                case 2:
                    return Color.parseColor("#0000" + argb);
                case 3:
                    char r = argb.charAt(0), g = argb.charAt(1), b = argb.charAt(2);
                    //noinspection StringBufferReplaceableByString
                    return Color.parseColor(new StringBuilder("#")
                        .append(r).append(r)
                        .append(g).append(g)
                        .append(b).append(b)
                        .toString());
                case 4:
                    return Color.parseColor("#00" + argb);
                case 5:
                    return Color.parseColor("#0" + argb);
                case 6:
                    return Color.parseColor("#" + argb);
            }
        }
        throw new IllegalArgumentException(argb + " is not a valid color.");
    }
}
