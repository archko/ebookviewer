package org.emdev.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import org.emdev.ui.progress.IProgressIndicator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;

public final class FileUtils {

    private static final ArrayList<String> mounts = new ArrayList<>();
    private static final ArrayList<String> mountsPR = new ArrayList<>();
    private static final ArrayList<String> aliases = new ArrayList<>();
    private static final ArrayList<String> aliasesPR = new ArrayList<>();

    public static void init() {
        for (final File f : new File(Environment.getExternalStorageDirectory().getPath()).listFiles()) {
            if (f.isDirectory()) {
                try {
                    final String cp = f.getCanonicalPath();
                    final String ap = f.getAbsolutePath();
                    if (!cp.equals(ap)) {
                        aliases.add(ap);
                        aliasesPR.add(ap + "/");
                        mounts.add(cp);
                        mountsPR.add("/");
                    }
                } catch (final IOException ex) {
                    System.err.println(ex.getMessage());
                }
            }
        }
    }

    private FileUtils() {
    }

    public static String getFileSize(final long size) {
        if (size > 1073741824) {
            return String.format(Locale.getDefault(), "%.2f", size / 1073741824.0) + " GB";
        } else if (size > 1048576) {
            return String.format(Locale.getDefault(), "%.2f", size / 1048576.0) + " MB";
        } else if (size > 1024) {
            return String.format(Locale.getDefault(), "%.2f", size / 1024.0) + " KB";
        } else {
            return size + " B";
        }

    }

    public static String getFileDate(final long time) {
        return new SimpleDateFormat("dd MMM yyyy").format(time);
    }

    public static String getAbsolutePath(final File file) {
        return file != null ? file.getAbsolutePath() : null;
    }

    public static String getCanonicalPath(final File file) {
        try {
            return file != null ? file.getCanonicalPath() : null;
        } catch (final IOException ex) {
            return null;
        }
    }

    public static String invertMountPrefix(final String fileName) {
        for (int i = 0, n = Math.min(aliases.size(), mounts.size()); i < n; i++) {
            final String alias = aliases.get(i);
            final String mount = mounts.get(i);
            if (fileName.equals(alias)) {
                return mount;
            }
            if (fileName.equals(mount)) {
                return alias;
            }
        }
        for (int i = 0, n = Math.min(aliasesPR.size(), mountsPR.size()); i < n; i++) {
            final String alias = aliasesPR.get(i);
            final String mount = mountsPR.get(i);
            if (fileName.startsWith(alias)) {
                return mount + fileName.substring(alias.length());
            }
            if (fileName.startsWith(mount)) {
                return alias + fileName.substring(mount.length());
            }
        }
        return null;
    }

    public static String getExtensionWithDot(final File file) {
        if (file == null) {
            return "";
        }
        final String name = file.getName();
        final int index = name.lastIndexOf(".");
        if (index == -1) {
            return "";
        }
        return name.substring(index);
    }
    
    public static final File getDiskCacheDir(final Context context, final String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment
            .getExternalStorageState()) ? getExternalCacheDir(
            context).getPath() : context.getCacheDir().getPath();

        if (TextUtils.isEmpty(uniqueName)) {
            return new File(cachePath);
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    /**
     * Get the external app cache directory
     *
     * @param context The {@link Context} to use
     * @return The external cache directory
     */
    public static final File getExternalCacheDir(final Context context) {
        final File mCacheDir = context.getExternalCacheDir();
        if (mCacheDir != null) {
            return mCacheDir;
        }

        /* Before Froyo we need to construct the external cache dir ourselves */
        final String dir = "/Android/data/" + context.getPackageName() + "/cache/";
        return new File(Environment.getExternalStorageDirectory().getPath() + dir);
    }

    public static FilePath parseFilePath(final String path, final Collection<String> extensions) {
        final File file = new File(path);
        final FilePath result = new FilePath();
        result.path = LengthUtils.safeString(file.getParent());
        result.name = file.getName();

        for (final String ext : extensions) {
            final String dext = "." + ext;
            if (result.name.endsWith(dext)) {
                result.extWithDot = dext;
                result.name = result.name.substring(0, result.name.length() - ext.length() - 1);
                break;
            }
        }
        return result;
    }

    public static void copy(final File source, final File target) throws IOException {
        if (!source.exists()) {
            return;
        }
        final long length = source.length();
        final int bufsize = MathUtils.adjust((int) length, 1024, 512 * 1024);

        final byte[] buf = new byte[bufsize];
        int l = 0;
        InputStream ins = null;
        OutputStream outs = null;
        try {
            ins = new FileInputStream(source);
            outs = new FileOutputStream(target);
            for (l = ins.read(buf); l > -1; l = ins.read(buf)) {
                outs.write(buf, 0, l);
            }
        } finally {
            if (outs != null) {
                try {
                    outs.close();
                } catch (final IOException ignored) {
                }
            }
            if (ins != null) {
                try {
                    ins.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    public static int move(final File sourceDir, final File targetDir, final String[] fileNames,
                           final IProgressIndicator progress) {
        int count = 0;
        int processed = 0;
        final int updates = Math.max(1, fileNames.length / 20);

        boolean renamed = true;

        final byte[] buf = new byte[128 * 1024];
        int length = 0;
        for (final String file : fileNames) {
            final File source = new File(sourceDir, file);
            final File target = new File(targetDir, file);
            processed++;

            renamed = renamed && source.renameTo(target);
            if (renamed) {
                count++;
                continue;
            }

            try {
                InputStream ins = null;
                OutputStream outs = null;
                try {
                    ins = new FileInputStream(source);
                    outs = new FileOutputStream(target);
                    for (length = ins.read(buf); length > -1; length = ins.read(buf)) {
                        outs.write(buf, 0, length);
                    }
                } finally {
                    if (outs != null) {
                        try {
                            outs.close();
                        } catch (final IOException ignored) {
                        }
                    }
                    if (ins != null) {
                        try {
                            ins.close();
                        } catch (final IOException ignored) {
                        }
                    }
                }
                source.delete();
                count++;

                if (progress != null && (processed % updates) == 0) {
                    progress.setProgressDialogMessage(0, processed, fileNames.length);
                }
            } catch (final IOException ex) {
                System.err.println(ex.getMessage());
            }
        }
        return count;
    }

    public static void copy(final InputStream source, final OutputStream target) throws IOException {
        ReadableByteChannel in = null;
        WritableByteChannel out = null;
        try {
            in = Channels.newChannel(source);
            out = Channels.newChannel(target);
            final ByteBuffer buf = ByteBuffer.allocateDirect(512 * 1024);
            while (in.read(buf) > 0) {
                buf.flip();
                out.write(buf);
                buf.flip();
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException ex) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    public static void copy(final InputStream source, final OutputStream target, final int bufsize,
                            final CopingProgress progress) throws IOException {
        ReadableByteChannel in = null;
        WritableByteChannel out = null;
        try {
            in = Channels.newChannel(source);
            out = Channels.newChannel(target);
            final ByteBuffer buf = ByteBuffer.allocateDirect(bufsize);
            long read = 0;
            while (in.read(buf) > 0) {
                buf.flip();
                read += buf.remaining();
                if (progress != null) {
                    progress.progress(read);
                }
                out.write(buf);
                buf.flip();
            }
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (final IOException ex) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (final IOException ignored) {
                }
            }
        }
    }

    public interface CopingProgress {

        void progress(long bytes);
    }

    public static final class FilePath {

        public String path;
        public String name;
        public String extWithDot;

        public File toFile() {
            return new File(path, name + LengthUtils.safeString(extWithDot));
        }
    }
}
