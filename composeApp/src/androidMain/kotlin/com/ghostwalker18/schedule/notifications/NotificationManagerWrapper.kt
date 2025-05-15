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

package com.ghostwalker18.schedule.notifications

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.text.TextUtils
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationChannelCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx. core. app. NotificationCompat.Action
import com.ghostwalker18.schedule.R

/**
 * Этот класс представляет собой надстройку над NotificationManagerCompat для удобства использования.
 *
 * @author Ипатов Никита
 * @author RuStore
 * @since 4.1
 */
class NotificationManagerWrapper private constructor(
    private val notificationManager: NotificationManagerCompat
) {

    /**
     * Этот метод позволяет создать канал для отправки и получения push-уведомлений.
     * @param channelId ID канала
     * @param channelName имя канала
     * @param channelDescription краткое описание канала для пункта в настройках
     */
    fun createNotificationChannel(
        channelId: String,
        channelName: String?,
        channelDescription: String?
    ){
        val builder = NotificationChannelCompat.Builder(
            channelId,
            NotificationManagerCompat.IMPORTANCE_DEFAULT
        )
        builder.setName(channelName)
        if (channelDescription != null) builder.setDescription(channelDescription)
        notificationManager.createNotificationChannel(builder.build())
    }

    /**
     * Этот метод позволяет показать push-уведомление.
     * @param context контекст приложения
     * @param data сообщение
     * @param intent действие при нажатии на уведомление
     * @param actions действия внутри уведомления
     */
    fun showNotification(
        context: Context, data: AppNotification,
        intent: PendingIntent? = null,
        vararg actions: Action
    ) {
        val builder: NotificationCompat.Builder =
            NotificationCompat.Builder(context, data.channelId)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(data.title)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(data.message)
                )
                .setContentText(data.message)
                .setAutoCancel(true)
        intent?.let{ builder.setContentIntent(it) }
        for(action in actions)
            builder.addAction(action)
        val notificationManager = NotificationManagerCompat.from(context)
        if (notificationManager.getNotificationChannel(data.channelId) == null) {
            createNotificationChannel(data.channelId, data.channelName, null)
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        notificationManager.notify(data.id, builder.build())
    }

    /**
     * Этот метод проверяет, включен ли канал уведомлений.
     * @param channelId ID канала
     */
    fun isNotificationChannelEnabled(channelId: String): Boolean {
        try {
            if (!TextUtils.isEmpty(channelId)) {
                val channel = notificationManager.getNotificationChannel(channelId)
                if (channel != null)
                    return channel.importance != NotificationManager.IMPORTANCE_NONE
            }
        } catch (_: Exception) { /*Not required*/ }
        return false
    }

    companion object{
        private var instance: NotificationManagerWrapper? = null

        /**
         * Этот метод позволяет получить синглтон менеджера уведомлений.
         * @param context контекст приложения
         */
        fun getInstance(context: Context): NotificationManagerWrapper{
            if(instance == null)
                instance = NotificationManagerWrapper(NotificationManagerCompat.from(context))
            return instance!!
        }
    }
}