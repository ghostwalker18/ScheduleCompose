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

import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import getNavigator
import getScheduleRepository
import models.Note
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.notes_activity
import viewmodels.NotesModel
import java.util.*


/**
 * Эта функция представляет собой экран заметок приложения
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun NotesActivity(
    group: String? = getScheduleRepository().savedGroup,
    date: Calendar? = Calendar.getInstance()
){
    val navigator = getNavigator()
    val model = viewModel { NotesModel() }
    model.group = group
    model.setStartDate(date)
    model.setEndDate(date)
    val notes by model.notes.collectAsState()
    val selectedNotes =  remember { mutableStateListOf<Note>() }
    val isFilterEnabled by model.isFilterEnabled.collectAsState()
    var keyWord by remember { mutableStateOf("") }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.notes_activity)) },
                navigationIcon = {
                    IconButton(
                        { navigator.goBack()}
                    ){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                },
                actions = {
                    AnimatedVisibility(selectedNotes.size == 1){
                        IconButton({
                            val note = selectedNotes[0]
                            navigator.goEditNoteActivity(note.group, note.date, note.id)
                        }){
                            Icon(Icons.Filled.EditNote, "")
                        }
                    }
                    AnimatedVisibility(selectedNotes.isNotEmpty()){
                        Row {
                            IconButton({ model.deleteNotes(selectedNotes) }){
                                Icon(Icons.Filled.Delete, "")
                            }
                            IconButton({}){
                                Icon(Icons.Filled.Share, "")
                            }
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                { navigator.goEditNoteActivity(model.group!!, model.startDate.value, 0 ) },
                backgroundColor = MaterialTheme.colors.primaryVariant
            ){
                Icon(Icons.AutoMirrored.Filled.NoteAdd, null)
            }
        }
    ){
        Column {
            AnimatedVisibility(
                visible = selectedNotes.isEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ){
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 10.dp)
                ) {
                    TextField(
                        value = keyWord,
                        leadingIcon = {
                            Icon(Icons.Filled.Search, null)
                        },
                        modifier = Modifier.weight(1f),
                        onValueChange = {
                            keyWord = it
                        }
                    )
                    IconButton({ model.isFilterEnabled.value = true }){
                        Icon(Icons.Filled.Tune, null)
                    }
                }
            }
            AnimatedVisibility(selectedNotes.isNotEmpty()){
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ){
                    IconButton(
                        {selectedNotes.clear()}
                    ){
                        Icon(Icons.Filled.Close, null)
                    }
                    AnimatedContent(
                        targetState = selectedNotes.size,
                        transitionSpec = {
                            if(targetState > initialState){
                                slideInVertically { height -> -height } + fadeIn() togetherWith
                                        slideOutVertically { height -> height } + fadeOut()
                            }
                            else {
                                slideInVertically { height -> height } + fadeIn() togetherWith
                                        slideOutVertically { height -> -height } + fadeOut()
                            }.using(
                                SizeTransform(clip = false)
                            )
                        }
                    ){
                        targetState ->
                        Text(text = "$targetState")
                    }
                }
            }
            AnimatedVisibility(
                isFilterEnabled,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ){
                NotesFilterFragment()
            }
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                notes.forEach{ note ->
                    item {
                        NoteView(
                            note = note,
                            onSelected = {
                                selectedNotes.add(note)
                            },
                            onUnselected = {
                                selectedNotes.remove(note)
                            }
                        )
                    }
                }
            }
        }
    }
}