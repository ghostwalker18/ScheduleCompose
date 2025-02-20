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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import converters.DateConverters
import models.Note

@Composable
fun NoteView(
    note: Note,
    onSelected: () -> Unit = {},
    onUnselected: () -> Unit = {}
) {
    var isSelected by remember{ mutableStateOf(false) }
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}