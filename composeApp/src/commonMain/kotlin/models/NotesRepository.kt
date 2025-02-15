package com.ghostwalker18.scheduledesktop2.models

import database.AppDatabase
import database.NoteDao
import kotlinx.coroutines.flow.Flow
import models.Note
import java.util.*


/**
 * Этот класс представляет репозиторий данных приложения о заметках.
 *
 * @author Ипатов Никита
 * @since 3.1
 * @see NoteDao
 */
class NotesRepository(private val db: AppDatabase) {
    /**
     * Этот метод позволяет сохранить заметку.
     *
     * @param note заметка
     */
    fun saveNote(note: Note) {
        db.noteDao().insert(note)
    }

    /**
     * Этот метод позволяет обновить заметку.
     *
     * @param note заметка
     */
    fun updateNote(note: Note) {
        db.noteDao().update(note)
    }

    /**
     * Этот метод позволяет получить заметку по ее ID.
     *
     * @param id первичный ключ
     * @return заметка
     */
    fun getNote(id: Int): Flow<Note> {
        return db.noteDao().getNote(id)
    }

    /**
     * Этот метод позволяет получить заметки для заданных группы и временного промежутка.
     *
     * @param group группа
     * @param dates список дат для выдачи
     * @return заметки
     */
    fun getNotes(group: String, dates: Array<Calendar?>): Flow<Array<Note>> {
        if (dates.size == 1) return db.noteDao().getNotes(dates[0]!!, group)
        return db.noteDao().getNotesForDays(Flow, group)
    }

    /**
     * Этот метод позволяет получить заметки для заданного ключевого слова и группы.
     *
     * @param group группа
     * @param keyword ключевое слово
     * @return список заметок
     */
    fun getNotes(group: String, keyword: String): Flow<Array<Note>> {
        return db.noteDao().getNotesByKeyword(keyword, group)
    }

    /**
     * Этот метод позволяет удалить выбранные заметки из БД.
     *
     * @param notes заметки для удаления
     */
    fun deleteNotes(notes: Collection<Note?>) {
        for (note in notes) db.noteDao().delete(note)
    }
}