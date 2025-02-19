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
import getScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import java.util.*


/**
 * Этот класс используется для отслеживания изменения состояния расписания.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class ScheduleModel : ViewModel() {
    val group = MutableStateFlow(getScheduleRepository().savedGroup)
    val teacher: MutableStateFlow<String?> = MutableStateFlow(null)
    val calendar = MutableStateFlow(Calendar.getInstance())

    init {
        group.onEach {
            getScheduleRepository().savedGroup = it
        }
    }

    /**
     * Этот метод позволяет передвинуть состояние расписания на следующую неделю.
     */
    fun goNextWeek(){
        val currentDate = calendar.value.clone() as Calendar
        currentDate.add(Calendar.WEEK_OF_YEAR, 1)
        calendar.value = currentDate
    }

    /**
     * Этот метод позволяет передвинуть состояние расписания на предыдущую неделю.
     */
    fun goPreviousWeek() {
        val currentDate = calendar.value.clone() as Calendar
        currentDate.add(Calendar.WEEK_OF_YEAR, -1)
        calendar.value = currentDate
    }

    fun getYear(): Int {
        return calendar.value[Calendar.YEAR]
    }

    fun getWeek(): Int {
        return calendar.value[Calendar.WEEK_OF_YEAR]
    }
}