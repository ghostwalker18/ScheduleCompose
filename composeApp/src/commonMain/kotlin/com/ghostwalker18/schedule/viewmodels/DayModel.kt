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

package com.ghostwalker18.schedule.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ghostwalker18.schedule.ScheduleApp
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ghostwalker18.schedule.models.Lesson
import java.util.*

/**
 * Этот класс представляет собой модель представления фрагмента расписания для определенного дня недели.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class DayModel : ViewModel() {
    private val repository = ScheduleApp.instance.scheduleRepository
    var isOpened  = MutableStateFlow(false)
    private val _date = MutableStateFlow(Calendar.getInstance())
    private val _lessons = MutableStateFlow(emptyArray<Lesson>())
    private var _lessonsMediator: Flow<Array<Lesson>>? = null
    val lessons = _lessons.asStateFlow()
    var group: String? = null
        set(value) {
            field = value
            revalidateLessons()
        }
    var teacher: String? = null
        set(value) {
            field = value
            revalidateLessons()
        }

    fun setDate(date: Calendar) {
        this._date.value = date
        revalidateLessons()
    }

    fun getDate(): StateFlow<Calendar> {
        return _date.asStateFlow()
    }

    private fun revalidateLessons(){
        _lessonsMediator = repository.getLessons(_date.value, teacher, group)
        viewModelScope.launch {
            _lessonsMediator?.collect{
                _lessons.value = it
            }
        }
    }
}