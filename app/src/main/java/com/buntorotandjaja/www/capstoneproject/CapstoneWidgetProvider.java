package com.buntorotandjaja.www.capstoneproject;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

/**
 * Implementation of App Widget functionality.
 */
public class CapstoneWidgetProvider extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.capstone_widget);

        boolean itemSold = CheckDBListing.getSold(context);

        // Create intent to launch app when clicked
        Intent intent = itemSold ? new Intent(context, ItemListActivity.class) :
                new Intent(context, LoginActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,0, intent, 0);

        // change image if there's an item sold
        int image = itemSold ? R.drawable.garage_sale_sold : R.drawable.garage_sale_icon;
        // set image accordingly
        views.setImageViewResource(R.id.imageView_capstoneWidget, image);
        // TODO to expand the idea, put extra in intent and extract from login
        // TODO then display page according to the extra data

        views.setOnClickPendingIntent(R.id.imageView_capstoneWidget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

