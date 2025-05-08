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

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationManagerCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ghostwalker18.schedule.ScheduleApp
import java.util.concurrent.TimeUnit

/**
 * Этот сервис управляет установкой напоминаний для заметок
 *
 * @author Ипатов Никита
 */
class NoteReminderService: Service() {
    private val defaultDelay: Long = 2

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val previousNotificationID = intent?.extras?.getInt("previous_notification_id")
        previousNotificationID?.let{
            NotificationManagerCompat.from(ScheduleApp.instance).cancel(it)
        }
        val noteID = intent?.extras?.getInt("noteID")
        val delay = intent?.extras?.getLong("delay") ?: defaultDelay
        noteID?.let {
            val context = ScheduleApp.instance as Context
            val inputData = Data.Builder()
                .putInt("noteID", it)
                .build()
            val request = OneTimeWorkRequest.Builder(
                workerClass = NoteReminderNotificationWorker::class.java
            )
                .addTag("schedulePCCE_note_$it")
                .setInputData(inputData)
                .setInitialDelay(delay, TimeUnit.MINUTES)
                .build()
            WorkManager.getInstance(context).enqueue(request)
        }
        return super.onStartCommand(intent, flags, startId)
    }
}