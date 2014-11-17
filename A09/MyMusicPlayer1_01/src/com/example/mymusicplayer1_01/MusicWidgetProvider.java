package com.example.mymusicplayer1_01;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

public class MusicWidgetProvider extends AppWidgetProvider {
	private static final String TAG = "MusicWidgetProvider";

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		Log.v(TAG, "onUpdate()");

		final int N = appWidgetIds.length;
		Intent serviceIntent = new Intent(context, MusicPlayerService.class);

		for (int i = 0; i < N; i++) {
			int appWidgetId = appWidgetIds[i];
			RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
					R.layout.widgetlayout);

			Log.w(TAG, "... WidgetExample " + appWidgetId);
			remoteViews.setViewVisibility(R.id.widget_linearlayout_controls,
					View.INVISIBLE);

			Log.w(TAG, "... textview_opentheapp visible");

			remoteViews.setViewVisibility(R.id.widget_textview_opentheapp,
					View.VISIBLE);

			Intent intentToApp = new Intent(context, MusicListActivity.class);
			PendingIntent pendingIntentToApp = PendingIntent.getActivity(
					context, 0, intentToApp, 0);
			remoteViews.setOnClickPendingIntent(
					R.id.widget_textview_opentheapp, pendingIntentToApp);
			appWidgetManager.updateAppWidget(appWidgetId, remoteViews);

			// Update the widgets via the service
			context.startService(serviceIntent);
		}
	}
}
