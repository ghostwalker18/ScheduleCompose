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
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.ghostwalker18.schedule.R
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.activities.MainActivity
import com.ghostwalker18.schedule.converters.DateConverters
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.remind_after_hour
import scheduledesktop2.composeapp.generated.resources.remind_tomorrow
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
        if(ScheduleApp.instance.preferences.getBoolean("notes_notifications", false)){
            CoroutineScope(
                Dispatchers.IO
            ).launch {
                repository.getNoteOneShot(noteID)?.let{
                    if(it.hasNotification){
                        val notificationID = Random.Default.nextInt()
                        val openNotesIntent =
                            Intent(applicationContext, MainActivity::class.java)
                        openNotesIntent.putExtra(
                            "note_date",
                            DateConverters().toString(it.date)
                        )
                        openNotesIntent.putExtra(
                            "note_group",
                            it.group
                        )
                        val remindAfterDayIntent = remindLaterIntent(
                            it.id,
                            notificationID,
                            1440L
                        )
                        val remindAfterHourIntent = remindLaterIntent(
                            it.id,
                            notificationID,
                            60L
                        )
                        val actionRemindAfterDay =
                            NotificationCompat.Action.Builder(
                                R.drawable.baseline_schedule_24,
                                getString(Res.string.remind_tomorrow),
                                PendingIntent.getService(
                                    applicationContext,
                                    it.id, remindAfterDayIntent,
                                    PendingIntent.FLAG_IMMUTABLE
                                )
                            )
                                .build()
                        val actionRemindAfterHour =
                            NotificationCompat.Action.Builder(
                                R.drawable.baseline_schedule_24,
                                getString(Res.string.remind_after_hour),
                                PendingIntent.getService(
                                    applicationContext,
                                    it.id, remindAfterHourIntent,
                                    PendingIntent.FLAG_IMMUTABLE
                                )
                            )
                                .build()

                        NotificationManagerWrapper.getInstance(applicationContext)
                            .showNotification(applicationContext, AppNotification(
                                notificationID,
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
                                ),
                                actionRemindAfterHour, actionRemindAfterDay
                            )
                        it.hasNotification = false
                        repository.updateNote(it)
                        this.coroutineContext.job.cancel()
                    }
                }
            }
        }
        return Result.success()
    }

    private fun remindLaterIntent(noteID: Int, previousID: Int, delay: Long): Intent {
        return Intent(applicationContext, NoteReminderService::class.java).apply {
            putExtra("noteID", noteID)
            putExtra("previous_notification_id", previousID)
            putExtra("delay", delay)
            putExtra("remindLater", true)
        }
    }
}