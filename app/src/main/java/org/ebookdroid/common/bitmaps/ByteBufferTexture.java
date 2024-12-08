/*
 * Copyright (C) 2010 The Android Open Source Project
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

package org.ebookdroid.common.bitmaps;

import android.graphics.Bitmap;
import android.opengl.GLUtils;

import org.emdev.common.log.LogContext;
import org.emdev.common.log.LogManager;
import org.emdev.ui.gl.BasicTexture;
import org.emdev.ui.gl.GLCanvas;
import org.emdev.ui.gl.GLES20Canvas;

import javax.microedition.khronos.opengles.GL10;

// UploadedTextures use a Bitmap for the content of the texture.
//
// Subclasses should implement onGetBitmap() to provide the Bitmap and
// implement onFreeBitmap(mBitmap) which will be called when the Bitmap
// is not needed anymore.
//
// isContentValid() is meaningful only when the isLoaded() returns true.
// It means whether the content needs to be updated.
//
// The user of this class should call recycle() when the texture is not
// needed anymore.
//
// By default an UploadedTexture is opaque (so it can be drawn faster without
// blending). The user or subclass can override it using setOpaque().
public class ByteBufferTexture extends BasicTexture {

    @SuppressWarnings("unused")
    private static final LogContext LCTX = LogManager.root().lctx("Texture");

    private static int[] sTextureId = new int[1];

    private static float[] sCropRect = new float[4];

    private boolean mOpaque = true;

    protected ByteBufferBitmap mBitmap;

    public ByteBufferTexture(final ByteBufferBitmap bitmap) {
        super(null, 0, STATE_UNLOADED);
        this.mBitmap = bitmap;
        setSize(mBitmap.getWidth(), mBitmap.getHeight());
    }

    protected void freeBitmap() {
        if (mBitmap != null) {
            mBitmap = null;
        }
    }

    @Override
    public int getWidth() {
        return mWidth;
    }

    @Override
    public int getHeight() {
        return mHeight;
    }

    /**
     * Updates the content on GPU's memory.
     *
     * @param canvas
     */
    public void updateContent(final GLCanvas canvas) {
        if (!isLoaded()) {
            uploadToCanvasGl2(canvas);
        }
    }

    private void uploadToCanvasGl2(GLCanvas canvas) {
        Bitmap bitmap = mBitmap.toBitmap().getBitmap();
        if (bitmap != null) {
            try {
                int bWidth = bitmap.getWidth();
                int bHeight = bitmap.getHeight();
                int texWidth = getTextureWidth();
                int texHeight = getTextureHeight();

                // Upload the bitmap to a new texture.
                mId = ((GLES20Canvas) canvas).getGLId().generateTexture();
                canvas.setTextureParameters(this);

                if (bWidth == texWidth && bHeight == texHeight) {
                    canvas.initializeTexture(this, bitmap);
                } else {
                    int format = GLUtils.getInternalFormat(bitmap);
                    int type = GLUtils.getType(bitmap);

                    canvas.initializeTextureSize(this, format, type);
                    canvas.texSubImage2D(this, 0, 0, bitmap, format, type);
                }
            } finally {
                freeBitmap();
            }
            // Update texture state.
            setAssociatedCanvas(canvas);
            mState = STATE_LOADED;
        } else {
            mState = STATE_ERROR;
            LCTX.e("bbt2,Texture load fail, no bitmap");
        }
    }

    @Override
    protected boolean onBind(final GLCanvas canvas) {
        updateContent(canvas);
        return isLoaded();
    }

    @Override
    protected int getTarget() {
        return GL10.GL_TEXTURE_2D;
    }

    public void setOpaque(final boolean isOpaque) {
        mOpaque = isOpaque;
    }

    @Override
    public boolean isOpaque() {
        return mOpaque;
    }

    @Override
    public void recycle() {
        super.recycle();
        if (mBitmap != null) {
            freeBitmap();
        }
    }
}
