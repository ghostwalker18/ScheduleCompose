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

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.stringArrayResource

/**
 * Эта функция отображает выпадающий список.
 * @param entries отображаемые элементы списка
 * @param entryValues значения элементов списка
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun ListView(
    entries: StringArrayResource,
    entryValues: StringArrayResource,
    modifier: Modifier,
    onItemSelected: (selectedItem: String) -> Unit = {}
) {
    Box(
        modifier
    ){
        var exp by remember { mutableStateOf(false) }
        val initialValue = stringArrayResource(entries)[0]
        var selectedOption by remember { mutableStateOf(initialValue) }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    exp = true
                }
        ){
            Text(text = selectedOption)
            Icon(Icons.Filled.ArrowDropDown, null)
        }
        DropdownMenu(
            expanded = exp,
            onDismissRequest = {exp = false},
            properties = PopupProperties(
                focusable = false,
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
            ),
            modifier = Modifier
                .background(color = MaterialTheme.colors.background)
        ){
            val optionEntries = stringArrayResource(entries)
            val optionValues = stringArrayResource(entryValues)
            optionEntries.forEachIndexed{
                index, option ->
                DropdownMenuItem({
                    val selectedValue = optionValues[index]
                    selectedOption = option
                    exp = false
                    onItemSelected(selectedValue)
                }){
                    Text(text = option)
                }
            }
        }
    }
}