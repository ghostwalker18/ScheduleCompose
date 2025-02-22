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

package com.ghostwalker18.scheduledesktop2

import MainActivityWorker
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.Popup
import com.ghostwalker18.scheduledesktop2.platform.FileTransferable
import models.ScheduleRepository
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.share_completed
import scheduledesktop2.composeapp.generated.resources.share_times_completed
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File

class MainActivityWorkerDesktop : MainActivityWorker {

    /**
     * Этот метод используется для добавления форматированной строки расписания в системный
     * буфер обмена и уведомления об этом.
     */
    override fun shareSchedule(): Pair<Boolean, StringResource> {
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(StringSelection("getSchedule()"), null)
        return Pair(true, Res.string.share_completed)
    }

    /**
     * Этот метод используется для добавления файлов звонков в системный буфер обмена и
     * уведомления об этом.
     */
    override fun shareTimes(): Pair<Boolean, StringResource>{
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(
                FileTransferable()
                    .add(File(ScheduleRepository.mondayTimesPath))
                    .add(File(ScheduleRepository.otherTimesPath)),
                null
            )
        return Pair(true, Res.string.share_times_completed)
    }
}