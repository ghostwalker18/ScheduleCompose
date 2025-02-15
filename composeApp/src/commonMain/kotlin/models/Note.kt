package com.ghostwalker18.schedule.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
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
    @PrimaryKey(autoGenerate = true) val id : Int,
    @ColumnInfo(name = "noteDate") var date : Calendar,
    @ColumnInfo(name = "noteGroup") var group : String,
    @ColumnInfo(name = "noteTheme") var theme: String?,
    @ColumnInfo(name = "noteText") var text: String,
    @ColumnInfo(name = "notePhotoID") var photoIDs: String?
)