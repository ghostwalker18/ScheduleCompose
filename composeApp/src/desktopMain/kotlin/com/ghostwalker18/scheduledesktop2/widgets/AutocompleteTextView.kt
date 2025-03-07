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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.PopupProperties

/**
 * Эта функция используется для ввода с автоподбором
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun AutocompleteTextView(
    value: String = "",
    onDismissRequest: () -> Unit = {},
    competitionThreshold: Int = 0,
    placeholder: String = "",
    options: Array<String>,
    modifier: Modifier = Modifier,
    onValueSet: (String) -> Unit = {}
) {
    var selectedOption by remember { mutableStateOf(value) }
    var exp by remember { mutableStateOf(false) }
    Column(modifier) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (!focusState.isFocused)
                        onDismissRequest()
                },
            value = selectedOption,
            placeholder = { Text(placeholder) },
            onValueChange = {
                selectedOption = it
                exp = if (selectedOption.length > competitionThreshold) true else false
                onValueSet(selectedOption) },
            colors = TextFieldDefaults.outlinedTextFieldColors()
        )
        val filterOpts = options.filter { it.contains(selectedOption, ignoreCase = true) }
        if(filterOpts.isNotEmpty()){
            DropdownMenu(
                expanded = exp,
                properties = PopupProperties(
                    focusable = false,
                    dismissOnBackPress = true,
                    dismissOnClickOutside = true
                ),
                onDismissRequest = onDismissRequest,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.background)
            ) {
                filterOpts.forEach { option ->
                    DropdownMenuItem(onClick = {
                        exp = false
                        selectedOption = option
                        onValueSet(option)
                    }) {
                        Text(text = option, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }
        }
    }
}