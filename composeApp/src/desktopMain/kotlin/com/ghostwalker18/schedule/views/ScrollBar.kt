/*
 * Copyright 2025 Ipatov Nikita
 *
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

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.gestures.ScrollableState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.rememberScrollbarAdapter

@Composable
actual fun ScrollBar(modifier: Modifier, state: ScrollableState?) {
    state?.let{
        val adapter = when(state){
            is LazyListState -> rememberScrollbarAdapter(state)
            is ScrollState -> rememberScrollbarAdapter(state)
            else -> throw UnsupportedOperationException("Unknown state to create adapter for scroll")
        }
        VerticalScrollbar(
            modifier = modifier
                .fillMaxHeight(),
            adapter = adapter
        )
    }
}