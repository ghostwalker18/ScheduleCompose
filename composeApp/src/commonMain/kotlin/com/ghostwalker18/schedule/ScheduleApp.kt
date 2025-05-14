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

import com.ghostwalker18.schedule.models.ScheduleRepository
import com.ghostwalker18.schedule.models.NotesRepository
import com.ghostwalker18.schedule.platform.ImportController
import com.russhwolf.settings.ObservableSettings

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
/**
 * Базовый прототип приложения для обеих платформ.
 *
 * @author Ипатов Никита
 */
expect class ScheduleApp() {
    /**
     * Репозиторий расписания приложения
     */
    val scheduleRepository: ScheduleRepository

    /**
     * Репозиторий заметок приложения
     */
    val notesRepository: NotesRepository

    /**
     * Настройки приложения
     */
    val preferences: ObservableSettings

    /**
     * Контроллер шэринга приложения
     */
    val shareController: ShareController

    /**
     * Контроллер импорта приложения
     */
    val importController: ImportController

    /**
     * Этот метод позволяет получить навигатор приложения
     */
    fun getNavigator(): Navigator

    companion object{
        /**
         * Синглтон приложения
         */
        val instance: ScheduleApp
    }
}