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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Notes
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.widgets.ScheduleTable
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.days_tab
import java.util.Calendar

/**
 * Этот класс представляет собой экран приложения для отображения расписания на день.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Composable
fun ScheduleItemScreen(
    group: String?,
    teacher: String?,
    date: Calendar
){
    val lessons by ScheduleApp.instance.scheduleRepository
        .getLessons(date, teacher, group)
        .collectAsState(emptyArray())
    val notesCount by ScheduleApp.instance.notesRepository
        .getNotesCount(group?: "", date)
        .collectAsState(0)
    val navigator = ScheduleApp.instance.navigator
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(Res.string.days_tab) + " " + DateConverters().toString(date)
                    )
                },
                navigationIcon = {
                    IconButton({ ScheduleApp.instance.navigator.goBack() }){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                },
                actions = {
                    IconButton(
                        {
                            val (showTextRequired, text) = ScheduleApp.instance.shareController
                                .shareSchedule(lessons.toList())
                            if (showTextRequired)
                                scope.launch {
                                    scaffoldState.snackbarHostState.showSnackbar(getString(text))
                                }
                        }
                    ){
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primaryVariant,
                    snackbarData = data
                )
            }
        }
    ){
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
        ) {
            ScheduleTable(lessons)
            IconButton(
                {
                    group?.let{
                        navigator.goNotesActivity(it, date)
                    }
                }
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(Icons.AutoMirrored.Filled.Notes, "")
                    if(notesCount > 0)
                        Text(text = notesCount.toString())
                }
            }
        }
    }
}