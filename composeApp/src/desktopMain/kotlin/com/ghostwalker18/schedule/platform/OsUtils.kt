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

package com.ghostwalker18.schedule.platform

import java.util.*

/**
 * Этот класс содержит методы и свойства, связанные с операционной системой.
 *
 * @author Ипатов Никита
 */
class OsUtils {

    /**
     * Это перечисление описывает поддерживаемые варианты ОС.
     */
    enum class OSType {
        Windows, MacOS, Linux, Unknown
    }

    companion object {
        /**
         * Это свойство описывает тип ОС хоста.
         */
        val hostOS = run {
            val os = System.getProperty("os.name", "generic").lowercase(Locale.ENGLISH)
            return@run when {
                os.contains("mac") || os.contains("darwin") -> OSType.MacOS
                os.contains("win") -> OSType.Windows
                os.contains("nux") || os.contains("nix") -> OSType.Linux
                else -> OSType.Unknown
            }
        }
    }
}