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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import getNavigator
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.date
import scheduledesktop2.composeapp.generated.resources.edit_notes_activity
import scheduledesktop2.composeapp.generated.resources.for_group
import viewmodels.EditNoteModel

@Composable
fun EditNoteActivity(){
    val navigator = getNavigator()
    var date by remember { mutableStateOf("") }
    var group by remember { mutableStateOf("") }
    var theme by remember { mutableStateOf("") }
    var text by remember { mutableStateOf("") }
    val model = viewModel { EditNoteModel() }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.edit_notes_activity)) },
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
            Row {
                Text(text = stringResource(Res.string.date))
                Text(text = date)
                IconButton({}){
                    Icon(Icons.Filled.ArrowDropDown, null)
                }
            }
            Row {
                TextField(
                    value = group,
                    modifier = Modifier
                        .weight(1f),
                    onValueChange = {
                        group = it
                    },
                    placeholder = {
                        Text(text = stringResource(Res.string.for_group))
                    }
                )
                IconButton({
                        group = ""
                    }
                ){
                    Icon(Icons.Filled.Close, null)
                }
            }
            Row {
                TextField(
                    value = theme,
                    modifier = Modifier
                        .weight(1f),
                    onValueChange = {
                        theme = it
                    },
                    placeholder = {
                        Text(text = stringResource(Res.string.theme))
                    }
                )
                IconButton({
                    theme = ""
                }
                ){
                    Icon(Icons.Filled.Close, null)
                }
            }
            Row {
                TextField(
                    value = text,
                    modifier = Modifier
                        .weight(1f),
                    onValueChange = {
                        text = it
                    },
                    placeholder = {
                        Text(text = stringResource(Res.string.text))
                    }
                )
                IconButton({
                    text = ""
                }
                ){
                    Icon(Icons.Filled.Close, null)
                }
            }
        }
    }
}