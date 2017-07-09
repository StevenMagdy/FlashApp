package com.steven.flashapp;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.RemoteViews;

public class WidgetProvider extends AppWidgetProvider {

	private static final String LOG_TAG = WidgetProvider.class.getName();

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

		super.onUpdate(context, appWidgetManager, appWidgetIds);
		boolean hasFlash = context.getPackageManager().hasSystemFeature(PackageManager
				.FEATURE_CAMERA_FLASH);
		if (!hasFlash) return;

		for (int appWidgetId : appWidgetIds) {

			RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget);

			Intent intent = new Intent(context, FlashService.class);
			PendingIntent pendingIntent = PendingIntent.getService(context,
					Utils.WIDGET_PENDING_INTENT_ID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
			remoteViews.setOnClickPendingIntent(R.id.imageButton_widget_flash, pendingIntent);

			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		super.onReceive(context, intent);
	}
}
