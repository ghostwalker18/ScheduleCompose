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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.painter.Painter
import getScheduleRepository
import kotlinx.coroutines.launch

@Preview
@Composable
fun TimesFragment(){
    var mondayPainter by remember { mutableStateOf<Painter?>(null) }
    var otherPainter by remember { mutableStateOf<Painter?>(null) }
    val scope = rememberCoroutineScope()
    val repository = getScheduleRepository()

    scope.launch {
        repository.mondayTimes.collect{
            mondayPainter = it
        }
    }
    scope.launch {
        repository.otherTimes.collect{
            otherPainter = it
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
    ) {
        mondayPainter?.let {
            Image(
                painter = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f)
                    .padding(15.dp)
            )
        }
        otherPainter?.let {
            Image(
                painter = it,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f)
                    .padding(15.dp)
            )
        }
    }
}