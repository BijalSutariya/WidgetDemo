package e.matrixhive.widgetdemo;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.util.Log;
import android.widget.RemoteViews;

import static android.content.ContentValues.TAG;

/**
 * Implementation of App Widget functionality.
 */
public class NewAppWidget extends AppWidgetProvider {

    private int batteryLevel = 0;

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.i(TAG, "onUpdate: ");

        int currentLevel = calculateBatteryLevel(context);
        if (batteryChanged(currentLevel)) {
            batteryLevel = currentLevel;
            Log.i(TAG, "onUpdate: Battery level changed");
        }
        updateViews(context);
    }

    private boolean batteryChanged(int currentLevelLeft) {
        return (batteryLevel != currentLevelLeft);
    }

    private int calculateBatteryLevel(Context context) {
        Intent batteryIntent = context.getApplicationContext().registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = 0;
        int scale =0;
        if (batteryIntent != null) {
            level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        }
        return level * 100 / scale;
    }

    private void updateViews(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.new_app_widget);
        views.setTextViewText(R.id.appwidget_text, batteryLevel + "%");
        ComponentName componentName = new ComponentName(context, NewAppWidget.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        appWidgetManager.updateAppWidget(componentName, views);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        AppWidgetManager widgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = widgetManager.getAppWidgetIds(new ComponentName(context, NewAppWidget.class));
        Intent updateIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, context, NewAppWidget.class);
        updateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appWidgetIds);
        context.sendBroadcast(updateIntent);
    }
}

