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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import getNavigator
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.app_name
import scheduledesktop2.composeapp.generated.resources.days_tab


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
    Scaffold(topBar = {
        TopAppBar(
            title = { Text(stringResource(Res.string.app_name)) },
            actions = {
                IconButton(
                    {}
                ){
                    Icon(Icons.Filled.Share, "")
                }
                IconButton(
                    {}
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
    }
    ){ innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ){
            val scope = rememberCoroutineScope()
            val pagerState = rememberPagerState{ 2 }
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