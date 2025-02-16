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

package viewmodels

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.util.*


/**
 * Этот класс используется для отслеживания изменения состояния расписания.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class ScheduleModel : ViewModel() {
    val group: MutableStateFlow<String?> = MutableStateFlow(null)
    val teacher: MutableStateFlow<String?> =MutableStateFlow(null)
    val calendar: MutableStateFlow<Calendar> = MutableStateFlow(Calendar.getInstance())

    /**
     * Этот метод позволяет передвинуть состояние расписания на следующую неделю.
     */
    fun goNextWeek() {
        calendar.update { date -> return date.add(Calendar.WEEK_OF_YEAR, 1) }
    }

    /**
     * Этот метод позволяет передвинуть состояние расписания на предыдущую неделю.
     */
    fun goPreviousWeek() {
        calendar.update { date -> return date.add(Calendar.WEEK_OF_YEAR, -1) }
    }

    fun getYear(): Int {
        return calendar.value[Calendar.YEAR]
    }

    fun getWeek(): Int {
        return calendar.value[Calendar.WEEK_OF_YEAR]
    }
}