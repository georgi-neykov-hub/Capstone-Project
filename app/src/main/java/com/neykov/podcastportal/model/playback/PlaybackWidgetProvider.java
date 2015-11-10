package com.neykov.podcastportal.model.playback;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.neykov.podcastportal.R;

public class PlaybackWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Intent buttonIntent = new Intent(context, PlaybackService.class)
                .setAction(PlaybackService.ACTION_CMD)
                .putExtra(PlaybackService.CMD_NAME, PlaybackService.CMD_PLAY);
        PendingIntent pendingIntent = PendingIntent.getService(context, 25/*request code*/, buttonIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_widget);
        views.setOnClickPendingIntent(R.id.play, pendingIntent);

        appWidgetManager.updateAppWidget(appWidgetIds, views);
    }
}
