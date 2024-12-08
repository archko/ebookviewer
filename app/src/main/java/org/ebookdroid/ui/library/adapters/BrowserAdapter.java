package org.ebookdroid.ui.library.adapters;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.ebookdroid.R;
import org.ebookdroid.common.settings.LibSettings;
import org.ebookdroid.common.settings.SettingsManager;
import org.ebookdroid.common.settings.books.BookSettings;
import org.emdev.ui.adapters.BaseViewHolder;
import org.emdev.utils.FileUtils;
import org.emdev.utils.LengthUtils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * 书架视图中的浏览列表适配器
 */
public class BrowserAdapter extends BaseAdapter implements Comparator<File> {

    private final FileFilter filter;

    private File currentDirectory;
    private List<File> files = Collections.emptyList();

    public BrowserAdapter(final FileFilter filter) {
        this.filter = filter;
    }

    @Override
    public int getCount() {
        return files.size();
    }

    @Override
    public File getItem(final int i) {
        return 0 <= i && i < files.size() ? files.get(i) : null;
    }

    @Override
    public long getItemId(final int i) {
        return i;
    }

    @Override
    public View getView(final int i, final View view, final ViewGroup parent) {
        final ViewHolder holder = BaseViewHolder.getOrCreateViewHolder(
            ViewHolder.class,
            R.layout.browseritem,
            view,
            parent);

        final File file = getItem(i);
        String ap = file.getAbsolutePath();

        holder.textView.setText(file.getName());

        if (file.isDirectory()) {
            Set<String> autoScanDirs = LibSettings.current().autoScanDirs;
            String mp = FileUtils.invertMountPrefix(ap);
            final boolean watched = autoScanDirs.contains(ap) || (mp != null && autoScanDirs.contains(mp));
            holder.imageView.setImageResource(watched ? R.drawable.browser_item_folder_watched : R.drawable.browser_item_folder_open);
            holder.info.setText("");
            holder.fileSize.setText("");
            holder.progressbar.setMax(0);
            holder.progressbar.setProgress(0);
        } else {
            BookSettings settings = SettingsManager.getBookSettings(file.getAbsolutePath());
            final boolean wasRead = settings != null;

            AdapterUtils.setIcon(file, wasRead, holder.imageView);
            AdapterUtils.setProgress(holder.progressbar, settings);

            holder.info.setText(FileUtils.getFileDate(file.lastModified()));
            holder.fileSize.setText(FileUtils.getFileSize(file.length()));
        }

        return holder.getView();
    }

    public void setCurrentDirectory(final File currentDirectory) {
        if (currentDirectory.getAbsolutePath().startsWith("/sys")) {
            return;
        }
        this.currentDirectory = currentDirectory;

        final File[] files = currentDirectory.listFiles(filter);

        if (LengthUtils.isNotEmpty(files)) {
            Arrays.sort(files, this);
        }

        setFiles(files);
    }

    private void setFiles(final File[] files) {
        final List<File> ff = LengthUtils.isNotEmpty(files) ? new ArrayList<>(Arrays.asList(files)) : new ArrayList<>();
        this.files = ff;
        notifyDataSetChanged();
    }

    public File getCurrentDirectory() {
        return currentDirectory;
    }

    public void remove(final File file) {
        if (files.remove(file)) {
            notifyDataSetChanged();
        }
    }

    @Override
    public int compare(final File o1, final File o2) {
        if (o1 == null && o2 == null) {
            return 0;
        }
        if (o1.isDirectory() && o2.isFile()) return -1;
        if (o1.isFile() && o2.isDirectory()) return 1;
        if (o1.lastModified() - o2.lastModified() > 0) {
            return -1;
        } else if (o1.lastModified() - o2.lastModified() < 0) { //jdk7以上需要对称,自反,传递性.
            return 1;
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends BaseViewHolder {

        TextView textView;
        ImageView imageView;
        TextView info;
        TextView fileSize;
        ProgressBar progressbar;

        @Override
        public void init(final View convertView) {
            super.init(convertView);
            textView = convertView.findViewById(R.id.browserItemText);
            imageView = convertView.findViewById(R.id.browserItemIcon);
            info = convertView.findViewById(R.id.browserItemInfo);
            fileSize = convertView.findViewById(R.id.browserItemfileSize);
            progressbar = convertView.findViewById(R.id.progress_horizontal);
        }
    }
}
