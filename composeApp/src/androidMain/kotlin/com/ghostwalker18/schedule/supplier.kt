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

import models.Lesson
import models.Note
import org.jetbrains.compose.resources.StringResource
import java.util.*

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface Navigator{
    actual fun goBack()
    actual fun goSettingsActivity()
    actual fun goShareAppActivity()
    actual fun goImportActivity()
    actual fun goNotesActivity(group: String, date: Calendar)
    actual fun goEditNoteActivity(group: String, date: Calendar, noteID: Int?)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface MainScreenController {
    actual fun shareSchedule(lessons: Collection<Lesson>): Pair<Boolean, StringResource>
    actual fun shareTimes(): Pair<Boolean, StringResource>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface NotesScreenController {
    actual fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface ShareScreenController {
    actual fun shareLink(): Pair<Boolean, StringResource>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface SettingsActivityController {
    actual fun connectToDeveloper(): Pair<Boolean, StringResource>
}