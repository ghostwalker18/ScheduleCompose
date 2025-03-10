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

import androidx.compose.animation.*
import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.schedule.getNavigator
import com.ghostwalker18.schedule.getNotesScreenController
import com.ghostwalker18.schedule.getScheduleRepository
import kotlinx.coroutines.launch
import com.ghostwalker18.schedule.models.Note
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.no_notes_now
import scheduledesktop2.composeapp.generated.resources.notes_activity
import com.ghostwalker18.schedule.viewmodels.NotesModel
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
    val worker = getNotesScreenController()
    val scope = rememberCoroutineScope()
    val model = viewModel { NotesModel() }
    model.group = group
    model.setStartDate(date)
    model.setEndDate(date)
    val notes by model.notes.collectAsState()
    val selectedNotes =  remember { mutableStateListOf<Note>() }
    val isFilterEnabled by model.isFilterEnabled.collectAsState()
    val keyWord by model.keyword.collectAsState()

    @Composable
    fun SearchNoteBar(){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp)
        ) {
            TextField(
                value = keyWord ?: "",
                leadingIcon = { Icon(Icons.Filled.Search, null) },
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
                    .weight(1f),
                onValueChange = { model.setKeyword(if (it == "") null else it) }
            )
            IconButton({ model.isFilterEnabled.value = true }){
                Icon(Icons.Filled.Tune, null)
            }
        }
    }

    @Composable
    fun SelectedNotesCounter(){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            IconButton({ selectedNotes.clear() })
            {
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

    val scaffoldState = rememberScaffoldState()
    Scaffold(
        scaffoldState = scaffoldState,
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
                            IconButton({
                                val (showTextRequired, text) = worker.shareNotes(selectedNotes)
                                if (showTextRequired)
                                    scope.launch {
                                        scaffoldState.snackbarHostState.showSnackbar(
                                            org.jetbrains.compose.resources.getString(
                                                text
                                            )
                                        )
                                    }
                            }){
                                Icon(Icons.Filled.Share, "")
                            }
                        }
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
            AnimatedContent(
                targetState = selectedNotes.isEmpty()
            ){
                targetState ->
                when(targetState){
                    true -> SearchNoteBar()
                    else -> SelectedNotesCounter()
                }
            }
            AnimatedVisibility(
                isFilterEnabled,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ){
                NotesFilterFragment()
            }
            if(notes.isEmpty()){
                Text(
                    text = stringResource(Res.string.no_notes_now),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                )
            }
            else {
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
}