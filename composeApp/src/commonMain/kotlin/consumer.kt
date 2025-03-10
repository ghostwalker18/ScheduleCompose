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
import androidx.compose.runtime.MutableState
import androidx.room.RoomDatabase
import com.russhwolf.settings.Settings
import database.AppDatabase
import models.Lesson
import models.Note
import models.NotesRepository
import models.ScheduleRepository
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import java.util.*
import java.util.prefs.Preferences

expect fun getDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>

/**
 * Эта функция позволяет получить доступ к репозиторию расписания приложения.
 */
expect fun getScheduleRepository(): ScheduleRepository

/**
 * Эта функция позволяет получить доступ к репозиторию заметок приложения.
 */
expect fun getNotesRepository(): NotesRepository

/**
 * Эта функция позволяет получить доступ к настройкам приложения.
 */
expect fun getPreferences(): Settings

/**
 * Этот интерфейс описывает навигацию по приложению.
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface Navigator {

    /**
     * Этот метод позволяет перейти к предыдущему экрану приложения.
     */
    fun goBack()

    /**
     * Этот метод позволяет перейти к экрану настроек приложения.
     */
    fun goSettingsActivity()

    /**
     * Этот метод позволяет перейти к экрану шэринга приложения.
     */
    fun goShareAppActivity()

    /**
     * Этот метод позволяет перейти к экрану импорта/экспорта данных приложения.
     */
    fun goImportActivity()

    /**
     * Этот метод позволяет перейти к экрану заметок приложения.
     */
    fun goNotesActivity(group: String, date: Calendar)

    /**
     * Этот метод позволяет перейти к экрану добавления/редактирования заметок приложения.
     */
    fun goEditNoteActivity(group: String, date: Calendar, noteID: Int? = null)
}

/**
 * Этот интерфейс определяет платформно-зависимые операции для MainActivity
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface MainScreenController {

    /**
     * Этот метод позволяет поделиться расписанием занятий.
     */
    fun shareSchedule(lessons: Collection<Lesson>): Pair<Boolean, StringResource>

    /**
     * Этот метод позволяет поделиться расписанием звонков.
     */
    fun shareTimes(): Pair<Boolean, StringResource>
}

/**
 * Этот интерфейс определяет платформно-зависимые операции для NotesActivity
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface NotesScreenController{

    /**
     * Этот метод позволяет поделиться заметками.
     */
    fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource>
}

/**
 * Этот интерфейс определяет платформно-зависимые операции для ShareActivity
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface ShareScreenController {

    /**
     * Этот метод позволяет поделиться ссылкой на приложение
     */
    fun shareLink(): Pair<Boolean, StringResource>
}

/**
 * Этот интерфейс определяет платформно-зависимые операции для SettingsActivity
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface SettingsActivityController {

    /**
     * Этот метод позволяет связаться с разработчиком.
     */
    fun connectToDeveloper(): Pair<Boolean, StringResource>
}

/**
 * Эта функция позволяет получить доступ к навигатору приложения
 */
expect fun getNavigator(): Navigator

expect fun getMainScreenController(): MainScreenController
expect fun getNotesScreenController(): NotesScreenController
expect fun getShareScreenController(): ShareScreenController
expect fun getSettingsScreenController(): SettingsActivityController

expect fun getAppQR(): DrawableResource

@Composable
expect fun getDownloadDialog(
    isEnabled: MutableState<Boolean>,
    title: String,
    links: Array<String>,
    mimeType: String
)