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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.getAppQR
import com.ghostwalker18.schedule.getNavigator
import com.ghostwalker18.schedule.getShareScreenController
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.scan_qr_code
import scheduledesktop2.composeapp.generated.resources.share_app

/**
 * Эта функция представляет собой экран приложения для возможности поделиться им.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun ShareAppScreenLand() {
    val navigator = getNavigator()
    val worker = getShareScreenController()
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    Scaffold(
        scaffoldState =  scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.share_app)) },
                navigationIcon = {
                    IconButton(
                        { navigator.goBack()}
                    ){
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "")
                    }
                },
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
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize()
        ){
            Column(
                modifier = Modifier.weight(0.5f)
            ) {
                Text(
                    text = stringResource(Res.string.scan_qr_code),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(5.dp)
                        .fillMaxWidth()
                )
                Image(painterResource(getAppQR()), null)
            }
            Text(
                text = stringResource(Res.string.or),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(10.dp)
            )
            Button({
                val(showMessageRequired, text) = worker.shareLink()
                if(showMessageRequired)
                    scope.launch {
                        scaffoldState.snackbarHostState.showSnackbar(getString(text))
                    }
            },
                modifier = Modifier
                    .weight(0.5f)
            ){
                Text(
                    text = stringResource(Res.string.share_link),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}