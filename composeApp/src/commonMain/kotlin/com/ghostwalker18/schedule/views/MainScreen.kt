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
import androidx.compose.foundation.background
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
import com.ghostwalker18.schedule.*
import kotlinx.coroutines.launch
import com.ghostwalker18.schedule.models.Lesson
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.days_tab
import com.ghostwalker18.schedule.viewmodels.DayModel
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.russhwolf.settings.get

/**
 * Эта функция представляет собой главный экран приложения
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navigator = ScheduleApp.instance.navigator
    val worker = ScheduleApp.instance.shareController
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
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    Scaffold(
        scaffoldState = scaffoldState,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.schedule)) },
                actions = {
                    AnimatedVisibility(pagerState.currentPage == 0
                            && ScheduleApp.instance.preferences["scheduleStyle", "in_fragment"] == "in_fragment"
                            || pagerState.currentPage == 1
                    ){
                        ContentWrapper(
                            toolTip = Res.string.main_share_schedule_descr
                        ){
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
                                Icon(
                                    imageVector = Icons.Filled.Share,
                                    contentDescription = stringResource(Res.string.main_share_schedule_descr)
                                )
                            }
                        }
                    }
                    ContentWrapper(
                        toolTip = Res.string.main_download_descr
                    ){
                        IconButton({ isDownloadDialogEnabled.value = true }){
                            Icon(
                                imageVector = Icons.Filled.Download,
                                contentDescription = stringResource(Res.string.main_download_descr)
                            )
                        }
                    }
                    ContentWrapper(
                        toolTip = Res.string.main_settings_descr
                    ){
                        IconButton({ navigator.goSettingsActivity() }){
                            Icon(
                                imageVector = Icons.Filled.Settings,
                                contentDescription = stringResource(Res.string.main_settings_descr)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colors.primary,
                    containerColor = MaterialTheme.colors.primary
                ),
                scrollBehavior = scrollBehavior
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
        },
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection)
    ){
        innerPadding ->
        AnimatedVisibility(isDownloadDialogEnabled.value){
            val links = mutableListOf<String>()
            lateinit var mimeType:String
            if(pagerState.currentPage == 0){
                links += ScheduleApp.instance.scheduleRepository.linksForFirstCorpusSchedule
                links += ScheduleApp.instance.scheduleRepository.linksForSecondCorpusSchedule
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
                selectedTabIndex = pagerState.currentPage,
            ){
                Tab(
                    modifier = Modifier
                        .background(
                            if(pagerState.currentPage == 0)
                                MaterialTheme.colors.secondary
                            else
                                MaterialTheme.colors.primary
                        ),
                    selected = pagerState.currentPage == 0,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(0)
                        }
                    }
                ){
                    Text(
                        modifier = Modifier
                            .padding(15.dp),
                        text = stringResource(Res.string.days_tab)
                    )
                }
                Tab(
                    modifier = Modifier
                        .background(
                            if(pagerState.currentPage == 1)
                                MaterialTheme.colors.secondary
                            else
                                MaterialTheme.colors.primary
                        ),
                    selected = pagerState.currentPage == 1,
                    onClick = {
                        scope.launch {
                            pagerState.animateScrollToPage(1)
                        }
                    }
                ){
                    Text(
                        modifier = Modifier
                            .padding(15.dp),
                        text = stringResource(Res.string.times_tab)
                    )
                }
            }
            HorizontalPager(pagerState){
                when(it){
                    0 -> when(getScreenOrientation()){
                        Orientation.Portrait -> DaysFragmentPortrait()
                        else -> DaysFragmentLand()
                    }
                    1 -> when(getScreenOrientation()){
                        Orientation.Portrait -> TimesFragmentPortrait()
                        else -> TimesFragmentLand()
                    }
                }
            }
        }
    }
}