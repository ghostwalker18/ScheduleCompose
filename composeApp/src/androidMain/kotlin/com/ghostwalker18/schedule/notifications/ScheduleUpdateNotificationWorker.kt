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
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.ghostwalker18.schedule.R
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.activities.MainActivity
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.models.ScheduleRepository
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

/**
 * Этот класс представляет собой работу
 * по получению нового расписания в фоновом режиме
 * и уведомления пользователя о результате работы.
 *
 * @author Ипатов Никита
 * @since 4.1
 */
class ScheduleUpdateNotificationWorker(
    context: Context,
    workerParameters: WorkerParameters
) : ListenableWorker(context, workerParameters) {

    override fun startWork(): ListenableFuture<Result> {
        val service = MoreExecutors.listeningDecorator(
            Executors.newSingleThreadExecutor()
        )
        return service.submit<Result> {
            val repository = ScheduleApp.instance.scheduleRepository
            val lastUpdateResult = repository.lastUpdateResult
            val lastAvailableDate = runBlocking { repository.getLastKnownLessonDate(repository.savedGroup) }
            repository.update()
            repository.updateResult?.whenComplete { updateResult, _ ->
                if (lastUpdateResult != ScheduleRepository.UpdateResult.FAIL
                    && updateResult === ScheduleRepository.UpdateResult.FAIL
                ) {
                    NotificationManagerWrapper.getInstance(applicationContext)
                        .showNotification(
                            applicationContext, AppNotification(
                                0,
                                applicationContext.getString(
                                    R.string.notifications_notification_schedule_update_channel_name
                                ),
                                applicationContext.getString(
                                    R.string.notifications_schedule_unavailable
                                ),
                                applicationContext.getString(
                                    R.string.notifications_notification_schedule_update_channel_id
                                ),
                                applicationContext.getString(
                                    R.string.notifications_notification_schedule_update_channel_name
                                )
                            )
                        )
                }
                val currentAvailableDate = runBlocking {
                    repository.getLastKnownLessonDate(
                        repository.savedGroup
                    )
                }
                if (currentAvailableDate?.after(lastAvailableDate) == true) {
                    val openScheduleIntent =
                        Intent(applicationContext, MainActivity::class.java)
                    openScheduleIntent.putExtra(
                        "schedule_date",
                        DateConverters().toString(currentAvailableDate)
                    )
                    NotificationManagerWrapper.getInstance(applicationContext)
                        .showNotification(
                            applicationContext, AppNotification(
                                0,
                                applicationContext.getString(
                                    R.string.notifications_notification_schedule_update_channel_name
                                ),
                                (applicationContext.getString(
                                    R.string.notifications_new_schedule_available
                                )
                                        + " " + DateConverters()
                                    .convertForNotification(currentAvailableDate)),
                                applicationContext.getString(
                                    R.string.notifications_notification_schedule_update_channel_id
                                ),
                                applicationContext.getString(
                                    R.string.notifications_notification_schedule_update_channel_name
                                )
                            ),
                            PendingIntent.getActivity(
                                applicationContext, 0,
                                openScheduleIntent, PendingIntent.FLAG_IMMUTABLE
                            )
                        )
                }
            }
            Result.success()
        }
    }
}