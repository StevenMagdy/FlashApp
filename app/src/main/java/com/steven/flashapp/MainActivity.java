package com.steven.flashapp;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {


	private ImageButton flashImageButton;
	private boolean hasFlash;

	private static final String LOG_TAG = MainActivity.class.getName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(LOG_TAG, "Activity Created");

		setContentView(R.layout.activity_main);
		hasFlash = getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);

		flashImageButton = (ImageButton) findViewById(R.id.imageButton_flash);

		flashImageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!hasFlash) return;
				if (!FlashService.IS_FLASH_ON) {
					Intent intent = new Intent(MainActivity.this, FlashService.class);
					intent.setAction(Utils.ACTION_FLASH_ON);
					startService(intent);
					flashImageButton.setImageResource(R.drawable.ic_flash_on_black_48dp);
				} else {
					Intent intent = new Intent(MainActivity.this, FlashService.class);
					intent.setAction(Utils.ACTION_FLASH_OFF);
					startService(intent);
					flashImageButton.setImageResource(R.drawable.ic_flash_off_black_48dp);
				}
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(LOG_TAG, "Activity Resumed");

		if (FlashService.IS_FLASH_ON) {
			flashImageButton.setImageResource(R.drawable.ic_flash_on_black_48dp);
		} else {
			flashImageButton.setImageResource(R.drawable.ic_flash_off_black_48dp);
		}
	}
}
