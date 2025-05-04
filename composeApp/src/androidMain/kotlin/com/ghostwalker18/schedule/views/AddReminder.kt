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

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.schedule.viewmodels.EditNoteModel
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.cancelButtonText
import scheduledesktop2.composeapp.generated.resources.go_to_date
import widgets.TimePickerModal

/**
 * Эта функция служит для добавления или снятия напоминания для заметки
 *
 * @author Ипатов Никита
 */
@Composable
actual fun AddReminder(){
    val model = viewModel{ EditNoteModel() }
    var stage by remember { mutableStateOf(0) }

    IconButton(
        onClick = { stage = 1 }
    ){
        Icon(Icons.Filled.Alarm, "")
    }

    when(stage){
        1 -> TimePickerModal(
            confirmButtonText = Res.string.go_to_date,
            dismissButtonText = Res.string.cancelButtonText,
            onTimeSelected = {
                hour, minute ->
                stage = 2
            },
            onDismiss = {}
        )
        2 -> {}
    }
}