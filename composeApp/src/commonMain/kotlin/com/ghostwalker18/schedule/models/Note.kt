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

package com.ghostwalker18.schedule.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ghostwalker18.schedule.converters.DateConverters
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.*
import java.util.*


/**
 * Этот класс используется для описания единичной сущности заметок.
 * Используется в ORM.
 * Содержит поля для даты, группы, темы, текста, идентификатора фото.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Entity(tableName = "tblNote")
data class Note(
    /**
     * Идентификатор заметки
     */
    @PrimaryKey(autoGenerate = true) val id : Int = 0,
    /**
     * Дата заметки
     */
    @ColumnInfo(name = "noteDate") var date : Calendar,
    /**
     * Группа заметки
     */
    @ColumnInfo(name = "noteGroup") var group : String,
    /**
     * Тема заметки
     */
    @ColumnInfo(name = "noteTheme") var theme: String?,
    /**
     * Текст заметки
     */
    @ColumnInfo(name = "noteText") var text: String,
    /**
     * Идентификаторы фотографий, приложенных к заметке
     */
    @ColumnInfo(name = "notePhotoIDs") var photoIDs: List<String>? = emptyList()
){
    override fun toString(): String {
        return runBlocking {
            var res = ""
            res = res + getString(Res.string.date) + ": " + DateConverters().toString(date) + "\n"
            res = res + getString(Res.string.group) + ": " + group + "\n"
            if (theme != null) res = res + getString(Res.string.theme) + ": " + theme + "\n"
            if (text != "") res = res + getString(Res.string.text) + ": " + text + "\n"
            return@runBlocking res
        }
    }

    companion object{

        /**
         * Этот метод возвращает копию заметки со своим ID и без приложенных фотографий.
         * @param note заметка
         * @return копия
         */
        fun copy(note: Note): Note {
            val copiedNote = Note(
                date = note.date,
                group = note.group,
                theme = note.theme,
                text = note.text
                )
            return copiedNote
        }
    }
}