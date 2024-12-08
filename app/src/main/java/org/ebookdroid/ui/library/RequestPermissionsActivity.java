package org.ebookdroid.ui.library;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;

import org.ebookdroid.EBookDroidApp;
import org.ebookdroid.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class RequestPermissionsActivity extends AppCompatActivity {

    private static final int DV_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    .setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                onSuccess();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    DV_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                onSuccess();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == DV_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onSuccess();
            } else {
                onFailure();
            }
        }
    }

    private void onSuccess() {
        EBookDroidApp.getInstance().initAfterPermission();

        Intent intent = new Intent(this, RecentActivity.class);
        startActivity(intent);

        finish();
    }

    private void onFailure() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.error_write_external_storage_permission);
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> finish());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
