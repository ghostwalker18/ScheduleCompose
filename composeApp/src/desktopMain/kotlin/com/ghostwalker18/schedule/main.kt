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

package com.ghostwalker18.schedule

import androidx.compose.ui.window.*
import com.ghostwalker18.schedule.network.NetworkService
import com.ghostwalker18.schedule.notifications.NotificationWorker
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.app_name
import scheduledesktop2.composeapp.generated.resources.check_for_update
import scheduledesktop2.composeapp.generated.resources.favicon

/**
 * Точка входа в десктопное приложение.
 */
fun main() = application {
    val tray = rememberTrayState()

    Tray(
        icon = painterResource(Res.drawable.favicon),
        state = tray,
        menu = {
            Item(
                text = stringResource(Res.string.check_for_update),
                onClick = {
                    val worker = NotificationWorker(
                        updateAPI = NetworkService(URLs.BASE_URI).getUpdateAPI(),
                        tray = tray
                    )
                    worker.checkForUpdates()
                }
            )
        }
    )

    Window(
        onCloseRequest = ::exitApplication,
        title = stringResource(Res.string.app_name),
        icon = painterResource(Res.drawable.favicon)
    ) {
        val scheduleApp = ScheduleApp()
        scheduleApp.App()
    }
}