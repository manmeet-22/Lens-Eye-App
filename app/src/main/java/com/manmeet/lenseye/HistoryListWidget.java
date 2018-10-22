package com.manmeet.lenseye;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RemoteViews;

/**
 * Implementation of App Widget functionality.
 */
public class HistoryListWidget extends AppWidgetProvider {

    //SharedPreferences sharedPreferences;
/*
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


   *//*     CharSequence widgetText = context.getString(R.string.appwidget_text);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.history_list_widget);
        views.setTextViewText(R.id.appwidget_text, widgetText);
        // Construct the RemoteViews object
        views.removeAllViews(R.id.widget_ingredients_container);
        for (Ingredient ingredient : ingredientList) {
            RemoteViews ingredientView = new RemoteViews(context.getPackageName(),
                    R.layout.ingredient_list_item);
            ingredientView.setTextViewText(R.id.ingredient_name,
                    String.valueOf(ingredient.getIngredient()));

            ingredientView.setTextViewText(R.id.ingredient_quantitiy,
                    String.valueOf(ingredient.getQuantity()) + " " + String.valueOf(ingredient.getMeasure()));
            views.addView(R.id.widget_ingredients_container, ingredientView);
        }
*//*
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.history_list_widget);
        views.setImageViewResource(R.id.widget_image, R.drawable.ic_remove_red_eye_blue_grey_700_36dp);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }*/

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them

        for (int appWidgetId : appWidgetIds) {
            //updateAppWidget(context, appWidgetManager, appWidgetId);
            // Create an Intent to launch ExampleActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            // Get the layout for the App Widget and attach an on-click listener
            // to the button
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.history_list_widget);
            views.setImageViewResource(R.id.widget_image, R.drawable.ic_remove_red_eye_blue_grey_700_36dp);
            views.setOnClickPendingIntent(R.id.widget_image, pendingIntent);

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views);

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

