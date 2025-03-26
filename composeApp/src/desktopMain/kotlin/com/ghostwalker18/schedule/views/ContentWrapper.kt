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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class)
@Composable
actual fun ContentWrapper(
    toolTip: StringResource,
    content: @Composable () -> Unit
){
    TooltipArea(
        content = content,
        delayMillis = 800,
        tooltip = {
            Surface(
                modifier = Modifier
                    .shadow(4.dp),
                color = Color(255, 255, 240),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    text = stringResource(toolTip),
                    color = MaterialTheme.colors.primaryVariant,
                    modifier = Modifier
                        .padding(10.dp)
                )
            }
        }
    )
}