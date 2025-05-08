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

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.core.net.toUri
import androidx.work.WorkManager
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.models.Note
import com.ghostwalker18.schedule.notifications.NoteReminderService
import org.jetbrains.compose.resources.StringResource
import java.util.*

actual fun getScreenOrientation(): Orientation {
    return when (ScheduleApp.instance.resources.configuration.orientation){
        Configuration.ORIENTATION_PORTRAIT -> Orientation.Portrait
        else -> Orientation.LandScape
    }
}

actual fun getPlatform(): Platform = Platform.Mobile

@Composable
actual fun hideKeyboard(){
    LocalSoftwareKeyboardController.current?.hide()
}

actual fun grantURIPermission(photoIDs: List<String>) {
    for (photoID in photoIDs){
        ScheduleApp.instance.contentResolver
            .takePersistableUriPermission(
                photoID.toUri(),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
    }
}

actual fun addNoteReminder(noteID: Int, delay: Long) {
    val context = ScheduleApp.instance as Context
    /*val inputData = Data.Builder()
        .putInt("noteID", noteID)
        .build()
    val request = OneTimeWorkRequest.Builder(
        workerClass = NoteReminderNotificationWorker::class.java
    )
        .addTag("schedulePCCE_note_$noteID")
        .setInputData(inputData)
        .setInitialDelay(delay, TimeUnit.MINUTES)
        .build()
    WorkManager.getInstance(context).enqueue(request)*/
    val intent = Intent(context, NoteReminderService::class.java)
    intent.putExtra("noteID", noteID)
    intent.putExtra("delay", delay)
    context.startService(intent)
}

actual fun removeNoteReminder(noteID: Int) {
    val context = ScheduleApp.instance as Context
    WorkManager.getInstance(context)
        .cancelAllWorkByTag("schedulePCCE_note_$noteID")
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface Navigator{

    actual fun goBack()

    actual fun goScheduleScreen(date: Calendar, group: String?, teacher: String?)

    actual fun goSettingsActivity()

    actual fun goShareAppActivity()

    actual fun goImportActivity()

    actual fun goNotesActivity(group: String, date: Calendar)

    actual fun goEditNoteActivity(group: String, date: Calendar, noteID: Int?)

    fun goPhotoView(photoID: String)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface ShareController {

    actual fun shareSchedule(lessons: Collection<Lesson>): Pair<Boolean, StringResource>

    actual fun shareTimes(): Pair<Boolean, StringResource>

    actual fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource>

    actual fun shareLink(platform: Platform): Pair<Boolean, StringResource>

    actual fun connectToDeveloper(): Pair<Boolean, StringResource>
}