package com.ghostwalker18.schedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
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
@Entity(tableName = "tblSchedule",
    primaryKeys = ["lessonDate", "lessonNumber", "groupName", "subjectName"])
data class Lesson(
    @ColumnInfo(name = "lessonDate") val date : Calendar,
    @ColumnInfo(name = "lessonNumber") var lessonNumber : String,
    @ColumnInfo(name="roomNumber") val roomNumber : String?,
    @ColumnInfo(name = "lessonTimes") val times : String?,
    @ColumnInfo(name = "groupName") var groupName : String,
    @ColumnInfo(name = "subjectName") val subject : String,
    @ColumnInfo(name = "teacherName") var teacher: String?
)