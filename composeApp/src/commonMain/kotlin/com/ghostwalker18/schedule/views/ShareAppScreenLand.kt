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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.ScheduleApp
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
    val navigator = ScheduleApp.instance.navigator
    val worker = ScheduleApp.instance.shareController
    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()
    var showDesktop by remember { mutableStateOf(false) }

    Scaffold(
        scaffoldState =  scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.share_app)) },
                navigationIcon = {
                    IconButton({ navigator.goBack()}){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back_descr)
                        )
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
        Column{
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ){
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Filled.Smartphone,
                    contentDescription = ""
                )
                Switch(
                    checked = showDesktop,
                    onCheckedChange = {
                        showDesktop = it
                    }
                )
                Icon(
                    imageVector = Icons.Filled.Computer,
                    contentDescription = ""
                )
            }
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

                    AnimatedContent(
                        targetState = showDesktop,
                        transitionSpec = {
                            fadeIn() + scaleIn() togetherWith fadeOut() + scaleOut()
                        }
                    ){
                        targetState ->
                        val painter = painterResource(
                            if(targetState)
                                Res.drawable.qr_code_github
                            else
                                Res.drawable.qr_code_rustore
                        )
                        Image(
                            painter = painter,
                            contentDescription = stringResource(Res.string.share_app_qr_descr)
                        )
                    }
                }
                Text(
                    text = stringResource(Res.string.or),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(10.dp)
                )
                Button(
                    onClick = {
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
}