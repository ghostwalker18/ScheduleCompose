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
import com.ghostwalker18.schedule.getNotesRepository
import com.ghostwalker18.schedule.getScheduleRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.ghostwalker18.schedule.models.Note
import java.util.*

/**
 * Этот класс служит для отслеживания изменений состояния списка заметок, отображаемого пользователю.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class NotesModel : ViewModel() {
    private val repository = getNotesRepository()
    private val _notes = MutableStateFlow(emptyArray<Note>())
    private var _notesMediator: Flow<Array<Note>>? = null
    private val _startDate = MutableStateFlow(Calendar.getInstance())
    private val _endDate = MutableStateFlow(Calendar.getInstance())
    private val _keyword: MutableStateFlow<String?> = MutableStateFlow(null)
    val notes = _notes.asStateFlow()
    val startDate = _startDate.asStateFlow()
    val endDate = _endDate.asStateFlow()
    val keyword = _keyword.asStateFlow()
    val isFilterEnabled = MutableStateFlow(false)
    var group: String? = ""
        set(value) {
            field = value
            if (group != null) {
                if (_keyword.value != null) _notesMediator = repository.getNotes(group!!, _keyword.value!!)
                if (startDate.value != null && endDate.value != null)
                    _notesMediator = repository
                        .getNotes(group!!, generateDateSequence(startDate.value!!, endDate.value!!))
            }
            viewModelScope.launch {
                _notesMediator?.collect{
                    _notes.value = it
                }
            }
        }

    init {
        group = getScheduleRepository().savedGroup
    }

    /**
     * Этот метод устанавливает ключевое слово для поиска заметок.
     * @param keyword ключевое слово
     */
    fun setKeyword(keyword: String?){
        _keyword.value = keyword
        if (keyword != null)
            _notesMediator = repository.getNotes(group!!, keyword)
        else {
            if (startDate.value != null && endDate.value != null && group != null) _notesMediator =
                repository.getNotes(
                    group!!,
                    generateDateSequence(startDate.value!!, endDate.value!!)
                )
        }
        viewModelScope.launch {
            _notesMediator?.collect{
                _notes.value = it
            }
        }
    }

    /**
     * Этот метод устанавливает начальную дату временного интервала выдачи заметок.
     * @param date начальная дата
     */
    fun setStartDate(date: Calendar?) {
        _startDate.value = date
        if (startDate.value != null && endDate.value != null && group != null)
            _notesMediator = repository
                .getNotes(group!!, generateDateSequence(startDate.value!!, endDate.value!!))
        viewModelScope.launch {
            _notesMediator?.collect{
                _notes.value = it
            }
        }
    }

    /**
     * Этот метод устанавливает конечную дату временного интервала выдачи заметок.
     * @param date конечная дата
     */
    fun setEndDate(date: Calendar?) {
        _endDate.value = date
        if (startDate.value != null && endDate.value != null && group != null)
            _notesMediator = repository
                .getNotes(group!!, generateDateSequence(startDate.value!!, endDate.value!!))
        viewModelScope.launch {
            _notesMediator?.collect{
                _notes.value = it
            }
        }
    }

    fun deleteNotes(notes: Collection<Note>){
        viewModelScope.launch {
            repository.deleteNotes(notes)
        }
    }

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