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

package com.ghostwalker18.scheduledesktop2.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import gray500Color
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import java.util.prefs.Preferences

/**
 * Эта функция служит для группировки элементов настроек
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun PreferenceCategory(title: String, content: @Composable () -> Unit){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .bottomBorder(1.dp, gray500Color)
    ) {
        Text(
            text = title,
            color = MaterialTheme.colors.primaryVariant
        )
        content()
    }
}

/**
 * Эта функция служит для отображения настройки в виде переключателя.
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun SwitchPreference(title: StringResource,
                     key: String,
                     defaultValue: Boolean = false,
                     preferences: Preferences
){
    var checked by remember { mutableStateOf(preferences.getBoolean(key, defaultValue)) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(title),
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = {
                preferences.putBoolean(key, it)
                checked = it
            }
        )
    }
}

/**
 * Эта функция служит для отображения настройки в виде списка.
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun ListPreference(title: StringResource,
                   key: String,
                   entryValues: StringArrayResource,
                   entries: StringArrayResource,
                   preferences: Preferences
){
    Column(modifier = Modifier.fillMaxWidth()) {
        var exp by remember { mutableStateOf(false) }
        Text(
            text = stringResource(title),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .clickable {
                    exp = true
                })
        DropdownMenu(
            expanded = exp,
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            ),
            onDismissRequest = { exp = false},
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colors.background)
        ){
            val optionEntries = stringArrayResource(entries)
            val optionValues = stringArrayResource(entryValues)
            optionEntries.forEachIndexed{ index, entry ->
                DropdownMenuItem({
                    preferences.put(key, optionValues[index])
                    exp = false
                }) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = entry,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}