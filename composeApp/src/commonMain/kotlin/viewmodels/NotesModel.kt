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
import getNotesRepository
import java.util.*

/**
 * Этот класс служит для отслеживания изменений состояния списка заметок, отображаемого пользователю.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class NotesModel() : ViewModel() {
    val repository = getNotesRepository()

    /**
     * Этот метод позволяет получить последовательность дат, основываясь на начальной и конечной.
     * @param startDate начальная дата (включается в интервал)
     * @param endDate конечная дата (включается в интервал)
     * @return массив дат
     */
    private fun generateDateSequence(startDate: Calendar, endDate: Calendar): Array<Calendar> {
        if (startDate == endDate || endDate.before(startDate))
            return arrayOf(startDate)
        val resultList: MutableList<Calendar> = ArrayList()
        //remember of reference nature of Java
        val counter = startDate.clone() as Calendar
        while (counter.before(endDate)) {
            //remember of reference nature of Java (also here)
            val date = counter.clone() as Calendar
            resultList.add(date)
            counter.add(Calendar.DATE, 1)
        }
        return resultList.toTypedArray()
    }
}