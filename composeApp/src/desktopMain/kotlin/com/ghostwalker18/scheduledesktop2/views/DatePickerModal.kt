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

import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import java.util.*

/**
 * Эта функция служит для отображения модального диалогового окна выбора даты
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    confirmButtonText: StringResource,
    dismissButtonText: StringResource,
    onDateSelected: (Calendar) -> Unit,
    onDismiss: () -> Unit
){
    val datePickerState = rememberDatePickerState()
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let {
                        val chosenDate = Calendar.Builder().setInstant(it).build()
                        onDateSelected(chosenDate)
                    }
                    onDismiss()
                }
            ){
                Text(stringResource(confirmButtonText))
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ){
                Text(stringResource(dismissButtonText))
            }
        }
    ){
        DatePicker(datePickerState)
    }
}