package com.example.submissionaplikasistory.view

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import android.widget.Toast
import androidx.core.net.toUri
import com.example.submissionaplikasistory.R
import com.example.submissionaplikasistory.view.service.StackWidgetService

/**
 * Implementation of App Widget functionality.
 */
class PostWidget : AppWidgetProvider() {


    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    @SuppressLint("StringFormatMatches")
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action != null ) {
            if (intent.action == TOAST_ACTION) {
                val viewIndex = intent.getStringExtra(EXTRA_ITEM)
                Toast.makeText(context, context?.getString(R.string.pick_stack_widget, viewIndex), Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {

        private const val TOAST_ACTION = "com.unknown.submission_aplikasi_story.TOAST.ACTION"
        const val EXTRA_ITEM = "com.unknown.submission_aplikasi_story.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()

            val views = RemoteViews(context.packageName, R.layout.post_widget)
            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            val toastIntent = Intent(context, PostWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)

            val toastPending = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                } else 0
            )
            views.setPendingIntentTemplate(R.id.stack_view , toastPending)

            appWidgetManager.updateAppWidget(appWidgetId, views)

        }
    }

}