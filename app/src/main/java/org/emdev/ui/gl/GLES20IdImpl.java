package org.emdev.ui.gl;

import android.opengl.GLES20;

import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

public class GLES20IdImpl extends GLId {
    private static final int[] mTempIntArray = new int[1];

    public int generateTexture() {
        GLES20.glGenTextures(1, mTempIntArray, 0);
        GLES20Canvas.checkError();
        return mTempIntArray[0];
    }

    public static void glGenBuffers(int n, int[] buffers, int offset) {
        GLES20.glGenBuffers(n, buffers, offset);
        GLES20Canvas.checkError();
    }

    public static void glDeleteTextures(GL11 gl, int n, int[] textures, int offset) {
        GLES20.glDeleteTextures(n, textures, offset);
        GLES20Canvas.checkError();
    }

    public static void glDeleteBuffers(GL11 gl, int n, int[] buffers, int offset) {
        GLES20.glDeleteBuffers(n, buffers, offset);
        GLES20Canvas.checkError();
    }

    public static void glDeleteFramebuffers(GL11ExtensionPack gl11ep, int n, int[] buffers, int offset) {
        GLES20.glDeleteFramebuffers(n, buffers, offset);
        GLES20Canvas.checkError();
    }
}
