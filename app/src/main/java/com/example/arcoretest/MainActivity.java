package com.example.arcoretest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.ArCoreApk;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.core.exceptions.UnavailableDeviceNotCompatibleException;
import com.google.ar.core.exceptions.UnavailableUserDeclinedInstallationException;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

public class MainActivity extends AppCompatActivity {

    private final int REQUEST_CODE_PERMISSION_CAMERA = 10;

    private ArFragment arFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initApp();
    }

    private void initApp() {
        //Check for camera permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Camera permissions are granted", Toast.LENGTH_LONG).show();
            if (apkArCoreInstalled()) {
                //Launch camera with ARCore
                initArSession();
                Toast.makeText(this, "Should launch ARCore now", Toast.LENGTH_LONG).show();
            }
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            //Show dialogue to explain why camera is required
            Toast.makeText(this, "The camera is required for this app to work!", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "Requesting permissions", Toast.LENGTH_LONG).show();
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CODE_PERMISSION_CAMERA);
        }


    }

    private void initArSession() {
        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.arFragment);

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            Anchor anchor = hitResult.createAnchor();

            ModelRenderable.builder()
                    .setSource(this, Uri.parse("Rooster.sfb"))
                    .build()
                    .thenAccept(modelRenderable -> addModelToScene(anchor, modelRenderable))
                    .exceptionally(throwable -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setMessage(throwable.getMessage()).show();
                        return null;
                    });
        });
    }

    private void addModelToScene(Anchor anchor, ModelRenderable modelRenderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        TransformableNode transformableNode = new TransformableNode(arFragment.getTransformationSystem());
        transformableNode.setParent(anchorNode);
        transformableNode.setRenderable(modelRenderable);
        arFragment.getArSceneView().getScene().addChild(anchorNode);
        transformableNode.select();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (apkArCoreInstalled()) {
                    //Launch camera with ARCore
                    initArSession();
                    Toast.makeText(this, "Should launch ARCore now", Toast.LENGTH_LONG).show();
                }
            } else {
                //Display error that camera cannot be used without permissions
                Toast.makeText(this, "Error - Camera Permissions Required", Toast.LENGTH_LONG).show();
            }
        }
    }

    public boolean apkArCoreInstalled() {
        try {
            switch (ArCoreApk.getInstance().requestInstall(this, true)) {
                case INSTALLED:
                    Toast.makeText(this, "ARCore is already installed", Toast.LENGTH_LONG).show();
                    return true;
                case INSTALL_REQUESTED:
                    Toast.makeText(this, "ARCore install is requested", Toast.LENGTH_LONG).show();
                    return false;
            }
        } catch (UnavailableDeviceNotCompatibleException e) {
            Toast.makeText(this, "The device is not compatible with ARCore", Toast.LENGTH_LONG).show();
        } catch (UnavailableUserDeclinedInstallationException e) {
            Toast.makeText(this, "The user declined installation of ARCore", Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
