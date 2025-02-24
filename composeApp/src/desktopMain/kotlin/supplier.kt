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

import com.ghostwalker18.scheduledesktop2.ScheduleApp
import models.Lesson
import models.Note
import models.NotesRepository
import models.ScheduleRepository
import org.jetbrains.compose.resources.StringResource
import java.util.*
import java.util.prefs.Preferences

actual fun getScheduleRepository(): ScheduleRepository = ScheduleApp.getInstance().scheduleRepository

actual fun getNotesRepository(): NotesRepository = ScheduleApp.getInstance().notesRepository

actual fun getPreferences(): Preferences = ScheduleApp.getInstance().preferences

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
actual interface MainActivityWorker {
    actual fun shareSchedule(lessons: Collection<Lesson>): Pair<Boolean, StringResource>
    actual fun shareTimes(): Pair<Boolean, StringResource>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface NotesActivityWorker {
    actual fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface ShareActivityWorker {
    actual fun shareLink(): Pair<Boolean, StringResource>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface SettingsActivityWorker {
    actual fun connectToDeveloper(): Pair<Boolean, StringResource>
}

actual fun getNavigator(): Navigator = ScheduleApp.getInstance().navigator
actual fun getMainActivityWorker(): MainActivityWorker = ScheduleApp.getInstance().mainActivityWorker
actual fun getNotesActivityWorker(): NotesActivityWorker = ScheduleApp.getInstance().notesActivityWorker
actual fun getShareActivityWorker(): ShareActivityWorker = ScheduleApp.getInstance().shareActivityWorker
actual fun getSettingsActivityWorker(): SettingsActivityWorker = ScheduleApp.getInstance().settingsActivityWorker