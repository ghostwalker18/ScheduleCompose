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

import com.ghostwalker18.schedule.NotesScreenController
import android.content.Context
import android.content.Intent
import com.ghostwalker18.schedule.ScheduleApp
import io.appmetrica.analytics.AppMetrica
import com.ghostwalker18.schedule.models.Note
import org.jetbrains.compose.resources.StringResource
import androidx.core.content.ContextCompat.startActivity
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.nothing_to_share

class NotesScreenControllerAndroid(private val context: Context) : NotesScreenController {

    override fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource> {
        if (ScheduleApp.getInstance().isAppMetricaActivated)
            AppMetrica.reportEvent("Поделились заметками")

        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        val notesToShare = StringBuilder()
        for (note in notes) {
            notesToShare.append(note.toString()).append("\n")
        }
        intent.putExtra(Intent.EXTRA_TEXT, notesToShare.toString())
        val shareIntent = Intent.createChooser(intent, null)
        startActivity(context, shareIntent, null)
        return Pair(false, Res.string.nothing_to_share)
    }
}