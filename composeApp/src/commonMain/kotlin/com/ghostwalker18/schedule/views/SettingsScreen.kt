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

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.widgets.ListPreference
import com.ghostwalker18.schedule.widgets.PreferenceCategory
import com.ghostwalker18.schedule.widgets.SwitchPreference
import com.ghostwalker18.schedule.getNavigator
import com.ghostwalker18.schedule.getPreferences
import com.ghostwalker18.schedule.getSettingsScreenController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.data_transfer
import scheduledesktop2.composeapp.generated.resources.schedule_style_entries

/**
 * Эта функция представляет собой экран настроек приложения
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun SettingsScreen() {
    val navigator = getNavigator()
    val preferences = getPreferences()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    val worker = getSettingsScreenController()
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
        },
        snackbarHost = {
            SnackbarHost(it) { data ->
                Snackbar(
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primaryVariant,
                    snackbarData = data
                )
            }
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
                item {
                    PreferenceCategory(
                        title = stringResource(Res.string.schedule_settings)
                    ){
                        ListPreference(
                            title = Res.string.schedule_style,
                            key = "scheduleStyle",
                            entryValues = Res.array.schedule_style_values,
                            entries = Res.array.schedule_style_entries,
                            preferences = preferences
                        )
                        SwitchPreference(
                            title = Res.string.option_add_teacher_search,
                            key = "addTeacherSearch",
                            preferences = preferences
                        )
                        SwitchPreference(
                            title = Res.string.option_do_not_update_times,
                            key = "doNotUpdateTimes",
                            preferences = preferences
                        )
                    }
                }
                item{
                    PreferenceCategory(
                        title = stringResource(Res.string.network_settings)
                    ){
                        ListPreference(
                            title = Res.string.option_download_for,
                            key = "downloadFor",
                            entryValues = Res.array.download_values,
                            entries =  Res.array.download_entries,
                            preferences = preferences
                        )
                        SwitchPreference(
                            title = Res.string.option_enable_caching,
                            key = "enableCaching",
                            preferences = preferences
                        )
                    }
                }
                item {
                    PreferenceCategory(
                        title = stringResource(Res.string.app_settings)
                    ){
                        ListPreference(
                            title = Res.string.option_theme,
                            key = "theme",
                            entryValues = Res.array.theme_values,
                            entries = Res.array.theme_entries,
                            preferences = preferences
                        )
                        ListPreference(
                            title = Res.string.option_language,
                            key = "language",
                            entryValues = Res.array.language_values,
                            entries = Res.array.language_entries,
                            preferences = preferences
                        )
                    }
                }
            }
            Row{
                Spacer(modifier = Modifier.weight(0.5f))
                Button(
                    { navigator.goImportActivity() },
                    modifier = Modifier.weight(0.5f)
                ){
                    Text(stringResource(Res.string.data_transfer))
                }
            }
            Button({ navigator.goShareAppActivity() },
                modifier = Modifier.fillMaxWidth()
            ){
                Text(stringResource(Res.string.share_app))
            }
            Text(
                text = "2024© Ипатов Никита",
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
                    .clickable {
                        val (showMessageRequired, text) = worker.connectToDeveloper()
                        if(showMessageRequired)
                            scope.launch {
                                scaffoldState.snackbarHostState.showSnackbar(getString(text))
                            }
                    }
            )
        }
    }
}