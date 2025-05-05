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

package widgets

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

/**
 * Эта функция служит для отображения модального диалогового окна выбора времени.
 * @param confirmButtonText текст на кнопке подтверждения
 * @param dismissButtonText текст на кнопке отмены
 * @param onTimeSelected действие при выборе времени
 * @param onDismiss действие при отмене выбора
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerModal(
    title: StringResource? = null,
    confirmButtonText: StringResource,
    dismissButtonText: StringResource,
    onTimeSelected: (hour: Int, minute: Int) -> Unit,
    onDismiss: () -> Unit
){
    val timePickerState = rememberTimePickerState(18, 0, true)

    AlertDialog(
        title = {
            title?.let{
                Text(stringResource(it))
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(dismissButtonText))
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onTimeSelected(timePickerState.hour, timePickerState.minute)
                }
            ){
                Text(stringResource(confirmButtonText))
            }
        },
        text = {
            TimePicker(timePickerState)
        }
    )
}