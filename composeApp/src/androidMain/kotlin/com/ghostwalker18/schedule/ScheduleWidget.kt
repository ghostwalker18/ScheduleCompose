/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ghostwalker18.schedule

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.content.ContextCompat
import com.ghostwalker18.schedule.ScheduleApp.Companion.getInstance
import com.ghostwalker18.schedule.activities.MainActivity
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.utils.AndroidUtils.checkNotificationsPermissions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class ScheduleWidget : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        for (id in appWidgetIds) {
            context.deleteSharedPreferences("WIDGET_$id")
        }
    }

    companion object{
        private val timeFormat = SimpleDateFormat("HH:mm", Locale("ru"))
        private val scope = CoroutineScope(Dispatchers.IO)

        fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val repository = getInstance().scheduleRepository
            val appPreferences = getInstance().preferences
            if (!checkNotificationsPermissions(context, appPreferences)
                || !appPreferences.getBoolean("schedule_notifications", false)
            ) {
                repository.update()
            }
            // Construct the RemoteViews object
            val views = RemoteViews(
                context.packageName,
                R.layout.schedule_widget_wrapper
            )

            val prefs = context.getSharedPreferences(
                "WIDGET_$appWidgetId",
                Context.MODE_PRIVATE
            )

            var group = prefs.getString("group", "last")
            if (group == "last") {
                group = repository.savedGroup ?: context.getString(R.string.not_mentioned)
            }

            val date = Calendar.getInstance()
            when (prefs.getString("day", "")) {
                "tomorrow" -> date.add(Calendar.DAY_OF_YEAR, 1)
            }

            val lessons = repository.getLessons(date, null, group)
            scope.launch {
                lessons.collect { lesson -> updateScheduleWidget(lesson, appWidgetId) }
            }
            val isEdited = prefs.getBoolean("isEdited", false)
            if (isEdited) {
                views.removeAllViews(R.id.widget_wrapper)

                val isDynamicColorsEnabled = prefs.getBoolean("dynamic_colors", false)
                val theme = prefs.getString("theme", "system")!!
                val widgetLayoutId = getRequiredLayout(theme, isDynamicColorsEnabled)
                val scheduleView = RemoteViews(context.packageName, widgetLayoutId)

                val day = prefs.getString("day", "today")!!
                when (day) {
                    "today" -> scheduleView.setTextViewText(R.id.day, context.getString(R.string.today))
                    "tomorrow" -> scheduleView.setTextViewText(R.id.day, context.getString(R.string.tomorrow))
                }

                views.addView(R.id.widget_wrapper, scheduleView)
            }

            views.setTextViewText(R.id.group, context.getString(R.string.for_group) + " " + group)
            views.setTextViewText(
                R.id.updated, context.getString(R.string.updated) + " " + timeFormat.format(date.time)
            )

            //setting action for refresh button: refresh schedule
            val intentRefresh = Intent(
                AppWidgetManager.ACTION_APPWIDGET_UPDATE,
                null, context.applicationContext, ScheduleWidget::class.java
            )
            intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
            val pendingRefresh = PendingIntent.getBroadcast(
                context, appWidgetId,
                intentRefresh, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.updateButton, pendingRefresh)

            //setting action for schedule tap: open app
            val intentOpenApp = Intent(context, MainActivity::class.java)
            val pendingOpenApp = PendingIntent.getActivity(
                context, appWidgetId, intentOpenApp, PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.schedule, pendingOpenApp)

            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        /**
         * Этот метод служит для обновления View виджета новыми занятиями.
         *
         * @author Ипатов Никита
         * @since 5.0
         */
        private fun updateScheduleWidget(lessons: Array<Lesson>, id: Int) {
            val context = getInstance().applicationContext
            val views = RemoteViews(
                context.packageName,
                R.layout.schedule_widget_wrapper
            )
            views.removeAllViews(R.id.schedule)
            if (lessons.isEmpty()) {
                val placeholder = RemoteViews(
                    context.packageName,
                    R.layout.schedule_widget_row_placeholder
                )
                views.addView(R.id.schedule, placeholder)
            }
            var counter = 0
            for (lesson in lessons) {
                counter++
                val lessonItem = RemoteViews(
                    context.packageName,
                    R.layout.schedule_widget_row_item
                )
                if (counter % 2 == 1) lessonItem.setInt(
                    R.id.row, "setBackgroundColor",
                    ContextCompat.getColor(context, R.color.gray_500)
                )
                with(lesson){
                    lessonItem.setTextViewText(R.id.lessonNumber, number)
                    lessonItem.setTextViewText(R.id.subjectName, subject)
                    lessonItem.setTextViewText(R.id.teacherName, teacher)
                    lessonItem.setTextViewText(R.id.roomNumber, room)
                }
                views.addView(R.id.schedule, lessonItem)
            }

            val appWidgetManager = AppWidgetManager.getInstance(context)
            appWidgetManager.partiallyUpdateAppWidget(id, views)
        }

        /**
         * Этот метод используется для получения id нужного лэйаута.
         * @param theme сохраненная тема
         * @param isDynamicColorsEnabled выбрано ли использование динамических цветов
         * @return id лэйаута
         */
        private fun getRequiredLayout(theme: String, isDynamicColorsEnabled: Boolean): Int {
            return when(theme){
                "system" -> if(isDynamicColorsEnabled) R.layout.schedule_widget_dynamic_daynight
                else R.layout.schedule_widget_daynight
                "night" -> if(isDynamicColorsEnabled) R.layout.schedule_widget_dynamic_night
                else R.layout.schedule_widget_night
                "day" -> if(isDynamicColorsEnabled) R.layout.schedule_widget_dynamic_day
                else R.layout.schedule_widget_day
                else -> R.layout.schedule_widget_daynight
            }
        }
    }
}