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

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.schedule.widgets.AutocompleteTextView
import com.ghostwalker18.schedule.widgets.DatePickerModal
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.getNavigator
import com.ghostwalker18.schedule.getScheduleRepository
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.date
import scheduledesktop2.composeapp.generated.resources.for_group
import com.ghostwalker18.schedule.viewmodels.EditNoteModel
import java.util.*

/**
 * Эта функция представляет собой экран редактирования или добавления новой заметки.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun EditNoteScreen(
    noteID: Int? = null,
    group: String? = getScheduleRepository().savedGroup,
    date: Calendar? = Calendar.getInstance(),
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
){
    val navigator = getNavigator()
    val model = viewModel { EditNoteModel() }
    val themes by model.themes.collectAsState()
    val noteDate  by model.date.collectAsState()
    val noteGroup by model.group.collectAsState()
    val noteTheme by model.theme.collectAsState()
    val noteText by model.text.collectAsState()
    if(noteID != 0)
        noteID?.let { model.setNoteID(it) }
    else {
        model.setGroup(group)
        model.date.value = date
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if(noteID == 0)
                        Text(stringResource(Res.string.add_note))
                    else
                        Text(stringResource(Res.string.edit_note))
                },
                navigationIcon = {
                    IconButton(
                        { navigator.goBack()}
                    ){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                },
            )
        }
    ){
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = stringResource(Res.string.date),
                    modifier = Modifier.padding(10.dp, 0.dp)
                )
                Text(text = DateConverters().toString(noteDate)!!)
                var showDatePicker by remember { mutableStateOf(false) }
                IconButton({ showDatePicker = true }){
                    Icon(Icons.Filled.ArrowDropDown, null)
                }
                if (showDatePicker)
                    DatePickerModal(
                        confirmButtonText = Res.string.chose_date,
                        dismissButtonText = Res.string.cancelButtonText,
                        onDismiss = { showDatePicker = false },
                        onDateSelected = {
                            date ->
                            model.date.value = date
                        }
                    )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextField(
                    value = noteGroup ?: "",
                    modifier = Modifier
                        .weight(1f),
                    onValueChange = {
                        model.setGroup(it)
                    },
                    placeholder = {
                        Text(text = stringResource(Res.string.for_group))
                    }
                )
                IconButton({
                        model.setGroup("")
                    }
                ){
                    Icon(Icons.Filled.Close, null)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AutocompleteTextView(
                    placeholder = stringResource(Res.string.theme),
                    value = noteTheme ?: "",
                    options = themes,
                    modifier = Modifier.weight(1f)
                ){
                    model.theme.value = it
                }
                IconButton({
                    model.theme.value = null
                }
                ){
                    Icon(Icons.Filled.Close, null)
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                TextField(
                    value = noteText,
                    modifier = Modifier
                        .weight(1f),
                    onValueChange = {
                        model.text.value = it
                    },
                    placeholder = {
                        Text(text = stringResource(Res.string.text))
                    }
                )
                IconButton({
                    model.text.value = ""
                }
                ){
                    Icon(Icons.Filled.Close, null)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            AttachNotePhotoView(
                sharedTransitionScope = sharedTransitionScope,
                animatedVisibilityScope = animatedVisibilityScope
            )
            Row {
                IconButton(
                    { navigator.goBack() },
                    modifier = Modifier
                        .padding(10.dp)
                        .background(MaterialTheme.colors.primary)
                        .weight(0.5f)
                ){
                    Icon(Icons.Filled.Close, null)
                }
                IconButton(
                    {
                        model.saveNote()
                        navigator.goBack()
                    },
                    modifier = Modifier
                        .padding(10.dp)
                        .background(MaterialTheme.colors.primary)
                        .weight(0.5f)
                ){
                    Icon(Icons.Filled.Save, null)
                }
            }
        }
    }
}