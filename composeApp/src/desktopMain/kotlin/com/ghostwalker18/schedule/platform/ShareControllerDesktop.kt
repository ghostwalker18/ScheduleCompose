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

package com.ghostwalker18.schedule.platform

import com.ghostwalker18.schedule.ShareController
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.models.Note
import com.ghostwalker18.schedule.models.ScheduleRepository
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.nothing_to_share
import scheduledesktop2.composeapp.generated.resources.share_completed
import scheduledesktop2.composeapp.generated.resources.share_times_completed
import java.awt.Desktop
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection
import java.io.File
import java.net.URI

/**
 * Этот класс представляет реализацию контроллера шэринга
 * приложения для десктопа.
 *
 * @author Ипатов Никита
 */
class ShareControllerDesktop : ShareController {

    /**
     * Этот метод используется для добавления форматированной строки расписания в системный
     * буфер обмена и уведомления об этом.
     */
    override fun shareSchedule(lessons: Collection<Lesson>): Pair<Boolean, StringResource> {
        if(lessons.isEmpty()){
            return Pair(true, Res.string.nothing_to_share)
        }
        else {
            val builder = StringBuilder()
            for(lesson in lessons){
                builder.append(lesson.toString(), "\n")
            }
            Toolkit.getDefaultToolkit()
                .systemClipboard
                .setContents(StringSelection(builder.toString()), null)
            return Pair(true, Res.string.share_completed)
        }
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
                    .add(File(ScheduleRepository.MONDAY_TIMES_PATH))
                    .add(File(ScheduleRepository.OTHER_TIMES_PATH)),
                null
            )
        return Pair(true, Res.string.share_times_completed)
    }

    override fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource> {
        val builder = StringBuilder()
        for(note in notes){
            builder.append(note.toString())
            builder.append("\n")
        }
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(StringSelection(builder.toString()), null)
        return Pair(true, Res.string.notes_share_completed)
    }

    override fun shareLink(): Pair<Boolean, StringResource> {
        val link = runBlocking {
            return@runBlocking getString(Res.string.github_link)
        }
        Toolkit.getDefaultToolkit()
            .systemClipboard
            .setContents(StringSelection(link), null)
        return Pair(true, Res.string.share_link_completed)
    }

    override fun connectToDeveloper(): Pair<Boolean, StringResource> {
        try {
            Desktop.getDesktop().mail(
                URI("mailto:" + runBlocking { return@runBlocking getString(Res.string.developer_email) }
                        + "?subject=" + runBlocking { return@runBlocking getString(Res.string.email_subject) }
                )
            )
            return Pair(false, Res.string.share_email_completed)
        } catch (_: Exception) {
            Toolkit.getDefaultToolkit()
                .systemClipboard
                .setContents(StringSelection(runBlocking { return@runBlocking getString(Res.string.developer_email) }), null)
            return Pair(true, Res.string.share_email_completed)
        }
    }
}