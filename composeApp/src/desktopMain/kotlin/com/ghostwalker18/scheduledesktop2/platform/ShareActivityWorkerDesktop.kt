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

import ShareActivityWorker
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.github_link
import scheduledesktop2.composeapp.generated.resources.share_link_completed
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * Этот класс выполняет операции для ShareActivity на десктопе
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class ShareActivityWorkerDesktop : ShareActivityWorker{

    override fun shareLink(): Pair<Boolean, StringResource> {
        val link = runBlocking {
            return@runBlocking getString(Res.string.github_link)
        }
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(StringSelection(link), null)
        return Pair(true, Res.string.share_link_completed)
    }
}