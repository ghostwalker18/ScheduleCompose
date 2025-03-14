package com.ghostwalker18.schedule;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import com.ghostwalker18.schedule.models.Lesson;
import com.ghostwalker18.schedule.models.ScheduleRepository;
import com.ghostwalker18.schedule.utils.AndroidUtils;
import com.ghostwalker18.schedule.activities.MainActivity;
import com.ghostwalker18.schedule.activities.WidgetSettingsActivity;
import com.russhwolf.settings.Settings;
import kotlinx.coroutines.flow.Flow;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Этот класс реализует функциональность виджета приложения по показу расписания на заданный день.
 *
 * @author Ипатов Никита
 * @since 2.2
 * @see WidgetSettingsActivity
 */
public class ScheduleWidget
        extends AppWidgetProvider {
    static final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

    public static void updateAppWidget(@NonNull Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        final ScheduleRepository repository = ScheduleApp.Companion.getInstance().scheduleRepository;
        Settings appPreferences = ScheduleApp.Companion.getInstance().preferences;
        if(!AndroidUtils.INSTANCE.checkNotificationsPermissions(context, appPreferences)
                || !appPreferences.getBoolean("schedule_notifications", false)){
            repository.update();
        }
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(),
                R.layout.schedule_widget_wrapper);

        SharedPreferences prefs = context.getSharedPreferences("WIDGET_" + appWidgetId,
                Context.MODE_PRIVATE);

        String group = prefs.getString("group", "last");
        if(group.equals("last")) {
            group = repository.getSavedGroup();
            if(group == null)
                group = context.getString(R.string.not_mentioned);
        }

        Calendar date = Calendar.getInstance();
        switch (prefs.getString("day", "")){
            case "tomorrow":
                date.add(Calendar.DAY_OF_YEAR,  1);
                break;
        }

        Flow<Lesson[]> lessons = repository.getLessons(date, group, null);
        lessons.observeForever(new ScheduleObserver(appWidgetId));

        boolean isEdited = prefs.getBoolean("isEdited", false);
        if(isEdited){
            views.removeAllViews(R.id.widget_wrapper);

            boolean isDynamicColorsEnabled = prefs.getBoolean("dynamic_colors", false);
            String theme = prefs.getString("theme", "system");
            int widgetLayoutId = getRequiredLayout(theme, isDynamicColorsEnabled);
            RemoteViews scheduleView = new RemoteViews(context.getPackageName(), widgetLayoutId);

            String day = prefs.getString("day", "today");
            switch (day){
                case "today":
                    scheduleView.setTextViewText(R.id.day, context.getString(R.string.today));
                    break;
                case "tomorrow":
                    scheduleView.setTextViewText(R.id.day, context.getString(R.string.tomorrow));
                    break;
            }

            views.addView(R.id.widget_wrapper, scheduleView);
        }

        views.setTextViewText(R.id.group, context.getString(R.string.for_group) + " " + group);
        views.setTextViewText(R.id.updated,context.getString(R.string.updated) + " " +
                timeFormat.format(date.getTime()));

        //setting action for refresh button: refresh schedule
        Intent intentRefresh = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE,
                null, context.getApplicationContext(), ScheduleWidget.class);
        intentRefresh.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS , new int[]{appWidgetId});
        PendingIntent pendingRefresh = PendingIntent.getBroadcast(context, appWidgetId,
                intentRefresh, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.updateButton, pendingRefresh);

        //setting action for schedule tap: open app
        Intent intentOpenApp = new Intent(context, MainActivity.class);
        PendingIntent pendingOpenApp = PendingIntent.getActivity(context, appWidgetId,
                intentOpenApp, PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.schedule, pendingOpenApp);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, @NonNull int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onDeleted(Context context, @NonNull int[] appWidgetIds) {
        for(int id : appWidgetIds){
            context.deleteSharedPreferences("WIDGET_" + id);
        }
    }

    /**
     * Этот метод используется для получения id нужного лэйаута.
     * @param theme сохраненная тема
     * @param isDynamicColorsEnabled выбрано ли использование динамических цветов
     * @return id лэйаута
     */
    private static int getRequiredLayout(String theme, boolean isDynamicColorsEnabled){
        if(isDynamicColorsEnabled){
            switch (theme){
                case "system":
                    return R.layout.schedule_widget_dynamic_daynight;
                case "night":
                    return R.layout.schedule_widget_dynamic_night;
                case "day":
                    return R.layout.schedule_widget_dynamic_day;
            }
        }
        else {
            switch (theme){
                case "system":
                    return R.layout.schedule_widget_daynight;
                case "night":
                    return R.layout.schedule_widget_night;
                case "day":
                    return R.layout.schedule_widget_day;
            }
        }
        return R.layout.schedule_widget_daynight;
    }

    /**
     * Этот класс служит для обновления View виджета новыми занятиями.
     *
     * @author Ипатов Никита
     * @since 2.3
     */
    private static class ScheduleObserver
            implements Observer<Lesson[]> {
        private final int id;

        public ScheduleObserver(int widgetId){
            id = widgetId;
        }

        @Override
        public void onChanged(@NonNull Lesson[] lessons) {
            Context context = ScheduleApp.Companion.getInstance().getApplicationContext();
            RemoteViews views = new RemoteViews(context.getPackageName(),
                    R.layout.schedule_widget_wrapper);
            views.removeAllViews(R.id.schedule);
            if(lessons.length == 0) {
                RemoteViews placeholder = new RemoteViews(context.getPackageName(),
                        R.layout.schedule_widget_row_placeholder);
                views.addView(R.id.schedule, placeholder);
            }
            int counter = 0;
            for(Lesson lesson : lessons){
                counter++;
                RemoteViews lessonItem = new RemoteViews(context.getPackageName(),
                        R.layout.schedule_widget_row_item);
                if(counter % 2 == 1)
                    lessonItem.setInt(R.id.row, "setBackgroundColor",
                            ContextCompat.getColor(context, R.color.gray_500));
                lessonItem.setTextViewText(R.id.lessonNumber, lesson.lessonNumber);
                lessonItem.setTextViewText(R.id.subjectName, lesson.subject);
                lessonItem.setTextViewText(R.id.teacherName, lesson.teacher);
                lessonItem.setTextViewText(R.id.roomNumber, lesson.roomNumber);
                views.addView(R.id.schedule, lessonItem);
            }

            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            appWidgetManager.partiallyUpdateAppWidget(id, views);
        }
    }
}
