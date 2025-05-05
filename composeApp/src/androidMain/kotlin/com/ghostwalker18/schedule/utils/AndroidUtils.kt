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

package com.ghostwalker18.schedule.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.ghostwalker18.schedule.R
import com.ghostwalker18.schedule.notifications.NotificationManagerWrapper
import com.russhwolf.settings.Settings
import java.io.File
import java.util.Calendar
import java.util.Date
import java.time.Duration

/**
 * В этом объекте содержаться различные вспомогательные методы, специфичные для Android.
 *
 * @author Ипатов Никита
 * @since 4.1
 */
object AndroidUtils {

    /**
     * Этот метод используется для очистки кэша ApachePOI.
     */
    fun clearPOICache(context: Context) {
        Thread {
            try {
                val cacheDir = File(context.cacheDir, "poifiles")
                val currentDate = Calendar.getInstance()
                cacheDir.listFiles()?.let {
                    for (cachedFile in it) {
                        val creationDate = Calendar.getInstance()
                        creationDate.time = Date(cachedFile.lastModified())
                        //Deleting files created more than 1 day ago
                        if (Duration.between(creationDate.toInstant(), currentDate.toInstant())
                                .toDays() > 1
                        ) cachedFile.delete()
                    }
                }
            } catch (_: Exception) { /*Not required*/ }
        }.start()
    }

    /**
     * Этот метод проверяет разрешения приложения
     * и меняет настройки приложения в соответствии с результатом
     *
     * @return разрешено ли приложению посылать уведомления
     */
    fun checkNotificationsPermissions(
        context: Context,
        preferences: Settings
    ): Boolean {
        var res = true
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            with(preferences){
                putBoolean("update_notifications", false)
                putBoolean("schedule_notifications", false)
                putBoolean("notes_notifications", false)
            }
            res = false
        }
        if (!NotificationManagerWrapper.getInstance(context)
                .isNotificationChannelEnabled(
                    context.getString(R.string.notifications_notification_schedule_update_channel_id)
                )
        ) {
            preferences.putBoolean("schedule_notifications", false)
            res = false
        }
        if (!NotificationManagerWrapper.getInstance(context)
                .isNotificationChannelEnabled(
                    context.getString(R.string.notifications_notification_app_update_channel_id)
                )
        ) {
            preferences.putBoolean("update_notifications", false)
        }
        if (!NotificationManagerWrapper.getInstance(context)
                .isNotificationChannelEnabled(
                    context.getString(R.string.notifications_notification_note_reminder_channel_id)
                )
        ) {
            preferences.putBoolean("notes_notifications", false)
        }
        return res
    }
}