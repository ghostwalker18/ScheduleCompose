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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Snackbar
import androidx.compose.material.SnackbarHost
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.widgets.ListView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.clear_app_data
import scheduledesktop2.composeapp.generated.resources.clear_app_data_button
import scheduledesktop2.composeapp.generated.resources.clear_data
import scheduledesktop2.composeapp.generated.resources.clear_data_types_entries
import scheduledesktop2.composeapp.generated.resources.clear_data_types_values
import scheduledesktop2.composeapp.generated.resources.clearing_ended
import scheduledesktop2.composeapp.generated.resources.clearing_start
import scheduledesktop2.composeapp.generated.resources.go_back_descr

/**
 * Эта функция отображает экран очистки данных приложения:
 * заметок, расписания или и того и другого.
 *
 * @author Ипатов Никита
 */
@Composable
fun ClearDataScreen(){
    val scaffoldState = rememberScaffoldState()
    val navigator = ScheduleApp.instance.getNavigator()
    val scope = rememberCoroutineScope(getContext = { Dispatchers.IO })

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.clear_app_data)) },
                navigationIcon = {
                    IconButton({ navigator.goBack() }){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back_descr)
                        )
                    }
                }
            )
        },
        snackbarHost = {
            SnackbarHost(it) {
                data ->
                Snackbar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primaryVariant,
                    snackbarData = data
                )
            }
        }
    ){
        var dataType by remember { mutableStateOf("notes") }

        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ){
            Row(
                modifier = Modifier
                    .padding(vertical = 5.dp)
            ){
                Text(
                    text = stringResource(Res.string.clear_data),
                    modifier = Modifier
                        .weight(0.5f)
                )
                ListView(
                    entries = Res.array.clear_data_types_entries,
                    entryValues = Res.array.clear_data_types_values,
                    modifier = Modifier
                        .weight(0.5f)
                ){
                    dataType = it
                }
            }
            Button(
                onClick = {
                    scope.launch {
                        var notesDeleted = 0
                        var lessonsDeleted = 0
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = getString(Res.string.clearing_start)
                        )
                        when(dataType){
                            "notes" ->
                                notesDeleted = ScheduleApp.instance.database.noteDao().deleteAllNotes()
                            "schedule" ->
                                lessonsDeleted = ScheduleApp.instance.database.lessonDao().deleteAllLessons()
                            "schedule_and_notes" -> {
                                notesDeleted = ScheduleApp.instance.database.noteDao().deleteAllNotes()
                                lessonsDeleted = ScheduleApp.instance.database.lessonDao().deleteAllLessons()
                            }
                        }
                        scaffoldState.snackbarHostState.showSnackbar(
                            message = getString(Res.string.clearing_ended, notesDeleted, lessonsDeleted)
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(stringResource(Res.string.clear_app_data_button))
            }
        }
    }
}