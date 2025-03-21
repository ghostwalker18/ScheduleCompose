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

package com.ghostwalker18.schedule.utils

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.ui.theme.ScheduleTheme

/**
 * Эта функция устанавливает содержимое экрана
 * в соответствии с темой приложения и текущим ночным режимом.
 *
 * @author Ипатов Никита
 * @since 5.0
 */
fun ComponentActivity.setContentWithTheme(content: @Composable () -> Unit){
    setContent {
        val theme by ScheduleApp.instance.themeState.collectAsState()
        val isInDarkMode = when(theme){
            "night" -> true
            "day" -> false
            else -> isSystemInDarkTheme()
        }
        ScheduleTheme(isInDarkMode){
            content()
        }
    }
}