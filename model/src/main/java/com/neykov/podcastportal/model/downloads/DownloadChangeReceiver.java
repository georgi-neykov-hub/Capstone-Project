package com.neykov.podcastportal.model.downloads;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class DownloadChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent serviceIntent = new Intent(context, DownloadIntentService.class)
                .setAction(intent.getAction())
                .putExtras(intent);
        context.startService(serviceIntent);
    }
}
