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
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ghostwalker18.schedule.database.APP_DATABASE_NAME
import com.ghostwalker18.schedule.database.AppDatabase
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.models.Note
import com.ghostwalker18.schedule.models.NotesRepository
import com.ghostwalker18.schedule.models.ScheduleRepository
import com.russhwolf.settings.Settings
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.qr_code
import java.util.*


actual fun getScreenOrientation(): Orientation {
    return when (ScheduleApp.getInstance().resources.configuration.orientation){
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
        ScheduleApp.getInstance().contentResolver
            .takePersistableUriPermission(
                photoID.toUri(),
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
    }
}

actual fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>{
    val context = ScheduleApp.getInstance() as Context
    val dbFile = context.getDatabasePath(APP_DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}

actual fun getScheduleRepository(): ScheduleRepository = ScheduleApp.getInstance().scheduleRepository

actual fun getNotesRepository(): NotesRepository = ScheduleApp.getInstance().notesRepository

actual fun getPreferences(): Settings = ScheduleApp.getInstance().preferences

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface Navigator{
    actual fun goBack()
    actual fun goSettingsActivity()
    actual fun goShareAppActivity()
    actual fun goImportActivity()
    actual fun goNotesActivity(group: String, date: Calendar)
    actual fun goEditNoteActivity(group: String, date: Calendar, noteID: Int?)
    fun goPhotoView(photoID: String)
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
actual interface SettingsScreenController {
    actual fun connectToDeveloper(): Pair<Boolean, StringResource>
}

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual interface ImportScreenController{
    actual fun importDB()
    actual fun exportDB()
}

actual fun getNavigator(): Navigator = ScheduleApp.getInstance().navigator
actual fun getMainScreenController(): MainScreenController = ScheduleApp.getInstance().mainActivityController
actual fun getNotesScreenController(): NotesScreenController = ScheduleApp.getInstance().notesActivityController
actual fun getShareScreenController(): ShareScreenController = ScheduleApp.getInstance().shareActivityController
actual fun getSettingsScreenController(): SettingsScreenController = ScheduleApp.getInstance().settingsActivityController
actual fun getImportScreenController(): ImportScreenController = ScheduleApp.getInstance().importScreenController

actual fun getAppQR(): DrawableResource = Res.drawable.qr_code