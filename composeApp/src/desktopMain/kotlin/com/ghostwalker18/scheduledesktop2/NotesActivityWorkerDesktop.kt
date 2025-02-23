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

import NotesActivityWorker
import models.Note
import org.jetbrains.compose.resources.StringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.notes_share_completed
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

/**
 * Этот класс исполняет операции для NotesActivity на десктопе.
 * @author Ипатов Никита
 * @since 1.0
 */
class NotesActivityWorkerDesktop : NotesActivityWorker {

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
}