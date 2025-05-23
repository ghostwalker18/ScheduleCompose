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

/**
 * Это перечисление описывает возможные ориентации экрана для приложения.
 */
enum class Orientation {
    Portrait, LandScape
}

/**
 * Эта функция позволяет получить текущую ориентацию экрана приложения.
 */
expect fun getScreenOrientation(): Orientation

/**
 * Это перечисление описывает платформы, под какими может быть запущено приложение.
 */
enum class Platform {
    Mobile, Desktop
}

/**
 * Эта функция позволяет получить текущую платформу, под которой запущено приложение.
 */
expect fun getPlatform(): Platform

/**
 * Эта функция скрывает экранную клавиатуру, если такая доступна для платформы.
 */
@Composable
expect fun hideKeyboard()

/**
 * Эта функция выдает приложению разрешения на доступ к фото заметок, если того требует платформа.
 */
expect fun grantURIPermission(photoIDs: List<String>)

/**
 * Эта функция добавляет к заметке напоминание, если того требует платформа.
 */
expect fun addNoteReminder(noteID: Int, delay: Long)

/**
 * Эта функция убирает напоминание о заметке, если того требует платформа.
 */
expect fun removeNoteReminder(noteID: Int)

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
     * Этот метод позволяет перейти к экрану просмотра расписания на день.
     */
    fun goScheduleScreen(date: Calendar, group: String?, teacher: String?)

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

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
expect interface ShareController {

    /**
     * Этот метод позволяет поделиться расписанием занятий.
     */
    fun shareSchedule(lessons: Collection<Lesson>): Pair<Boolean, StringResource>

    /**
     * Этот метод позволяет поделиться расписанием звонков.
     */
    fun shareTimes(): Pair<Boolean, StringResource>

    /**
     * Этот метод позволяет поделиться заметками.
     */
    fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource>

    /**
     * Этот метод позволяет поделиться ссылкой на приложение.
     */
    fun shareLink(platform: Platform): Pair<Boolean, StringResource>

    /**
     * Этот метод позволяет связаться с разработчиком.
     */
    fun connectToDeveloper(): Pair<Boolean, StringResource>
}

/**
 * Эта функция используется для аналитики о событиях в приложении
 */
expect fun notifyEvent(eventName: String)