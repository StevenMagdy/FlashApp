package com.steven.flashapp;

import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {


	private SwitchCompat flashSwitch;
	private Camera camera;
	private Camera.Parameters cameraParameters;
	private boolean hasFlash;
	private boolean flashOn;

	private static final String LOG_TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		flashSwitch = (SwitchCompat) findViewById(R.id.switch_flash);

		flashSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if (!hasFlash) return;
				if (isChecked) {
					openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
					turnFlashOn();
				} else {
					turnFlashOff();
					closeCamera();
				}
			}
		});
	}

	private void openCamera(int cameraId) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			if (camera != null) return;
			try {
				camera = Camera.open(cameraId);
			} catch (RuntimeException e) {
				flashSwitch.setChecked(false);
				Toast.makeText(this, "Error opening camera", Toast.LENGTH_SHORT).show();
				Log.e(LOG_TAG, "Error opening camera", e);
			}
		}
	}

	private void closeCamera() {
		if (camera == null) return;
		camera.release();
		camera = null;
	}

	private void turnFlashOn() {
		if (camera == null) return;
		cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		camera.setParameters(cameraParameters);
		camera.startPreview();
		flashOn = true;
	}

	private void turnFlashOff() {
		if (camera == null) return;
		cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		camera.setParameters(cameraParameters);
		camera.stopPreview();
		flashOn = false;
	}


}
