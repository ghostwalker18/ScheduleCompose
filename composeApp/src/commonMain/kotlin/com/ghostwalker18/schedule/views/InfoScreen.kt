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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.unit.dp
import be.digitalia.compose.htmlconverter.HtmlStyle
import be.digitalia.compose.htmlconverter.htmlToAnnotatedString
import com.ghostwalker18.schedule.ScheduleApp
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.app_info
import scheduledesktop2.composeapp.generated.resources.go_back_descr
import scheduledesktop2.composeapp.generated.resources.info
import scheduledesktop2.composeapp.generated.resources.scroll_up

/**
 * Эта функция отображает экран справки приложения
 *
 * @author Ипатов Никита
 */
@Composable
fun InfoScreen(){
    val scaffoldState = rememberScaffoldState()
    val navigator = ScheduleApp.instance.getNavigator()
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()

    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(Res.string.info)) },
                navigationIcon = {
                    IconButton({ navigator.goBack() }){
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(Res.string.go_back_descr)
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = scrollState.value > 10,
                enter = fadeIn(),
                exit = fadeOut()
            ){
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            scrollState.animateScrollTo(0)
                        }
                    },
                    backgroundColor = MaterialTheme.colors.primaryVariant
                ){
                    ContentWrapper(
                        toolTip = Res.string.scroll_up
                    ){
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.KeyboardArrowLeft,
                            tint = Color.White,
                            contentDescription = stringResource(Res.string.scroll_up)
                        )
                    }
                }
            }
        }
    ){
        val linkColor = MaterialTheme.colors.primary
        val text = remember {
            htmlToAnnotatedString(
                runBlocking { getString(Res.string.app_info) } ,
                style = HtmlStyle(
                    textLinkStyles = TextLinkStyles(
                        style = SpanStyle(color = linkColor)
                    )
                )
            )
        }
        Box(
            modifier = Modifier.fillMaxSize()
        ){
            Text(
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .padding(10.dp),
                text = text
            )
            ScrollBar(
                modifier = Modifier.align(Alignment.CenterEnd),
                state = scrollState
            )
        }
    }
}