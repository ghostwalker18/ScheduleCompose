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

import Navigator
import androidx.compose.animation.*
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NoteAdd
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import getNavigator
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.notes_activity
import viewmodels.NotesModel

@Composable
fun NotesActivity(){
    val navigator = getNavigator()
    val model = viewModel { NotesModel() }
    var isFilterEnabled by remember { mutableStateOf(false) }
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
                    IconButton({}){
                        Icon(Icons.Filled.EditNote, "")
                    }
                    IconButton({}){
                        Icon(Icons.Filled.Delete, "")
                    }
                    IconButton({}){
                        Icon(Icons.Filled.Share, "")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton({ navigator.goEditNoteActivity() }){
                Icon(Icons.AutoMirrored.Filled.NoteAdd, null)
            }
        }
    ){
        Column {
            Row {
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
                IconButton({isFilterEnabled = true}){
                    Icon(Icons.Filled.Tune, null)
                }
            }
            AnimatedVisibility(
                isFilterEnabled,
                enter = fadeIn() + expandHorizontally(),
                exit = fadeOut() + shrinkHorizontally()
            ){
                NotesFilterFragment(isFilterEnabled)
            }
            LazyColumn {

            }
        }
    }
}