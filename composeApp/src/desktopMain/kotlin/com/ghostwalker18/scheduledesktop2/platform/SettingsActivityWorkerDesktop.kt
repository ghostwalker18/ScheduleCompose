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

package com.ghostwalker18.scheduledesktop2.platform

import SettingsActivityWorker
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.developer_email
import scheduledesktop2.composeapp.generated.resources.email_subject
import scheduledesktop2.composeapp.generated.resources.share_email_completed
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.net.URI

/**
 * Этот класс выполняет операции для SettingsActivity на десктопе.
 * @author Ипатов Никита
 * @since 1.0
 */
class SettingsActivityWorkerDesktop : SettingsActivityWorker {

    override fun connectToDeveloper(): Pair<Boolean, StringResource> {
        try {
            Desktop.getDesktop().mail(
                URI("mailto:" + runBlocking { return@runBlocking getString(Res.string.developer_email) }
                            + "?subject=" + runBlocking { return@runBlocking getString(Res.string.email_subject) }
                )
            )
            return Pair(false, Res.string.share_email_completed)
        } catch (e: Exception) {
            Toolkit.getDefaultToolkit()
                .systemClipboard
                .setContents(StringSelection(runBlocking { return@runBlocking getString(Res.string.developer_email) }), null)
            return Pair(true, Res.string.share_email_completed)
        }
    }
}