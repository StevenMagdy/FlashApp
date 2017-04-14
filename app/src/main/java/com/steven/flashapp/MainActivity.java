package com.steven.flashapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

public class MainActivity extends AppCompatActivity {

	private ImageButton flashImageButton;
	private boolean hasFlash;
	private boolean flashOn;
	private IntentFilter serviceIntentFilter;
	private BroadcastReceiver serviceBroadcastReceiver;

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
				flashImageButton.setEnabled(false);
				if (!flashOn) {
					Intent intent = new Intent(MainActivity.this, FlashService.class);
					intent.setAction(Utils.ACTION_TURN_FLASH_ON);
					startService(intent);
				} else {
					Intent intent = new Intent(MainActivity.this, FlashService.class);
					intent.setAction(Utils.ACTION_TURN_FLASH_OFF);
					startService(intent);
				}
			}
		});

		serviceIntentFilter = new IntentFilter();
		serviceIntentFilter.addAction(Utils.ACTION_FLASH_TURNED_ON);
		serviceIntentFilter.addAction(Utils.ACTION_FLASH_TURNED_OFF);

		serviceBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (intent.getAction()) {
					case Utils.ACTION_FLASH_TURNED_ON:
						flashImageButton.setImageResource(R.drawable.ic_flash_on_black_48dp);
						flashOn = true;
						break;
					case Utils.ACTION_FLASH_TURNED_OFF:
						flashImageButton.setImageResource(R.drawable.ic_flash_off_black_48dp);
						flashOn = false;
						break;
				}
				flashImageButton.setEnabled(true);
			}
		};
	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.i(LOG_TAG, "Activity Resumed");
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(serviceBroadcastReceiver, serviceIntentFilter);
	}

	@Override
	protected void onPause() {
		super.onPause();
		Log.i(LOG_TAG, "Activity Paused");
		LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceBroadcastReceiver);
	}
}
