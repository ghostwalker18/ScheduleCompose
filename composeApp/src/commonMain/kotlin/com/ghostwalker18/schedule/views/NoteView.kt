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

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.models.Note

/**
 * Эта функция отображает единичную заметку в списке.
 * @param note заметка для отображения
 * @param onSelected действие при выборе заметки
 * @param onUnselected действие при отмене выбора заметки
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun NoteView(
    note: Note,
    selectedNotes: SnapshotStateList<Note>,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    onSelected: () -> Unit = {},
    onUnselected: () -> Unit = {}
) {
    var isSelected = selectedNotes.contains(note)
    Row(
        verticalAlignment = Alignment.Bottom
    ){
        if (isSelected)
            Icon(Icons.Filled.Check, null)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 5.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colors.primaryVariant,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable {
                    isSelected = !isSelected
                    if(isSelected)
                        onSelected()
                    else
                        onUnselected()
                }
        ){
            Text(
                text = DateConverters().toString(note.date)!!,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
            )
            Text(
                text = note.theme ?: "",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 5.dp)
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colors.primaryVariant,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(5.dp)
            )
            Text(
                text = note.text,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
            PhotoPreview(
                modifier = Modifier.padding(5.dp),
                photoIDs = note.photoIDs as MutableList
            )
        }
    }
}