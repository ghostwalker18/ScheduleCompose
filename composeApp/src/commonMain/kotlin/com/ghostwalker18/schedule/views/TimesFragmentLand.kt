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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.ScheduleApp
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.times_monday
import scheduledesktop2.composeapp.generated.resources.times_other

/**
 * Эта функция представляет собой элемент интерфейса для отображения
 * расписания звонков.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Composable
fun TimesFragmentLand(){
    val repository = ScheduleApp.instance.scheduleRepository
    val mondayPainter by repository.mondayTimes.collectAsState()
    val otherPainter by repository.otherTimes.collectAsState()

    Row(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        mondayPainter?.let {
            val aspectRatio = it.intrinsicSize.width / it.intrinsicSize.height
            Image(
                painter = it,
                contentDescription = stringResource(Res.string.times_monday),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f)
                    .padding(15.dp)
                    .aspectRatio(aspectRatio)
            )
        }
        otherPainter?.let {
            val aspectRatio = it.intrinsicSize.width / it.intrinsicSize.height
            Image(
                painter = it,
                contentDescription = stringResource(Res.string.times_other),
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.5f)
                    .padding(15.dp)
                    .aspectRatio(aspectRatio)
            )
        }
    }
}