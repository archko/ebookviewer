package org.ebookdroid.core.crop;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;

import org.ebookdroid.common.bitmaps.ByteBufferBitmap;

import java.nio.ByteBuffer;

public class PageCropper {

    public static final int BMP_SIZE = 240;

    private PageCropper() {
    }

    public static synchronized RectF getCropBounds(final ByteBufferBitmap bitmap, final RectF psb) {
        //return nativeGetCropBounds(bitmap.getPixels(), bitmap.getWidth(), bitmap.getHeight(), psb.left, psb.top,
        //    psb.right, psb.bottom);
        return getJavaCropBounds(bitmap.toBitmap().getBitmap(), psb);
    }

    public static synchronized RectF getColumn(final ByteBufferBitmap bitmap, final float x, final float y) {
        return nativeGetColumn(bitmap.getPixels(), bitmap.getWidth(), bitmap.getHeight(), x, y);
    }

    private static native RectF nativeGetCropBounds(ByteBuffer pixels, int width, int height, float left, float top,
                                                    float right, float bottom);

    private static native RectF nativeGetColumn(ByteBuffer pixels, int width, int height, float x, float y);


    //=======================
    //========================= java =========================

    /**
     * min width/height,if a bitmap is to small, don't crop
     */
    public static final int MIN_WIDTH = 30;
    private static final int THRESHOLD = 4;

    // 扫描步进
    private final static int LINE_SIZE = 5;
    private final static int V_LINE_SIZE = 8;
    //边距留白,与切割的图片大小是有关的,缩略图越小,留白应该越小,因为精度小
    private final static int LINE_MARGIN = 16;
    //这个值越小,表示忽略的空间越大,比如一行就一个页码,如果这个适中,就直接忽略,认为这行是空白的
    private final static double WHITE_THRESHOLD = 0.004;

    /**
     * 使用的是平均像素,对于有些图片切割会异常,在于精确度的调整.
     * 扫描步进为5,平均200*200的图片1毫秒内,对于红色字,会当成无效的字
     *
     * @param bitmap
     * @param bitmapBounds
     * @return
     */
    public static RectF getJavaCropBounds(final Bitmap bitmap) {
        return getJavaCropBounds(bitmap, new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()));
    }

    public static RectF getJavaCropTopBottomBounds(final Bitmap bitmap, final Rect bitmapBounds) {
        if (bitmap.getHeight() < (MIN_WIDTH) || bitmap.getWidth() < MIN_WIDTH) {
            return new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight());
        }
        final float avgLum = 225;
        float left = 0;
        float right = bitmap.getWidth();
        float top = getTopBound(bitmap, bitmapBounds, avgLum);
        float bottom = getBottomBound(bitmap, bitmapBounds, avgLum);

        left = left * bitmapBounds.width();
        top = top * bitmapBounds.height();
        right = bitmap.getWidth();
        bottom = bottom * bitmapBounds.height();

        return new RectF(left, top, right, bottom);
    }

    public static RectF getJavaCropBounds(final Bitmap bitmap, final RectF bitmapBounds) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        if (height < (MIN_WIDTH) || width < MIN_WIDTH) {
            return new RectF(0, 0, width, height);
        }
        //System.out.printf("droid-bitmap:%s, %s, %s%n", width, height, bitmapBounds);
        //long start = System.currentTimeMillis();

        //计算平均灰度不如直接设置225,效果要好的多,平均值会把红色的识别成白边
        final float avgLum = 225; //calculateAvgLum(bitmap, bitmapBounds);
        Rect rect = new Rect((int) bitmapBounds.left * width, (int) bitmapBounds.top * height, (int) bitmapBounds.right * width, (int) bitmapBounds.bottom * height);
        float left = getLeftBound(bitmap, rect, avgLum);
        float right = getRightBound(bitmap, rect, avgLum);
        float top = getTopBound(bitmap, rect, avgLum);
        float bottom = getBottomBound(bitmap, rect, avgLum);

        left = left * bitmapBounds.width();
        top = top * bitmapBounds.height();
        right = right * bitmapBounds.width();
        bottom = bottom * bitmapBounds.height();

        //System.out.printf("droid-crop-time:%s%n", (System.currentTimeMillis() - start));
        return new RectF(left, top, right, bottom);
    }

    private static float getLeftBound(final Bitmap bmp, final Rect bitmapBounds, final float avgLum) {
        final int w = bmp.getWidth() / 3;
        int whiteCount = 0;
        int x = 0;
        for (x = bitmapBounds.left; x < bitmapBounds.left + w; x += LINE_SIZE) {
            final boolean white = isRectWhite(bmp, x, bitmapBounds.top + LINE_MARGIN, x + LINE_SIZE, bitmapBounds.bottom - LINE_MARGIN,
                avgLum);
            if (white) {
                whiteCount++;
            } else {
                if (whiteCount >= 1) {
                    return (float) (Math.max(bitmapBounds.left, x - LINE_SIZE) - bitmapBounds.left)
                        / bitmapBounds.width();
                }
                whiteCount = 0;
            }
        }
        return whiteCount > 0 ? (float) (Math.max(bitmapBounds.left, x - LINE_SIZE) - bitmapBounds.left)
            / bitmapBounds.width() : 0;
    }

    private static float getTopBound(final Bitmap bmp, final Rect bitmapBounds, final float avgLum) {
        final int h = bmp.getHeight() / 3;
        int whiteCount = 0;
        int y = 0;
        for (y = bitmapBounds.top; y < bitmapBounds.top + h; y += V_LINE_SIZE) {
            final boolean white = isRectWhite(bmp, bitmapBounds.left + LINE_MARGIN, y, bitmapBounds.right - LINE_MARGIN, y + LINE_SIZE,
                avgLum);
            if (white) {
                whiteCount++;
            } else {
                if (whiteCount >= 1) {
                    return (float) (Math.max(bitmapBounds.top, y - V_LINE_SIZE) - bitmapBounds.top)
                        / bitmapBounds.height();
                }
                whiteCount = 0;
            }
        }
        return whiteCount > 0 ? (float) (Math.max(bitmapBounds.top, y - V_LINE_SIZE) - bitmapBounds.top)
            / bitmapBounds.height() : 0;
    }

    private static float getRightBound(final Bitmap bmp, final Rect bitmapBounds, final float avgLum) {
        final int w = bmp.getWidth() / 3;
        int whiteCount = 0;
        int x = 0;
        for (x = bitmapBounds.right - LINE_SIZE; x > bitmapBounds.right - w; x -= LINE_SIZE) {
            final boolean white = isRectWhite(bmp, x, bitmapBounds.top + LINE_MARGIN, x + LINE_SIZE, bitmapBounds.bottom - LINE_MARGIN,
                avgLum);
            if (white) {
                whiteCount++;
            } else {
                if (whiteCount >= 1) {
                    return (float) (Math.min(bitmapBounds.right, x + 2 * LINE_SIZE) - bitmapBounds.left)
                        / bitmapBounds.width();
                }
                whiteCount = 0;
            }
        }
        return whiteCount > 0 ? (float) (Math.min(bitmapBounds.right, x + 2 * LINE_SIZE) - bitmapBounds.left)
            / bitmapBounds.width() : 1;
    }

    private static float getBottomBound(final Bitmap bmp, final Rect bitmapBounds, final float avgLum) {
        final int h = bmp.getHeight() * 2 / 3;
        int whiteCount = 0;
        int y = 0;
        for (y = bitmapBounds.bottom - V_LINE_SIZE; y > bitmapBounds.bottom - h; y -= V_LINE_SIZE) {
            final boolean white = isRectWhite(bmp, bitmapBounds.left + LINE_MARGIN, y, bitmapBounds.right - LINE_MARGIN, y + LINE_SIZE,
                avgLum);
            if (white) {
                whiteCount++;
            } else {
                if (whiteCount >= 1) {
                    return (float) (Math.min(bitmapBounds.bottom, y + 2 * V_LINE_SIZE) - bitmapBounds.top)
                        / bitmapBounds.height();
                }
                whiteCount = 0;
            }
        }
        return whiteCount > 0 ? (float) (Math.min(bitmapBounds.bottom, y + 2 * V_LINE_SIZE) - bitmapBounds.top)
            / bitmapBounds.height() : 1;
    }

    private static boolean isRectWhite(final Bitmap bmp, final int l, final int t, final int r, final int b,
                                       final float avgLum) {
        int count = 0;

        final int[] pixels = getPixels(bmp, new Rect(l, t, r, b));
        for (final int c : pixels) {
            final float lum = getLum(c);
            if ((lum < avgLum) && ((avgLum - lum) * 10 > avgLum)) {
                count++;
            }
        }
        return ((float) count / pixels.length) < WHITE_THRESHOLD;
    }

    private static float calculateAvgLum(final Bitmap bmp, final Rect bitmapBounds) {
        if (bmp == null) {
            return 1000;
        }

        float lum = 0f;

        final int sizeX = bitmapBounds.width() / 10;
        final int sizeY = bitmapBounds.height() / 10;
        final int centerX = bitmapBounds.centerX();
        final int centerY = bitmapBounds.centerX();

        final int[] pixels = getPixels(bmp,
            new Rect(centerX - sizeX,
                centerY - sizeY,
                centerX + sizeX,
                centerY + sizeY));
        for (final int c : pixels) {
            lum += getLum(c);
        }

        return lum / (pixels.length);
    }

    private static float getLum(final int c) {
        final int r = (c & 0xFF0000) >> 16;
        final int g = (c & 0xFF00) >> 8;
        final int b = c & 0xFF;

        final int min = Math.min(r, Math.min(g, b));
        final int max = Math.max(r, Math.max(g, b));
        return (2f * min + max) / 3;
    }

    public static int[] getPixels(Bitmap bitmap, Rect srcRect) {
        int width = srcRect.width();
        int height = srcRect.height();
        //boolean hasAlpha = bitmap.hasAlpha();
        int[] pixels = new int[width * height];

        bitmap.getPixels(pixels, 0, width, srcRect.left, srcRect.top, width, height);
        return pixels;
    }

    //=====================

    public static RectF getJavaCropBounds(final ByteBuffer bitmap, final RectF bitmapBounds) {
        long start = System.currentTimeMillis();
        int len = bitmap.limit() - bitmap.position();
        byte[] intArray = new byte[len];
        bitmap.get(intArray);

        //计算平均灰度不如直接设置225,效果要好的多,平均值会把红色的识别成白边
        final float avgLum = 225; //calculateAvgLum(bitmap, bitmapBounds);
        Rect rect = new Rect((int) bitmapBounds.left, (int) bitmapBounds.top, (int) bitmapBounds.right, (int) bitmapBounds.bottom);
        float left = getLeftBound(intArray, rect, avgLum);
        float right = getRightBound(intArray, rect, avgLum);
        float top = getTopBound(intArray, rect, avgLum);
        float bottom = getBottomBound(intArray, rect, avgLum);

        left = left * bitmapBounds.width();
        top = top * bitmapBounds.height();
        right = right * bitmapBounds.width();
        bottom = bottom * bitmapBounds.height();

        System.out.println(String.format("droid-crop-time:%s, %s, %s, %s, %s, %s", (System.currentTimeMillis() - start), intArray.length, left, top, right, bottom));
        return new RectF(left, top, right, bottom);
    }

    private static float getLeftBound(final byte[] bmp, final Rect bitmapBounds, final float avgLum) {
        final int w = bitmapBounds.width() / 3;
        int whiteCount = 0;
        int x = 0;
        for (x = bitmapBounds.left; x < bitmapBounds.left + w; x += LINE_SIZE) {
            final boolean white = isRectWhite(bmp, x, bitmapBounds.top + LINE_MARGIN, x + LINE_SIZE, bitmapBounds.bottom - LINE_MARGIN,
                avgLum);
            if (white) {
                whiteCount++;
            } else {
                if (whiteCount >= 1) {
                    return (float) (Math.max(bitmapBounds.left, x - LINE_SIZE) - bitmapBounds.left)
                        / bitmapBounds.width();
                }
                whiteCount = 0;
            }
        }
        return whiteCount > 0 ? (float) (Math.max(bitmapBounds.left, x - LINE_SIZE) - bitmapBounds.left)
            / bitmapBounds.width() : 0;
    }

    private static float getTopBound(final byte[] bmp, final Rect bitmapBounds, final float avgLum) {
        final int h = bitmapBounds.height() / 3;
        int whiteCount = 0;
        int y = 0;
        for (y = bitmapBounds.top; y < bitmapBounds.top + h; y += V_LINE_SIZE) {
            final boolean white = isRectWhite(bmp, bitmapBounds.left + LINE_MARGIN, y, bitmapBounds.right - LINE_MARGIN, y + LINE_SIZE,
                avgLum);
            if (white) {
                whiteCount++;
            } else {
                if (whiteCount >= 1) {
                    return (float) (Math.max(bitmapBounds.top, y - V_LINE_SIZE) - bitmapBounds.top)
                        / bitmapBounds.height();
                }
                whiteCount = 0;
            }
        }
        return whiteCount > 0 ? (float) (Math.max(bitmapBounds.top, y - V_LINE_SIZE) - bitmapBounds.top)
            / bitmapBounds.height() : 0;
    }

    private static float getRightBound(final byte[] bmp, final Rect bitmapBounds, final float avgLum) {
        final int w = bitmapBounds.width() / 3;
        int whiteCount = 0;
        int x = 0;
        for (x = bitmapBounds.right - LINE_SIZE; x > bitmapBounds.right - w; x -= LINE_SIZE) {
            final boolean white = isRectWhite(bmp, x, bitmapBounds.top + LINE_MARGIN, x + LINE_SIZE, bitmapBounds.bottom - LINE_MARGIN,
                avgLum);
            if (white) {
                whiteCount++;
            } else {
                if (whiteCount >= 1) {
                    return (float) (Math.min(bitmapBounds.right, x + 2 * LINE_SIZE) - bitmapBounds.left)
                        / bitmapBounds.width();
                }
                whiteCount = 0;
            }
        }
        return whiteCount > 0 ? (float) (Math.min(bitmapBounds.right, x + 2 * LINE_SIZE) - bitmapBounds.left)
            / bitmapBounds.width() : 1;
    }

    private static float getBottomBound(final byte[] bmp, final Rect bitmapBounds, final float avgLum) {
        final int h = bitmapBounds.height() * 2 / 3;
        int whiteCount = 0;
        int y = 0;
        for (y = bitmapBounds.bottom - V_LINE_SIZE; y > bitmapBounds.bottom - h; y -= V_LINE_SIZE) {
            final boolean white = isRectWhite(bmp, bitmapBounds.left + LINE_MARGIN, y, bitmapBounds.right - LINE_MARGIN, y + LINE_SIZE,
                avgLum);
            if (white) {
                whiteCount++;
            } else {
                if (whiteCount >= 1) {
                    return (float) (Math.min(bitmapBounds.bottom, y + 2 * V_LINE_SIZE) - bitmapBounds.top)
                        / bitmapBounds.height();
                }
                whiteCount = 0;
            }
        }
        return whiteCount > 0 ? (float) (Math.min(bitmapBounds.bottom, y + 2 * V_LINE_SIZE) - bitmapBounds.top)
            / bitmapBounds.height() : 1;
    }

    private static boolean isRectWhite(final byte[] pixels, final int l, final int t, final int r, final int b,
                                       final float avgLum) {
        int count = 0;

        for (final int c : pixels) {
            final float lum = getLum(c);
            if ((lum < avgLum) && ((avgLum - lum) * 10 > avgLum)) {
                count++;
            }
        }
        return ((float) count / pixels.length) < WHITE_THRESHOLD;
    }
}
