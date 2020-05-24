package com.example.arcoretest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_PERMISSION_CAMERA = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initApp();
    }

    private void initApp() {
        //Check for camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            //Launch camera with ARCore
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            //Show dialogue to explain why camera is required
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Launch camera with ARCore
            } else {
                //Display error that camera cannot be used without permissions
            }
        }
    }
}
