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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import getNavigator
import getPreferences
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.data_transfer
import scheduledesktop2.composeapp.generated.resources.schedule_style_entries
import scheduledesktop2.composeapp.generated.resources.share
import java.util.prefs.Preferences


/**
 * Эта функция представляет собой экран настроек приложения
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Preview
@Composable
fun SettingsActivity() {
    val navigator = getNavigator()
    val preferences by remember { mutableStateOf(getPreferences()) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.settings)) },
                navigationIcon = {
                    IconButton(
                        { navigator.goBack() }
                    ){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp)
        ){
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
            ){
                item{
                    ListPreference(
                        title = Res.string.schedule_style,
                        key = "scheduleStyle",
                        entryValues = Res.array.schedule_style_values,
                        entries = Res.array.schedule_style_entries,
                        preferences = preferences
                    )
                }
                item{
                    SwitchPreference(
                        title = Res.string.option_add_teacher_search,
                        key = "addTeacherSearch",
                        preferences = preferences
                    )
                }
                item {
                    SwitchPreference(
                        title = Res.string.option_do_not_update_times,
                        key = "doNotUpdateTimes",
                        preferences = preferences
                    )
                }
                item {
                    ListPreference(
                        title = Res.string.option_download_for,
                        key = "downloadFor",
                        entryValues = Res.array.download_values,
                        entries =  Res.array.download_entries,
                        preferences = preferences
                    )
                }
                item {
                    SwitchPreference(
                        title = Res.string.option_enable_caching,
                        key = "enableCaching",
                        preferences = preferences
                    )
                }
                item{
                    ListPreference(
                        title = Res.string.option_theme,
                        key = "theme",
                        entryValues = Res.array.theme_values,
                        entries = Res.array.theme_entries,
                        preferences = preferences
                    )
                }
                item{
                    ListPreference(
                        title = Res.string.option_language,
                        key = "language",
                        entryValues = Res.array.language_values,
                        entries = Res.array.language_entries,
                        preferences = preferences
                    )
                }
            }
            Row{
                Spacer(modifier = Modifier.weight(0.5f))
                Button({}){
                    Text(stringResource(Res.string.data_transfer))
                }
            }
            Button({}, modifier = Modifier.fillMaxWidth()){
                Text(stringResource(Res.string.share))
            }
            Text(
                text = "2024© Ипатов Никита",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
    }
}

@Composable
fun SwitchPreference(title: StringResource,
                     key: String,
                     defaultValue: Boolean = false,
                     preferences: Preferences){
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

@Composable
fun ListPreference(title: StringResource,
                   key: String,
                   entryValues: StringArrayResource,
                   entries: StringArrayResource,
                   preferences: Preferences){
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
            onDismissRequest = { },
            modifier = Modifier.fillMaxWidth()
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