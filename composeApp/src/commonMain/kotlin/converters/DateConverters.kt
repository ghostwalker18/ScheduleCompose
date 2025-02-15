package com.ghostwalker18.schedule.database

import androidx.room.TypeConverter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Этот класс используется для ORM.
 * Содержит методы для преобразования Calendar в String для БД и наоборот
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class DateConverters {
    companion object {
        val dateFormatPhoto = SimpleDateFormat("dd_MM_yyyy",
            Locale("ru"))
        private val dateFormatDb = SimpleDateFormat("dd.MM.yyyy",
            Locale("ru"))
        private val dateFormatFirstCorpus = SimpleDateFormat("d MMMM yyyy",
            Locale("ru"))
        private val dateFormatSecondCorpus = SimpleDateFormat("dd.MM.yyyy",
            Locale("ru"))
        private val dateFormatThirdCorpus = SimpleDateFormat("",
            Locale("ru")
        )

        /**
         * Этот метод используется для преобразования строки в дату согласно заданному формату.
         * @param date строка даты
         * @param format формат даты
         * @return дата
         */
        @Synchronized
        private fun stringToCal(date: String?, format: SimpleDateFormat): Calendar? {
            return date?.let{
                try {
                    val cal = Calendar.getInstance()
                    cal.time = format.parse(it)!!
                    cal
                } catch (e: ParseException) {
                    null
                }
            }
        }
    }

    /**
     * Этот метод преобразует Calendar сущнисти в String для БД.
     * @param date  the entity attribute value to be converted
     * @return
     */
    @TypeConverter
    fun toString(date: Calendar?): String? {
        return date?.let { dateFormatDb.format(it.time)}
    }

    /**
     * Этот метод преобразует String из БД в Calendar сущности.
     * @param date  the data from the database column to be
     *                converted
     * @return
     */
    @TypeConverter
    fun fromString(value: String?): Calendar? {
        return stringToCal(value, dateFormatDb)
    }

    /**
     * Этот метод преобразует String из расписания первого корпуса на Первомайском
     * в Calendar сущности.
     * @param date дата из расписания первого корпуса
     * @return преобразованная дата в формате Calendar
     */
    fun convertFirstCorpusDate(date: String?): Calendar? {
        return stringToCal(date, dateFormatFirstCorpus)
    }

    /**
     * Этот метод преобразует String из расписания второго корпуса на Советской ул. в Calendar сущности.
     * @param date дата из расписания второго корпуса
     * @return преобразованная дата в формате Calendar
     */
    fun convertSecondCorpusDate(date: String?): Calendar? {
        return stringToCal(date, dateFormatSecondCorpus)
    }

    /**
     * Этот метод преобразует String из расписания третьего корпуса на Ленинградской ул. в Calendar сущности
     */
    fun convertThirdCorpus(date: String?): Calendar? {
        return stringToCal(date, dateFormatThirdCorpus)
    }
}