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

import androidx.compose.runtime.Composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Этот класс определяет платформно-зависимые операции для ImportScreen
 */
abstract class ImportController {

    /**
     * Это перечисление описывает возможные этапы операции по экспорту/импорту данных
     *
     * @author Ипатов Никита
     */
    enum class OperationStatus{
        Ready, Started, Unpacking, Packing, Doing, Ended, Error
    }
    /**
     * Тип данных для импорта или экспорта
     */
    lateinit var dataType: String
    /**
     * Тип политики импорта данных
     */
    lateinit var importPolicy: String
    protected val scope = CoroutineScope(Dispatchers.IO)
    protected val _status = MutableStateFlow(OperationStatus.Ready)
    val status = _status.asStateFlow()

    /**
     * Этот метод позволяет провести процедуру инициализации контролера.
     * Должен вызываться до использования других методов.
     */
    @Composable
    open fun initController(){ /*To be override if needed*/ }

    /**
     * Этот метод позволяет импортировать БД приложения
     */
    abstract fun importDB()

    /**
     * Этот метод позволяет экспортировать БД приложения
     */
    abstract fun exportDB()
}