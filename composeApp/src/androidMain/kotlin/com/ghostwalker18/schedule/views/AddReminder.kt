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

import android.Manifest
import android.app.Activity
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.AlarmOn
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.removeNoteReminder
import com.ghostwalker18.schedule.ui.theme.AlertDialogHeaderFontSize
import com.ghostwalker18.schedule.utils.Utils.calculateTimeDistance
import com.ghostwalker18.schedule.viewmodels.EditNoteModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.add_note_reminder_descr
import scheduledesktop2.composeapp.generated.resources.cancelButtonText
import scheduledesktop2.composeapp.generated.resources.something_weird
import scheduledesktop2.composeapp.generated.resources.days_before_confirm
import scheduledesktop2.composeapp.generated.resources.days_before_delay
import scheduledesktop2.composeapp.generated.resources.note_reminder_time
import scheduledesktop2.composeapp.generated.resources.reminder_error
import scheduledesktop2.composeapp.generated.resources.remove_note_reminder
import com.ghostwalker18.schedule.widgets.TimePickerModal
import com.ghostwalker18.schedule.widgets.bottomBorder
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.notification_permission_required
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Это перечисление описывает стадии добавления напоминания
 *
 * @author Ипатов Никита
 */
internal enum class STAGE {
    READY, ERROR, CHOOSE_TIME, CHOOSE_DAYS_DELAY, ADD_REMINDER
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
    var delay by remember { mutableStateOf<Long>(0) }
    var daysBeforeNotify by remember { mutableStateOf(0) }
    var maxDaysBeforeNotify by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()
    val hasNotification by model.hasNotification.collectAsState()
    val context = LocalContext.current

    val notificationPermissionLauncher =  rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){
        granted ->
        stage =
            if(granted)
                STAGE.CHOOSE_TIME
            else
                STAGE.READY

    }

    scope.launch {
        model.date.collect {
            requiredTime.set(Calendar.DAY_OF_YEAR, (it.clone() as Calendar).get(Calendar.DAY_OF_YEAR))
            val now = Calendar.getInstance()
            maxDaysBeforeNotify = calculateTimeDistance(now, requiredTime, TimeUnit.DAYS).toInt()
        }
    }

    val noteDate by model.date.collectAsState()
    val now = Calendar.getInstance()
    if(noteDate.after(now)
        && ScheduleApp.instance.preferences.getBoolean("notes_notifications", false)
        ){
        IconButton(
            onClick = {
                if(hasNotification){
                    removeNoteReminder(model.id)
                    model.hasNotification.value = false
                    stage = STAGE.READY
                } else {
                    if(shouldShowRequestPermissionRationale(
                            context as Activity, Manifest.permission.POST_NOTIFICATIONS)
                        ){
                        val toast = Toast.makeText(
                            context,
                            runBlocking{getString(Res.string.notification_permission_required)},
                            Toast.LENGTH_SHORT
                        )
                        toast.show()
                    } else {
                        if(Build.VERSION.SDK_INT > 32)
                            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }
        ){
            AnimatedContent(
                targetState = hasNotification,
                transitionSpec = {
                    slideInHorizontally{ it } togetherWith fadeOut()
                }
            ){
                targetState ->
                if(targetState){
                    Icon(
                        imageVector = Icons.Filled.AlarmOn,
                        contentDescription = stringResource(Res.string.remove_note_reminder)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Filled.AlarmOff,
                        contentDescription = stringResource(Res.string.add_note_reminder_descr)
                    )
                }
            }
        }
    }

    when(stage){
        STAGE.CHOOSE_TIME  -> TimePickerModal(
            confirmButtonText = Res.string.note_reminder_time,
            dismissButtonText = Res.string.cancelButtonText,
            onTimeSelected = {
                hour, minute ->
                requiredTime.set(Calendar.HOUR_OF_DAY, hour)
                requiredTime.set(Calendar.MINUTE, minute)
                stage = STAGE.CHOOSE_DAYS_DELAY
            },
            onDismiss = { stage = STAGE.READY }
        )

        STAGE.CHOOSE_DAYS_DELAY -> {
            AlertDialog(
                title = {
                    Text(
                        text = stringResource(Res.string.days_before_delay),
                        modifier = Modifier.padding(vertical = 5.dp),
                        fontSize = AlertDialogHeaderFontSize,
                        color = MaterialTheme.colors.onBackground
                    )
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
                        Text(
                            text = stringResource(Res.string.days_before_confirm),
                            color = MaterialTheme.colors.primary
                        )
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = { stage = STAGE.READY }
                    ){
                        Text(
                            text = stringResource(Res.string.cancelButtonText),
                            color = MaterialTheme.colors.primary
                        )
                    }
                },
                onDismissRequest = { stage = STAGE.READY },
                backgroundColor = MaterialTheme.colors.background
            )
        }

        STAGE.ADD_REMINDER -> {
            requiredTime.add(Calendar.DAY_OF_YEAR, -daysBeforeNotify)
            val now = Calendar.getInstance()
            delay = calculateTimeDistance(now, requiredTime, TimeUnit.MINUTES)
            if(delay > 0){
                model.hasNotification.value = true
                model.delay = delay
                stage = STAGE.READY
            } else {
                stage = STAGE.ERROR
            }
        }

        STAGE.ERROR -> {
            AlertDialog(
                title = {
                    Text(
                        text = stringResource(Res.string.something_weird),
                        modifier = Modifier
                            .padding(vertical = 5.dp)
                            .bottomBorder(1.dp, MaterialTheme.colors.onError),
                        fontSize = AlertDialogHeaderFontSize
                    )
                },
                text = {
                    Text(stringResource(Res.string.reminder_error))
                },
                confirmButton = {
                    TextButton(
                        onClick = { stage = STAGE.READY }
                    ){
                        Text(
                            text = stringResource(Res.string.cancelButtonText),
                            color = MaterialTheme.colors.primary
                        )
                    }
                },
                onDismissRequest = { stage = STAGE.READY },
                backgroundColor = MaterialTheme.colors.background
            )
        }

        STAGE.READY -> {/*Not required*/}
    }
}