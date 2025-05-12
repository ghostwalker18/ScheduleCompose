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

import androidx.compose.runtime.Composable
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.models.Note
import org.jetbrains.compose.resources.StringResource
import java.util.*

actual fun getScreenOrientation(): Orientation = Orientation.LandScape

actual fun getPlatform(): Platform = Platform.Desktop

@Composable
actual fun hideKeyboard(){/*Not required*/}

actual fun grantURIPermission(photoIDs: List<String>) { /*Not required*/ }

actual fun addNoteReminder(noteID: Int, delay: Long) { /*Not required*/ }

actual fun removeNoteReminder(noteID: Int) { /*Not required*/ }

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface Navigator{

    actual fun goBack()

    actual fun goScheduleScreen(date: Calendar, group: String?, teacher: String?)

    actual fun goSettingsActivity()

    actual fun goShareAppActivity()

    actual fun goImportActivity()

    actual fun goNotesActivity(group: String, date: Calendar)

    actual fun goEditNoteActivity(group: String, date: Calendar, noteID: Int?)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface ShareController {

    actual fun shareSchedule(lessons: Collection<Lesson>): Pair<Boolean, StringResource>

    actual fun shareTimes(): Pair<Boolean, StringResource>

    actual fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource>

    actual fun shareLink(platform: Platform): Pair<Boolean, StringResource>

    actual fun connectToDeveloper(): Pair<Boolean, StringResource>
}

actual fun notifyEvent(eventName: String) {/* Not required now */}