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
import com.ghostwalker18.schedule.grantURIPermission
import com.ghostwalker18.schedule.models.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

/**
 * Этот класс используется для отслеживания изменений состояния редактируемой заметки.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class EditNoteModel : ViewModel() {
    private val scheduleRepository = ScheduleApp.instance.scheduleRepository
    private val notesRepository = ScheduleApp.instance.notesRepository
    private var note: Note? = null
    private var noteThemesMediator: Flow<Array<String>>? = null
    private val _themes = MutableStateFlow(emptyArray<String>())
    private val _photoIDs = MutableStateFlow(listOf<String>())
    private val _group = MutableStateFlow(scheduleRepository.savedGroup)
    private var isEdited = false

    /**
     * Список возможных тем для заметки
     */
    val themes = _themes.asStateFlow()

    /**
     * Группа для заметки
     */
    val group = _group.asStateFlow()

    /**
     * Идентификаторы фотографий на устройстве для заметки
     */
    val photoIDs = _photoIDs.asStateFlow()

    /**
     * Дата заметки
     */
    val date = MutableStateFlow(Calendar.getInstance())

    /**
     * Тема заметки
     */
    val theme:MutableStateFlow<String?> = MutableStateFlow("")

    /**
     * Текст заметки
     */
    val text = MutableStateFlow("")

    init {
        viewModelScope.launch {
            scheduleRepository.getSubjects(scheduleRepository.savedGroup).collect{
                _themes.value = it
            }
        }
    }

    /**
     * Этот метод позволяет задать id заметки для редактирования.
     * @param id идентификатор
     */
    fun setNoteID(id: Int) {
        isEdited = true
        viewModelScope.launch {
            notesRepository.getNote(id).collect{
                it?.let {
                    note = it
                    _group.value = it.group
                    date.value = it.date
                    text.value = it.text
                    theme.value = it.theme
                }
            }
        }
    }

    /**
     * Этот метод позволяет задать группу для заметки.
     * @param group группа
     */
    fun setGroup(group: String?) {
        _group.value = group
        noteThemesMediator = scheduleRepository.getSubjects(group)
        viewModelScope.launch {
            noteThemesMediator?.collect{
                _themes.value = it
            }
        }
    }

    /**
     * Этот метод добавляет фотографию к заметке.
     * @param id идентификатор фотографии
     */
    fun addPhotoID(id: String) {
        val currentUris = _photoIDs.value.toMutableList()
        currentUris += id
        _photoIDs.value = currentUris
    }

    /**
     * Этот метод убирает фотографию из заметки.
     * @param id идентификатор фотографии
     */
    fun removePhotoID(id: String) {
        val currentUris = _photoIDs.value.toMutableList()
        currentUris -= id
        _photoIDs.value = currentUris
    }

    /**
     * Этот метод позволяет сохранить заметку.
     */
    fun saveNote() {
        viewModelScope.launch {
            var noteToSave = note
            if (noteToSave != null) {
                noteToSave.date = date.value!!
                noteToSave.group = _group.value!!
                noteToSave.theme = theme.value
                noteToSave.text = text.value
                noteToSave.photoIDs = photoIDs.value
            }
            else{
                noteToSave = Note(
                    date = date.value,
                    group = _group.value ?: "",
                    theme = theme.value,
                    text = text.value,
                    photoIDs = photoIDs.value
                )
            }
            grantURIPermission(photoIDs.value)
            if (isEdited)
                notesRepository.updateNote(noteToSave)
            else
                notesRepository.saveNote(noteToSave)
        }
    }
}