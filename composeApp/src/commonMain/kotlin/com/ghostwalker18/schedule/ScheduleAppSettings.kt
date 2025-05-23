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

/**
 * Этот объект содержит все настройки приложения
 * @author Ипатов Никита
 */
object ScheduleAppSettings {

    /**
     * Этот объект содержит настройки расписания
     */
    object ScheduleSettings {

        object ScheduleStyle {
            const val key = "scheduleStyle"
            const val defaultValue = "in_fragment"
        }

        object TeacherSearch {
            const  val key = "addTeacherSearch"
            const val defaultValue = false
        }

        object UpdateTimes {
            const val key = "doNotUpdateTimes"
        }
    }

    /**
     * Этот объект содержит настройки сети
     */
    object NetworkSettings {

        object DownloadFor {
            const val key = "downloadFor"
            const val defaultValue = "all"
        }

        object EnableCaching {
            const val key = "enableCaching"
            const val defaultValue = true
        }
    }

    /**
     * Этот объект содержит настройки приложения
     */
    object AppSettings {

        object Theme {
            const val key = "theme"
            const val defaultValue = "system"
        }

        object Language {
            const val key = "language"
            const val defaultValue = "system"
        }
    }

    /**
     * Этот объект содержит настройки уведомлений
     */
    object NotificationSettings {

        object UpdateNotifications{
            const val key = "update_notifications"
            const val defaultValue = false
        }

        object ScheduleNotifications {
            const val key = "schedule_notifications"
            const val defaultValue = false
        }

        object NotesNotifications {
            const val key = "notes_notifications"
            const val defaultValue = true
        }
    }
}