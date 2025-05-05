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

package com.ghostwalker18.schedule.views

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.ghostwalker18.schedule.notifications.NoteReminderNotificationWorker
import com.ghostwalker18.schedule.utils.Utils.calculateTimeDistance
import com.ghostwalker18.schedule.viewmodels.EditNoteModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.add_note_reminder_descr
import scheduledesktop2.composeapp.generated.resources.cancelButtonText
import scheduledesktop2.composeapp.generated.resources.days_before_confirm
import scheduledesktop2.composeapp.generated.resources.days_before_delay
import scheduledesktop2.composeapp.generated.resources.note_reminder_time
import scheduledesktop2.composeapp.generated.resources.remove_note_reminder
import widgets.TimePickerModal
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Это перечисление описывает стадии добавления напоминания
 *
 * @author Ипатов Никита
 */
internal enum class STAGE {
    READY, CHOOSE_TIME, CHOOSE_DAYS_DELAY, ADD_REMINDER
}

/**
 * Эта функция служит для добавления или снятия напоминания для заметки
 *
 * @author Ипатов Никита
 */
@Composable
actual fun AddReminder(){
    val model = viewModel{ EditNoteModel() }
    var stage by remember { mutableStateOf(STAGE.READY) }
    val requiredTime = remember { return@remember Calendar.getInstance() }
    var daysBeforeNotify by remember { mutableStateOf(0) }
    var maxDaysBeforeNotify by remember { mutableStateOf(0) }
    val context = LocalView.current.context
    val scope = rememberCoroutineScope()
    val hasNotification by model.hasNotification.collectAsState()

    scope.launch {
        model.date.collect {
            requiredTime.set(Calendar.DAY_OF_YEAR, it.get(Calendar.DAY_OF_YEAR))
            val now = Calendar.getInstance()
            maxDaysBeforeNotify = calculateTimeDistance(now, requiredTime, TimeUnit.DAYS).toInt()
        }
    }

    val noteDate by model.date.collectAsState()
    val now = Calendar.getInstance()
    if(noteDate.after(now)){
        IconButton(
            onClick = {
                if(hasNotification){
                    WorkManager.getInstance(context)
                        .cancelAllWorkByTag("schedulePCCE_note_" + model.id)
                    model.hasNotification.value = false
                    stage = STAGE.READY
                } else {
                    stage = STAGE.CHOOSE_TIME
                }
            }
        ){
            if(hasNotification)
                Icon(
                    imageVector = Icons.Filled.AlarmOn,
                    contentDescription = stringResource(Res.string.remove_note_reminder)
                )
            else
                Icon(
                    imageVector = Icons.Filled.AlarmOff,
                    contentDescription = stringResource(Res.string.add_note_reminder_descr)
                )
        }
    }

    when(stage){
        STAGE.CHOOSE_TIME  -> TimePickerModal(
            confirmButtonText = Res.string.note_reminder_time,
            dismissButtonText = Res.string.cancelButtonText,
            onTimeSelected = {
                hour, minute ->
                requiredTime.set(Calendar.HOUR, hour)
                requiredTime.set(Calendar.MINUTE, minute)
                stage = STAGE.CHOOSE_DAYS_DELAY
            },
            onDismiss = { stage = STAGE.READY }
        )

        STAGE.CHOOSE_DAYS_DELAY -> {
            AlertDialog(
                title = {
                    Text(stringResource(Res.string.days_before_delay))
                },
                text = {
                    TextField(
                        value = daysBeforeNotify.toString(),
                        onValueChange = {
                            daysBeforeNotify = try {
                                val newDelay = it.toInt()
                                if(newDelay > maxDaysBeforeNotify)
                                    maxDaysBeforeNotify
                                else
                                    newDelay
                            } catch (_: Exception) { 0 }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                },
                confirmButton = {
                    TextButton(
                        onClick = { stage = STAGE.ADD_REMINDER }
                    ){
                        Text(stringResource(Res.string.days_before_confirm))
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { stage = STAGE.READY }
                    ){
                        Text(stringResource(Res.string.cancelButtonText))
                    }
                },
                onDismissRequest = { stage = STAGE.READY },
            )
        }

        STAGE.ADD_REMINDER -> {
            requiredTime.add(Calendar.DAY_OF_YEAR, -daysBeforeNotify)
            val inputData = Data.Builder()
                .putInt("noteID", model.id)
                .build()
            val now = Calendar.getInstance()
            val delay = calculateTimeDistance(now, requiredTime, TimeUnit.MINUTES)
            if(delay > 0){
                val request = OneTimeWorkRequest.Builder(
                    workerClass = NoteReminderNotificationWorker::class.java
                )
                    .addTag("schedulePCCE_note_" + model.id)
                    .setInputData(inputData)
                    .setInitialDelay(delay, TimeUnit.MINUTES)
                    .build()
                WorkManager.getInstance(context).enqueue(request)
                model.hasNotification.value = true
            }
            stage = STAGE.READY
        }

        STAGE.READY -> {/*Not required*/}
    }
}