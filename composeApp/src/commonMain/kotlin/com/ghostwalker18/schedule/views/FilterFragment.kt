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
import com.ghostwalker18.schedule.widgets.DatePickerModal
import com.ghostwalker18.schedule.converters.DateConverters
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.end_date
import scheduledesktop2.composeapp.generated.resources.filters
import scheduledesktop2.composeapp.generated.resources.start_date
import com.ghostwalker18.schedule.viewmodels.NotesModel

/**
 * Этот класс служит для отображения панели фильтров заметок.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun NotesFilterFragment(){
    val model = viewModel { NotesModel() }
    val group = model.group
    val startDate by model.startDate.collectAsState()
    val endDate by model.endDate.collectAsState()

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
            ContentWrapper(
                toolTip = Res.string.filter_close_descr
            ){
                IconButton({model.isFilterEnabled.value = false}){
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(Res.string.filter_close_descr)
                    )
                }
            }
        }
        Text(
            text = stringResource(Res.string.for_group),
            modifier = Modifier
                .padding(5.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = group ?: "",
                onValueChange = { model.group = it },
                modifier = Modifier.weight(1f)
            )
            ContentWrapper(
                toolTip = Res.string.note_clear_group_descr
            ){
                IconButton({ model.group = null }){
                    Icon(
                        imageVector = Icons.Filled.Close,
                        contentDescription = stringResource(Res.string.note_clear_group_descr)
                    )
                }
            }
        }
        Text(
            text = stringResource(Res.string.start_date),
            modifier = Modifier
                .padding(5.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = DateConverters().toString(startDate) ?: "",
                onValueChange = { model.setStartDate(DateConverters().fromString(it)) },
                modifier = Modifier.weight(1f)
            )
            var showDatePicker by remember { mutableStateOf(false) }
            ContentWrapper(
                toolTip = Res.string.filter_start_date_descr
            ){
                IconButton({ showDatePicker = true }){
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(Res.string.filter_start_date_descr)
                    )
                }
            }
            if (showDatePicker)
                DatePickerModal(
                    confirmButtonText = Res.string.chose_date,
                    dismissButtonText = Res.string.cancelButtonText,
                    onDismiss = { showDatePicker = false },
                    onDateSelected = { model.setStartDate(it) }
                )
        }
        Text(
            text = stringResource(Res.string.end_date),
            modifier = Modifier
                .padding(5.dp)
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            TextField(
                value = DateConverters().toString(endDate) ?: "",
                onValueChange = { model.setEndDate(DateConverters().fromString(it)) },
                modifier = Modifier.weight(1f)
            )
            var showDatePicker by remember { mutableStateOf(false) }
            ContentWrapper(
                toolTip = Res.string.filter_end_date_descr
            ){
                IconButton({ showDatePicker = true }){
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = stringResource(Res.string.filter_end_date_descr)
                    )
                }
            }
            if (showDatePicker)
                DatePickerModal(
                    confirmButtonText = Res.string.chose_date,
                    dismissButtonText = Res.string.cancelButtonText,
                    onDismiss = { showDatePicker = false },
                    onDateSelected = { model.setEndDate(it) }
                )
        }
    }
}