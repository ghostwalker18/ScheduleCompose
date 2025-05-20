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

package com.ghostwalker18.schedule.widgets

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import com.ghostwalker18.schedule.ui.theme.gray500Color
import com.russhwolf.settings.Settings
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getStringArray
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringArrayResource
import org.jetbrains.compose.resources.stringResource

/**
 * Эта функция служит для группировки элементов настроек
 * @param title название категории
 * @param content настройки в категории
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun PreferenceCategory(
    title: String,
    content: @Composable () -> Unit
){
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
 * @param title название настройки
 * @param key ключ для сохранения настройки
 * @param defaultValue значение по умолчанию для настройки
 * @param preferences объект настроек, где будет сохраняться значение
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun SwitchPreference(
    title: StringResource,
    key: String,
    defaultValue: Boolean = false,
    preferences: Settings
){
    //Initializing preferences with default value (at once)
    remember { preferences.putBoolean(key, preferences.getBoolean(key, defaultValue)) }

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
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = MaterialTheme.colors.primaryVariant,
                uncheckedThumbColor = Color.LightGray
            )
        )
    }
}

/**
 * Эта функция служит для отображения настройки в виде списка.
 * @param title название настройки
 * @param key ключ для сохранения настройки
 * @param entries отображаемые элементы списка
 * @param entryValues значения элементов списка
 * @param defaultValue значение по умолчанию для настройки
 * @param preferences объект настроек, где будет сохраняться значение
 * @param entryDrawables значки для элементов списка
 * @param entryDrawableDescr описание значка для элемента списка
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun ListPreference(
    title: StringResource,
    key: String,
    entryValues: StringArrayResource,
    entries: StringArrayResource,
    defaultValue: String? = null,
    preferences: Settings,
    entryDrawables: Array<DrawableResource>? = null,
    entryDrawableDescr: StringResource? = null
){
    //Initializing preferences with default value (at once)
    val currentValue = remember {
        return@remember defaultValue?.let{
            runBlocking {
                val value = preferences.getString(key, it)
                preferences.putString(key, value)
                return@runBlocking value
            }
        }
    }
    var currentValueImage: Int? by rememberSaveable {

        fun searchDrawableFor (entryValues: StringArrayResource, value: String?): Int? {
            return value?.let{
                val entryArray = runBlocking { getStringArray(entryValues) }
                return entryArray.indexOf(value)
            }
        }

        mutableStateOf(searchDrawableFor(entryValues, currentValue))
    }

    Column(
        modifier = Modifier.fillMaxWidth()
    ){
        var exp by remember { mutableStateOf(false) }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ){
            Text(
                text = stringResource(title),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    //.fillMaxWidth()
                    .padding(5.dp)
                    .clickable {
                        exp = true
                    }
            )
            currentValueImage?.let{
                index ->
                entryDrawables?.let{
                    Image(
                        painter = painterResource(it[index]),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
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
            optionEntries.forEachIndexed {
                index, entry ->
                DropdownMenuItem(
                    onClick = {
                        entryDrawables?.let {
                            currentValueImage = index
                        }
                        preferences.putString(key, optionValues[index])
                        exp = false
                    }
                ){
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ){
                        Text(
                            text = entry,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                        entryDrawables?.let {
                            Image(
                                painter = painterResource(it[index]),
                                contentDescription = entryDrawableDescr?.let{
                                    stringResource(it)
                                },
                                modifier = Modifier.size(50.dp)
                            )
                        }
                    }

                }
            }
        }
    }
}