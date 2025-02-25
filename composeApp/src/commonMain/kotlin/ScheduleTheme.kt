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

import androidx.compose.runtime.Composable
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.ui.graphics.Color

val gray500Color = Color(0xff9E9E9E)

val LightColors = lightColors(
    primary = Color(0xff66A101),
    surface = Color(0xff66A101),
    onPrimary = Color(0xff121212),
    primaryVariant = Color(0xff080c73),
    secondary = Color(0xff067138),
    background = Color(0xfffafafa)
)

val DarkColors = darkColors(
    primary = Color(0xff00574B),
    surface = Color(0xff00574B),
    onPrimary = Color(0xffFAFAFA),
    primaryVariant = Color(0xff4169e1),
    secondary = Color(0xff06713b),
    background = Color(0xff303030)
)

/**
 * Эта функция предоставляет тему приложению.
 */
@Composable
fun ScheduleTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}