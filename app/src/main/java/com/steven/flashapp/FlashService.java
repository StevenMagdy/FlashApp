package com.steven.flashapp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

public class FlashService extends Service {

	private Camera camera;
	private Camera.Parameters cameraParameters;
	private Handler serviceHandler;

	private static final String LOG_TAG = FlashService.class.getName();

	private Runnable turnFlashOnRunnable = new Runnable() {
		@Override
		public void run() {
			openCamera(Camera.CameraInfo.CAMERA_FACING_BACK);
			turnFlashOn();
		}
	};

	private Runnable turnFlashOffRunnable = new Runnable() {
		@Override
		public void run() {
			turnFlashOff();
			closeCamera();
			stopSelf();
		}
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(LOG_TAG, "Service Starting");

		if (!Utils.FLASH_ON) {
			serviceHandler.post(turnFlashOnRunnable);
			Intent notificationIntent = new Intent(this, FlashService.class);
			PendingIntent notificationPendingIntent = PendingIntent.getService(this,
					Utils.NOTIFICATION_PENDING_INTENT_ID,
					notificationIntent,
					0);

			NotificationCompat.Builder notificationBuilder =
					(NotificationCompat.Builder) new NotificationCompat
							.Builder(this)
							.setContentTitle("Flash is On")
							.setSmallIcon(R.drawable.flash_vector)
							.setPriority(NotificationCompat.PRIORITY_MAX)
							.setContentText("Tap to turn flash off")
							.setContentIntent(notificationPendingIntent)
							.setAutoCancel(true)
							.setOngoing(true);

			startForeground(Utils.NOTIFICATION_ID, notificationBuilder.build());

			LocalBroadcastManager.getInstance(this)
					.sendBroadcast(new Intent(Utils.ACTION_FLASH_TURNED_ON));
		} else {
			serviceHandler.post(turnFlashOffRunnable);
			LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Utils
					.ACTION_FLASH_TURNED_OFF));
		}
		return START_STICKY;
	}

	@Nullable
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(LOG_TAG, "Service Created");
		HandlerThread handlerThread = new HandlerThread("FlashServiceThread");
		handlerThread.start();
		serviceHandler = new Handler(handlerThread.getLooper());
		Utils.FLASH_ON = false;
	}

	@Override
	public void onDestroy() {
		serviceHandler.getLooper().quit();
		super.onDestroy();
		Log.i(LOG_TAG, "Service Destroyed");
	}

	private void turnFlashOn() {
		if (camera == null) return;
		cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
		camera.setParameters(cameraParameters);
		camera.startPreview();
		Log.i(LOG_TAG, "Flash turned on");
		Utils.FLASH_ON = true;
	}

	private void openCamera(int cameraId) {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
			if (camera != null) return;
			try {
				camera = Camera.open(cameraId);
				Log.i(LOG_TAG, "Camera Opened");
			} catch (RuntimeException e) {
//				Toast.makeText(this, "Error opening camera", Toast
//						.LENGTH_SHORT).show();
				Log.e(LOG_TAG, "Error opening camera", e);
				stopSelf();

			}
		}
	}

	private void turnFlashOff() {
		if (camera == null) return;
		cameraParameters = camera.getParameters();
		cameraParameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
		camera.setParameters(cameraParameters);
		camera.stopPreview();
		Utils.FLASH_ON = false;
		Log.i(LOG_TAG, "Flash turned off");
	}

	private void closeCamera() {
		if (camera == null) return;
		camera.release();
		camera = null;
		Log.i(LOG_TAG, "Camera Closed");
	}

}
