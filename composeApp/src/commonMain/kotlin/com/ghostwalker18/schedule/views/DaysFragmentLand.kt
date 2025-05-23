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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.schedule.widgets.AutocompleteTextView
import com.ghostwalker18.schedule.widgets.CustomButton
import com.ghostwalker18.schedule.widgets.DatePickerModal
import com.ghostwalker18.schedule.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.back
import scheduledesktop2.composeapp.generated.resources.forward
import scheduledesktop2.composeapp.generated.resources.monday
import com.ghostwalker18.schedule.viewmodels.ScheduleModel
import java.util.Calendar

/**
 * Эта функция представляет собой элемент интерфейса, используемый для
 * отображения расписания занятий.
 * @param date дата для показа расписания на неделю
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Composable
fun DaysFragmentLand(
    date: Calendar = Calendar.getInstance()
){
    val repository = ScheduleApp.instance.scheduleRepository
    val model: ScheduleModel = viewModel { ScheduleModel(date) }
    val group by model.group.collectAsState()
    val teacher by model.teacher.collectAsState()
    var status by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0f) }
    val groups by repository.groups.collectAsState(emptyArray())
    val teachers by repository.teachers.collectAsState(emptyArray())

    model.viewModelScope.launch {
        repository.status.collect{
            status = it.status
            progress = it.progress.toFloat()
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ){
        var showDatePicker by remember { mutableStateOf(false) }
        if(showDatePicker)
            DatePickerModal(
                confirmButtonText = Res.string.go_to_date,
                dismissButtonText = Res.string.cancelButtonText,
                onDismiss = { showDatePicker = false },
                onDateSelected = { model.calendar.value = it }
            )
        CustomButton(
            onClick = { model.goPreviousWeek() },
            onLongClick = { showDatePicker = true },
            modifier = Modifier
                .weight(0.125f)
                .align(Alignment.CenterVertically)

        ){
            ContentWrapper(
                toolTip = Res.string.previous_week_tooltip
            ){
                Text(stringResource(Res.string.back))
            }
        }
        Column(
            modifier = Modifier
                .weight(0.75f)
                .absolutePadding(left = 5.dp, right = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
            ){
                Text(
                    text = stringResource(Res.string.group_choice_text),
                    color = MaterialTheme.colors.primaryVariant
                )
                Row {
                    AutocompleteTextView(
                        value = group ?: "",
                        options = groups,
                        modifier = Modifier.weight(1f)
                    ){
                        model.group.value = it
                    }
                    IconButton({
                        model.group.value = null
                    }){
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = stringResource(Res.string.days_group_clear_descr)
                        )
                    }
                }
                if(ScheduleApp.instance.preferences.getBoolean(
                        ScheduleAppSettings.ScheduleSettings.TeacherSearch.key,
                        ScheduleAppSettings.ScheduleSettings.TeacherSearch.defaultValue
                )){
                    Text(
                        text = stringResource(Res.string.teacher_choice_text),
                        color = MaterialTheme.colors.primaryVariant
                    )
                    Row {
                        AutocompleteTextView(
                            value = teacher ?: "",
                            options = teachers,
                            modifier = Modifier.weight(1f)
                        ){
                            model.teacher.value = it
                        }
                        IconButton({ model.teacher.value = null })
                        {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = stringResource(Res.string.days_teacher_clear_descr)
                            )
                        }
                    }
                }
                ScheduleItemFragment(date, Res.string.monday)
                ScheduleItemFragment(date, Res.string.tuesday)
                ScheduleItemFragment(date, Res.string.wednesday)
                ScheduleItemFragment(date, Res.string.thursday)
                ScheduleItemFragment(date, Res.string.friday)
            }
            Text(status)
            LinearProgressIndicator(
                progress = progress / 100,
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.fillMaxWidth()
            )
            ContentWrapper(
                toolTip = Res.string.update_descr
            ){
                IconButton({ repository.update() }){
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(Res.string.update_descr)
                    )
                }
            }
        }
        CustomButton(
            onClick = { model.goNextWeek() },
            onLongClick = { showDatePicker = true },
            modifier = Modifier
                .weight(0.125f)
                .align(Alignment.CenterVertically)
        ){
            ContentWrapper(
                toolTip = Res.string.next_week_tooltip
            ){
                Text(stringResource(Res.string.forward))
            }
        }
    }
}