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
				Intent intent = new Intent(MainActivity.this, FlashService.class);
				startService(intent);
			}
		});
		if (Utils.FLASH_ON) {
			flashImageButton.setImageResource(R.drawable.ic_flash_on_black_48dp);
		} else {
			flashImageButton.setImageResource(R.drawable.ic_flash_off_black_48dp);
		}

		serviceIntentFilter = new IntentFilter();
		serviceIntentFilter.addAction(Utils.ACTION_FLASH_TURNED_ON);
		serviceIntentFilter.addAction(Utils.ACTION_FLASH_TURNED_OFF);

		serviceBroadcastReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				switch (intent.getAction()) {
					case Utils.ACTION_FLASH_TURNED_ON:
						flashImageButton.setImageResource(R.drawable.ic_flash_on_black_48dp);
						break;
					case Utils.ACTION_FLASH_TURNED_OFF:
						flashImageButton.setImageResource(R.drawable.ic_flash_off_black_48dp);
						break;
				}
				flashImageButton.setEnabled(true);
			}
		};
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(serviceBroadcastReceiver, serviceIntentFilter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(serviceBroadcastReceiver);
	}
}
