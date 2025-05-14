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
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.ghostwalker18.schedule.models.Note
import org.jetbrains.compose.resources.stringResource
import com.ghostwalker18.schedule.viewmodels.NotesModel
import com.ghostwalker18.schedule.ScheduleApp
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.no_notes_now
import scheduledesktop2.composeapp.generated.resources.notes_activity
import scheduledesktop2.composeapp.generated.resources.notes_filter_descr
import java.util.*

/**
 * Эта функция представляет собой экран заметок приложения
 * @param group группа для заметок
 * @param date дата заметок
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NotesScreen(
    group: String? = ScheduleApp.instance.scheduleRepository.savedGroup,
    date: Calendar? = Calendar.getInstance(),
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
){
    val navigator = ScheduleApp.instance.getNavigator()
    val worker = ScheduleApp.instance.shareController
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
                onValueChange = { model.setKeyword(it.ifEmpty{ null }) }
            )
            ContentWrapper(
                toolTip = Res.string.notes_filter_descr
            ){
                IconButton({ model.isFilterEnabled.value = true }){
                    Icon(
                        imageVector = Icons.Filled.Tune,
                        contentDescription = stringResource(Res.string.notes_filter_descr)
                    )
                }
            }
        }
    }

    @Composable
    fun SelectedNotesCounter(){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ){
            ContentWrapper(
                toolTip = Res.string.notes_selection_cancel_descr
            ){
                IconButton({ selectedNotes.clear() })
                {
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(Res.string.notes_selection_cancel_descr)
                    )
                }
            }
            AnimatedContent(
                targetState = selectedNotes.size,
                transitionSpec = {
                    if(targetState > initialState){
                        slideInVertically { -it } + fadeIn() togetherWith
                                slideOutVertically { it } + fadeOut()
                    }
                    else {
                        slideInVertically { it } + fadeIn() togetherWith
                                slideOutVertically { -it } + fadeOut()
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
                    IconButton({ navigator.goBack() }){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back_descr)
                        )
                    }
                },
                actions = {
                    AnimatedVisibility(selectedNotes.size == 1){
                        ContentWrapper(
                            toolTip = Res.string.notes_edit_selected_descr
                        ){
                            IconButton(
                                onClick = {
                                    val note = selectedNotes[0]
                                    navigator.goEditNoteActivity(note.group, note.date, note.id)
                                }
                            ){
                                Icon(
                                    imageVector = Icons.Filled.EditNote,
                                    contentDescription = stringResource(Res.string.notes_edit_selected_descr)
                                )
                            }
                        }
                    }
                    AnimatedVisibility(selectedNotes.isNotEmpty()){
                        Row {
                            ContentWrapper(
                                toolTip = Res.string.notes_delete_selected_descr
                            ){
                                IconButton(
                                    onClick = {
                                        model.deleteNotes(selectedNotes)
                                        selectedNotes.clear()
                                    }
                                ){
                                    Icon(
                                        imageVector = Icons.Filled.Delete,
                                        contentDescription = stringResource(Res.string.notes_delete_selected_descr)
                                    )
                                }
                            }
                            ContentWrapper(
                                toolTip = Res.string.notes_share_selected_descr
                            ){
                                IconButton(
                                    onClick = {
                                        val (showTextRequired, text) = worker.shareNotes(selectedNotes)
                                        if (showTextRequired)
                                            scope.launch {
                                                scaffoldState.snackbarHostState.showSnackbar(
                                                    getString(text)
                                                )
                                            }
                                    }
                                ){
                                    Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = stringResource(Res.string.notes_share_selected_descr)
                                    )
                                }
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
                ContentWrapper(
                    toolTip = Res.string.notes_add_descr
                ){
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.NoteAdd,
                        tint = Color.White,
                        contentDescription = stringResource(Res.string.notes_add_descr)
                    )
                }
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
                enter = fadeIn() + slideInHorizontally { it / 2 },
                exit = fadeOut() + slideOutHorizontally { it / 2 }
            ){
                NotesFilterFragment()
            }
            if(notes.isEmpty()){
                Text(
                    text = stringResource(Res.string.no_notes_now),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxSize()
                )
            }
            else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth()
                ){
                    notes.forEach {
                        note ->
                        item {
                            NoteView(
                                note = note,
                                selectedNotes = selectedNotes,
                                sharedTransitionScope = sharedTransitionScope,
                                animatedVisibilityScope = animatedVisibilityScope,
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