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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.end_date
import scheduledesktop2.composeapp.generated.resources.filters
import scheduledesktop2.composeapp.generated.resources.start_date
import viewmodels.NotesModel

@Composable
fun NotesFilterFragment(isFilterEnabled: Boolean){
    var group by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var model = viewModel { NotesModel() }

    AnimatedVisibility(
        isFilterEnabled,
        enter = fadeIn() + expandHorizontally(),
        exit = fadeOut() + shrinkHorizontally()
    ){
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp)
                .border(width = 1.dp, color = MaterialTheme.colors.secondary)
                .background(MaterialTheme.colors.background)
                .padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(Res.string.filters),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                IconButton({}){
                    Icon(Icons.Filled.Close, null)
                }
            }
            Text(
                text = stringResource(Res.string.friday),
                modifier = Modifier
                    .padding(5.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = group,
                    onValueChange = {
                        group = it
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    {
                        group = ""
                    }
                ){
                    Icon(Icons.Filled.Close, null)
                }
            }
            Text(
                text = stringResource(Res.string.start_date),
                modifier = Modifier
                    .padding(5.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = startDate,
                    onValueChange = {
                        startDate = it
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton({}){
                    Icon(Icons.Filled.ArrowDropDown, null)
                }
            }
            Text(
                text = stringResource(Res.string.end_date),
                modifier = Modifier
                    .padding(5.dp)
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                TextField(
                    value = endDate,
                    onValueChange = {
                        endDate = it
                    },
                    modifier = Modifier.weight(1f)
                )
                IconButton({}){
                    Icon(Icons.Filled.ArrowDropDown, null)
                }
            }
        }
    }
}