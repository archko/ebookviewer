package org.emdev.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class PermissionUtils {

    public static boolean hasStoragePermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                Log.i("ABCD", "此手机是Android 11或更高的版本，且已获得访问所有文件权限");
                return true;
            } else {
                Log.i("ABCD", "此手机是Android 11或更高的版本，且没有访问所有文件权限");
            }
            return false;
        } else {
            Log.i("ABCD", "此手机版本小于Android 11，版本为：API ${Build.VERSION.SDK_INT}，不需要申请文件管理权限");
            return (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) == PackageManager.PERMISSION_GRANTED
            );
        }
    }
}
