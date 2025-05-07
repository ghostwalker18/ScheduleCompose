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

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ghostwalker18.schedule.R
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.activities.MainActivity
import com.ghostwalker18.schedule.converters.DateConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Этот класс представляет собой работу
 * по уведомлению пользователя об ожидающей заметке.
 *
 * @author Ипатов Никита
 */
class NoteReminderNotificationWorker(
    context: Context,
    workerParameters: WorkerParameters
): Worker(context, workerParameters) {

    override fun doWork(): Result {
        val noteID = inputData.getInt("noteID", 0)
        val repository = ScheduleApp.instance.notesRepository
        CoroutineScope(
            Dispatchers.IO
        ).launch {
            repository.getNote(noteID).collect {
                it?.let{
                    if(it.hasNotification){
                        val openNotesIntent =
                            Intent(applicationContext, MainActivity::class.java)
                        openNotesIntent.putExtra(
                            "noteDate",
                            DateConverters().toString(it.date)
                        )
                        openNotesIntent.putExtra(
                            "noteGroup",
                            it.group
                        )
                        NotificationManagerWrapper.getInstance(applicationContext)
                            .showNotification(
                                applicationContext, AppNotification(
                                    Random.Default.nextInt(),
                                    applicationContext.getString(R.string.notes),
                                    it.toString(),
                                    applicationContext.getString(
                                        R.string.notifications_notification_note_reminder_channel_id
                                    ),
                                    applicationContext.getString(
                                        R.string.notifications_notification_note_reminder_channel_name
                                    )
                                ),
                                PendingIntent.getActivity(
                                    applicationContext, 0,
                                    openNotesIntent, PendingIntent.FLAG_IMMUTABLE
                                )
                            )
                        it.hasNotification = false
                        repository.updateNote(it)
                    }
                }
            }
        }
        return Result.success()
    }
}