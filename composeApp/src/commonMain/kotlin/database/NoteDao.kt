package com.ghostwalker18.schedule.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import java.util.*

/**
 * Интерфейс DAO для работы с таблицой БД, содержащей сведения о заметках к занятиям.
 * Используется Room для генерации.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Dao
@TypeConverters(DateConverters::class)
interface NoteDao {
    /**
     * Этот метод позволяет получить заметку из базы данных по ее ID.
     * @param id идентификатор заметки
     * @return заметка
     */
    @Query("SELECT * FROM tblNote WHERE id = :id")
    fun getNote(id: Int): Flow<Note>

    /**
     * Этот метод позволяет получить заметки для заданной группы и дня.
     * @param date день
     * @param group группа
     * @return список заметок
     */
    @Query("SELECT * FROM tblNote WHERE noteDate = :date AND noteGroup = :group")
    fun getNotes(date: Calendar, group: String): Flow<Array<Note>>

    /**
     * Этот метод позволяет получить заметки для заданных группы и дней.
     * @param dates дни
     * @param group группа
     * @return список заметок
     */
    @Query("SELECT * FROM tblNote WHERE noteDate IN (:dates) AND noteGroup = :group " +
            "ORDER BY noteDate")
    fun getNotesForDays(dates: Array<Calendar>, group: String): Flow<Array<Note>>

    /**
     * Этот метод позволяет получить заметки, содержащие в теме или тексте заданное слова.
     * @param keyword ключевое слово
     * @return список заметок
     */
    @Query("SELECT * FROM tblNote WHERE (noteText LIKE '%' || :keyword || '%' OR " +
            "noteTheme LIKE '%' || :keyword || '%') AND noteGroup = :group " +
            "ORDER BY noteDate DESC")
    fun getNotesByKeyword(keyword: String?, group: String): Flow<Array<Note>>

    /**
     * Этот метод позволяет внести заметку в БД.
     * @param note заметка
     * @return
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(note: Note)
    /**
     * Этот метод позволяет обновить заметку из БД.
     * @param note заметка
     * @return
     */
    @Update
    suspend fun update(note: Note)

    /**
     * Этот метод позволяет удалить заметку из БД.
     * @param note заметка
     * @return
     */
    @Delete
    suspend fun delete(note: Note)
}