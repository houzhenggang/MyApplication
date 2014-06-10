
package com.miracle.sphonesocialwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;

public class SphoneTimeAppWidgetProvider extends AppWidgetProvider {

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.sphone_social_appwidget);
        Intent i = new Intent();
        i.setClassName("com.android.deskclock", "com.android.deskclock.AlarmClock");
        views.setOnClickPendingIntent(R.id.top_time,
                PendingIntent.getActivity(context, 0,
                        i, 0));
        appWidgetManager.updateAppWidget(appWidgetIds, views);

        SphoneTimeService.sAppWidgetIds = appWidgetIds;
        Intent intent = new Intent(context, SphoneTimeService.class);
        context.startService(intent);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        SphoneTimeService.sAppWidgetIds = appWidgetIds;
        super.onDeleted(context, appWidgetIds);
    }

}
