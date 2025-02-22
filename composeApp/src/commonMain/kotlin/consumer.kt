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

import androidx.compose.runtime.Composable
import models.NotesRepository
import models.ScheduleRepository
import org.jetbrains.compose.resources.StringResource
import java.util.*
import java.util.prefs.Preferences

expect fun getScheduleRepository(): ScheduleRepository

expect fun getNotesRepository(): NotesRepository

expect fun getPreferences(): Preferences

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface Navigator{
    fun goBack()
    fun goSettingsActivity()
    fun goShareAppActivity()
    fun goImportActivity()
    fun goNotesActivity(group: String, date: Calendar)
    fun goEditNoteActivity(group: String, date: Calendar, noteID: Int? = null)
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface MainActivityWorker{
    fun shareSchedule(): Pair<Boolean, StringResource>
    fun shareTimes(): Pair<Boolean, StringResource>
}

expect fun getNavigator(): Navigator
expect fun getMainActivityWorker(): MainActivityWorker