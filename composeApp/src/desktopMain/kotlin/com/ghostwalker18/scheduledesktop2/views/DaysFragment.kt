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

package com.ghostwalker18.scheduledesktop2.views

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.ghostwalker18.scheduledesktop2.ScheduleApp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.back
import scheduledesktop2.composeapp.generated.resources.forward
import scheduledesktop2.composeapp.generated.resources.monday
import viewmodels.ScheduleModel

@Preview
@Composable
fun DaysFragment(){
    val repository by remember { mutableStateOf(ScheduleApp.getInstance().getScheduleRepository()) }
    val model: ScheduleModel = viewModel { ScheduleModel() }
    var group by remember { mutableStateOf("") }
    var teacher by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var progress by remember { mutableStateOf(0f) }
    var groups by remember { mutableStateOf(arrayOf("")) }
    var teachers by remember { mutableStateOf(arrayOf("")) }

    model.viewModelScope.launch {
        repository.status.collect{
            status = it.status
            progress = it.progress.toFloat()
        }
    }
    model.viewModelScope.launch {
        repository.groups.collect{
            groups = it
        }
    }
    model.viewModelScope.launch {
        repository.teachers.collect{
            teachers = it
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ){
        Button(
            onClick = { model.goPreviousWeek() },
            modifier = Modifier
                .weight(0.125f)
                .align(Alignment.CenterVertically)
        ){
            Text(stringResource(Res.string.back))
        }
        Column(
            modifier = Modifier
                .weight(0.75f)
                .absolutePadding(left = 5.dp, right = 5.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ){
                item {
                    Column {
                        Text(stringResource(Res.string.group_choice_text))
                        Row {
                            AutocompleteTextView(
                                value = group,
                                options = groups,
                                modifier = Modifier.weight(1f)
                            ){
                                model.group.value = it
                                group = it
                            }
                            IconButton({
                                group = ""
                                model.group.value = null
                            }){
                                Icon(Icons.Filled.Close, "")
                            }
                        }
                        Text(stringResource(Res.string.teacher_choice_text))
                        Row {
                            AutocompleteTextView(
                                value = teacher,
                                options = teachers,
                                modifier = Modifier.weight(1f)
                            ){
                                model.teacher.value = it
                                teacher = it
                            }
                            IconButton({
                                teacher = ""
                                model.teacher.value = null
                            }){
                                Icon(Icons.Filled.Close, "")
                            }
                        }
                    }
                }
                item{
                    ScheduleItemFragment(Res.string.monday)
                }
                item{
                    ScheduleItemFragment(Res.string.tuesday)
                }
                item{
                    ScheduleItemFragment(Res.string.wednesday)
                }
                item{
                    ScheduleItemFragment(Res.string.thursday)
                }
                item{
                    ScheduleItemFragment(Res.string.friday)
                }
            }
            Text(status)
            LinearProgressIndicator(
                progress = progress / 100,
                modifier = Modifier.fillMaxWidth()
            )
            IconButton({repository.update()}){ Icon(Icons.Filled.Refresh, "") }
        }
        Button(
            onClick = { model.goNextWeek() },
            modifier = Modifier
                .weight(0.125f)
                .align(Alignment.CenterVertically)
        ){
            Text(stringResource(Res.string.forward))
        }
    }
}