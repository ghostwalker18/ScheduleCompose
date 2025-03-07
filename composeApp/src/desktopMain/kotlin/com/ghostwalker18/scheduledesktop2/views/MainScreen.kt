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

import URLs
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ghostwalker18.scheduledesktop2.platform.DownloadDialog
import getMainScreenController
import getNavigator
import getScheduleRepository
import kotlinx.coroutines.launch
import models.Lesson
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.app_name
import scheduledesktop2.composeapp.generated.resources.days_tab
import viewmodels.DayModel

/**
 * Эта функция представляет собой главный экран приложения
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Preview
@Composable
fun MainActivity() {
    val navigator = getNavigator()
    val worker = getMainScreenController()
    val scope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()
    val pagerState = rememberPagerState{ 2 }
    val dayModels = mutableListOf<DayModel>()
    for (id in arrayOf(Res.string.monday, Res.string.tuesday,
        Res.string.wednesday, Res.string.thursday, Res.string.friday)
    ){
        dayModels.add(viewModel(key = id.key){ DayModel() })
    }
    val isDownloadDialogEnabled = remember { mutableStateOf(false) }
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.app_name)) },
                actions = {
                    IconButton(
                        {
                            when(pagerState.currentPage){
                                0 -> {
                                    val lessons = mutableListOf<Lesson>()
                                    for(model in dayModels){
                                        if(model.isOpened.value)
                                            for (lesson in model.lessons.value){
                                                lessons += lesson
                                            }
                                    }
                                    val (showTextRequired, text) = worker.shareSchedule(lessons)
                                    if (showTextRequired)
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(getString(text))
                                        }
                                }
                                1 -> {
                                    val (showTextRequired, text) = worker.shareTimes()
                                    if (showTextRequired)
                                        scope.launch {
                                            scaffoldState.snackbarHostState.showSnackbar(getString(text))
                                        }
                                }
                            }
                        }
                    ){
                        Icon(Icons.Filled.Share, "")
                    }
                    IconButton(
                        {isDownloadDialogEnabled.value = true}
                    ){
                        Icon(Icons.Filled.Download, "")
                    }
                    IconButton(
                        { navigator.goSettingsActivity() }
                    ) {
                        Icon(Icons.Filled.Settings, "")
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
    ){ innerPadding ->
        AnimatedVisibility(isDownloadDialogEnabled.value){
            val links = mutableListOf<String>()
            lateinit var mimeType:String
            if(pagerState.currentPage == 0){
                links += getScheduleRepository().linksForFirstCorpusSchedule
                links += getScheduleRepository().linksForSecondCorpusSchedule
                mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
            } else {
                links += listOf(URLs.MONDAY_TIMES_URL, URLs.OTHER_TIMES_URL)
                mimeType = "image/jpg"
            }
            DownloadDialog(
                isDownloadDialogEnabled,
                "placeholder",
                links.toTypedArray(),
                mimeType)
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ){
            TabRow(
                selectedTabIndex = 0
            ){
                Tab(
                    modifier = Modifier
                        .padding(15.dp),
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                ){
                    Text(stringResource(Res.string.days_tab))
                }
                Tab(
                    modifier = Modifier
                        .padding(15.dp),
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                ){
                    Text(stringResource(Res.string.times_tab))
                }
            }
            HorizontalPager(pagerState){
                when(it){
                    0 -> DaysFragment()
                    1 -> TimesFragment()
                }
            }
        }
    }
}