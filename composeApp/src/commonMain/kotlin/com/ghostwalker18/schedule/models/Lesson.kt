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
import com.ghostwalker18.schedule.converters.DateConverters
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.number
import scheduledesktop2.composeapp.generated.resources.subject
import scheduledesktop2.composeapp.generated.resources.teacher
import java.util.*

/**
 * Этот класс используется для описания единичной сущности расписания - урока.
 * Используется в ORM.
 * Содержит поля для даты, порядкового номера, номера(названия) кабинета,
 * времени проведения, группы, преподавателя, предмета.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Entity(
    tableName = "tblSchedule",
    primaryKeys = ["lessonDate", "lessonNumber", "groupName", "subjectName"],
)
data class Lesson(
    /**
     * Дата занятия
     */
    @ColumnInfo(name = "lessonDate", index = true) val date : Calendar,
    /**
     * Номер пары
     */
    @ColumnInfo(name = "lessonNumber") var number : String,
    /**
     * Номер/название кабинета
     */
    @ColumnInfo(name="roomNumber") val room : String?,
    /**
     * Время проведения занятия
     */
    @ColumnInfo(name = "lessonTimes") val times : String?,
    /**
     * Название группы
     */
    @ColumnInfo(name = "groupName", index = true) var group : String,
    /**
     * Название предмета
     */
    @ColumnInfo(name = "subjectName") val subject : String,
    /**
     * Имя преподавателя
     */
    @ColumnInfo(name = "teacherName") var teacher: String?
){
     override fun toString(): String {
         return runBlocking {
             var res = ""
             res = res + getString(Res.string.date) + ": " + DateConverters().toString(date) + "\n"
             res = res + getString(Res.string.number) + ": " + number + "\n"
             res = res + getString(Res.string.subject) + ": " + subject + "\n"
             if (teacher != "") res = res + getString(Res.string.teacher) + ": " + teacher + "\n"
             if (room != "") res = res + getString(Res.string.room) + ": " + room + "\n"
             return@runBlocking res
         }
    }
}