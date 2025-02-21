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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import getNavigator
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.data_types
import scheduledesktop2.composeapp.generated.resources.import_activity
import scheduledesktop2.composeapp.generated.resources.operation_type

/**
 * Эта функция представляет собой экран импорта и экспорта данных приложения
 */
@Composable
fun ImportActivity(){
    val navigator = getNavigator()
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.import_activity)) },
                navigationIcon = {
                    IconButton({ navigator.goBack() }){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, null)
                    }
                }
            )
        }
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ) {
            Row {
                Text(
                    text = stringResource(Res.string.operation_type),
                    modifier = Modifier
                        .weight(0.5f)
                )
            }
            Row {
                Text(
                    text = stringResource(Res.string.data_types),
                    modifier = Modifier
                        .weight(0.5f)
                )
            }
            AnimatedVisibility(true){
                Row {
                    Text(
                        text = stringResource(Res.string.import_policy_type),
                        modifier = Modifier
                            .weight(0.5f)
                    )

                }
            }
            Spacer(Modifier.weight(0.5f))
            Button(
                {},
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Text(text = "")
            }
        }
    }
}