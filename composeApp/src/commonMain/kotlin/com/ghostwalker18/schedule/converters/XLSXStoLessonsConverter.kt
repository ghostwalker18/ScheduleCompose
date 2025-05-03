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

package com.ghostwalker18.schedule.converters

import com.ghostwalker18.schedule.models.Lesson
import org.apache.poi.ss.usermodel.*
import com.ghostwalker18.schedule.utils.RowCache
import java.util.*

/**
 * Этот класс содержит в себе методы для работы с файлами расписания ПАСТ.
 *
 * @author  Ипатов Никита
 * @since 1.0
 * @see IConverter
 */
class XLSXStoLessonsConverter : IConverter {
    private var firstRowGap1 = 0

    override fun convertFirstCorpus(excelFile: Workbook): List<Lesson> {
        val lessons: MutableList<Lesson> = ArrayList()
        val dateConverters = DateConverters()

        for (i in 0..<excelFile.numberOfSheets) {
            val sheet: Sheet = excelFile.getSheetAt(i)
            val cache = RowCache.builder()
                .setSheet(sheet)
                .setSize(10)
                .build()

            val dateString: String = sheet.sheetName.trim()
            val date = dateConverters.convertFirstCorpusDate(dateString)
            date?: break

            val groups: NavigableMap<Int, String> = TreeMap()
            val groupsRow = cache!!.getRow(GROUPS_ROW_1) ?: break
            //checking if there is a schedule at the list
            //getting groups` names
            for (j in groupsRow.firstCellNum + 2..<groupsRow.lastCellNum) {
                val groupRowCell = groupsRow.getCell(j) ?: continue
                //if cells are united, only first cell in union is not null
                if ((groupRowCell.stringCellValue.trim { it <= ' ' } != "") && (groupRowCell.stringCellValue.trim { it <= ' ' } != "Группа") && (groupRowCell.stringCellValue.trim { it <= ' ' } != "День недели")
                ) {
                    val group = groupRowCell.stringCellValue.trim { it <= ' ' }
                    //mistake protection
                    groups[j] = prepareGroup(group)
                }
            }

            //searching for first row gap where schedule starts
            for (j in GROUPS_ROW_1 + 2..<sheet.lastRowNum) {
                if (!cache.getRow(j)!!.zeroHeight) {
                    firstRowGap1 = j
                    break
                }
            }

            //start filling schedule from top to bottom and from left to right
            val groupBounds = groups.navigableKeySet()
            var j: Int = sheet.firstRowNum + firstRowGap1
            while (j < firstRowGap1 + SCHEDULE_HEIGHT_1
            ) {
                for (k in groupBounds) {

                    val lessonNumber = getCellContentsAsString(cache, j, 1).trim()

                    val times = prepareTimes(getCellContentsAsString(cache, j + 1, 1))
                    var subject = getCellContentsAsString(cache, j, k) + " " +
                            getCellContentsAsString(cache, j + 1, k)
                    subject = (prepareSubject(subject))
                    val teacher = prepareTeacher(getCellContentsAsString(cache, j + 2, k))
                    val nextGroupBound = groupBounds.higher(k)
                    var roomNumber:String? = if (nextGroupBound != null) {
                        (getCellContentsAsString(cache, j, nextGroupBound - 1) + " "
                                + getCellContentsAsString(cache, j + 1, nextGroupBound - 1) + " "
                                + getCellContentsAsString(cache, j + 2, nextGroupBound - 1))
                    } else {
                        (getCellContentsAsString(cache, j, k + 3) + " "
                                + getCellContentsAsString(cache, j + 1, k + 3) + " "
                                + getCellContentsAsString(cache, j + 2, k + 3))
                    }
                    roomNumber = prepareRoomNumber(roomNumber)
                    val lesson = Lesson(date, lessonNumber, roomNumber, times, groups[k]!!, subject, teacher)
                    lessons.add(lesson)
                }
                j += SCHEDULE_CELL_HEIGHT_1
            }
        }

        return lessons
    }

    override fun convertSecondCorpus(excelFile: Workbook): List<Lesson> {
        val lessons: MutableList<Lesson> = ArrayList()
        val dateConverters = DateConverters()

        for (i in 0..<excelFile.numberOfSheets) {
            val sheet = excelFile.getSheetAt(i)
            val cache = RowCache.builder()
                .setSheet(sheet)
                .setSize(5)
                .build()

            val dateString = sheet.sheetName + "." + Calendar.getInstance()[Calendar.YEAR]
            val date = dateConverters.convertSecondCorpusDate(dateString)
            date ?: break

            val groups: NavigableMap<Int, String> = TreeMap()
            val groupsRow = cache!!.getRow(GROUPS_ROW_2) ?: break
            //checking if there is a schedule at the list
            //getting groups` names
            for (j in groupsRow.firstCellNum + 2..<groupsRow.lastCellNum) {
                val groupRowCell = groupsRow.getCell(j) ?: continue
                //if cells are united, only first cell in union is not null
                if (groupRowCell.stringCellValue.trim { it <= ' ' } != "") {
                    val group = groupRowCell.stringCellValue.trim { it <= ' ' }
                    //mistake protection
                    groups[j] = prepareGroup(group)
                }
            }

            //start filling schedule from top to bottom and from left to right
            val groupBounds = groups.navigableKeySet()
            var j = sheet.firstRowNum + FIRST_ROW_GAP_2
            scheduleFilling@
            while (j < sheet.lastRowNum) {
                for (k in groupBounds) {
                    //bottom of schedule are group names, breaking here
                    if (cache.getRow(j)!!.getCell(k).stringCellValue == groups[k])
                        break@scheduleFilling

                    val lessonNumber = getCellContentsAsString(cache, j, 1).trim()
                    val times = prepareTimes(getCellContentsAsString(cache, j + 1, 1))
                    val subject = prepareSubject(getCellContentsAsString(cache, j, k))
                    val teacher = prepareTeacher(getCellContentsAsString(cache, j + 1, k))
                    val nextGroupBound = groupBounds.higher(k)
                    var roomNumber: String?
                    if (nextGroupBound != null) {
                        roomNumber = getCellContentsAsString(cache, j, nextGroupBound - 1)
                    } else {
                        roomNumber = getCellContentsAsString(cache, j, k + 2)
                        if (roomNumber == "") roomNumber = getCellContentsAsString(cache, j, k + 3)
                    }
                    roomNumber = prepareRoomNumber(roomNumber)
                    groups[k]
                    val lesson = Lesson(date, lessonNumber, roomNumber, times, groups[k]!!, subject, teacher)
                    lessons.add(lesson)
                }
                j += SCHEDULE_CELL_HEIGHT_2
            }
        }

        return lessons
    }

    companion object{
        private const val GROUPS_ROW_1 = 3
        private const val SCHEDULE_HEIGHT_1 = 24
        private const val SCHEDULE_CELL_HEIGHT_1 = 4

        private const val FIRST_ROW_GAP_2 = 5
        private const val GROUPS_ROW_2 = 3
        private const val SCHEDULE_CELL_HEIGHT_2 = 2

        /**
         * Этот метод используется для получения содержимого ячейки в виде строки.
         *
         * @param cache лист эксель
         * @param row номер ряда ячейки
         * @param column номер столбца ячейки
         * @return содержимое ячейки в виде строки
         */
        private fun getCellContentsAsString(cache: RowCache, row: Int, column: Int): String {
            val cell = cache.getRow(row)?.getCell(column) ?: return ""
            return when (cell.cellType) {
                CellType.STRING -> cache.getRow(row)
                    ?.getCell(column)
                    ?.stringCellValue
                    ?: ""

                CellType.NUMERIC -> cache.getRow(row)
                    ?.getCell(column)
                    ?.numericCellValue?.toInt().toString()

                else -> ""
            }
        }

        /**
         * Этот метод используется для преобразования строки с группой к формату X-X.
         * @param group название группы
         * @return название группы
         */
        private fun prepareGroup(group: String): String {
            return group
                .replace("\\s+".toRegex(), "")
                .replace("([йцукенгшщзхъфывапролджэячсмитьбюЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮ])(\\d)".toRegex(),
                    "$1-$2")
        }

        /**
         * Этот метод используется для преобразования строки с временем занятия к формату ХХ.ХХ
         * @param times время
         * @return время
         */
        private fun prepareTimes(times: String?): String? {
            var times = times ?: return null
            times = times.trim { it <= ' ' }.replace("\\s+".toRegex(), "")
            if (times.startsWith("0")
                || times.startsWith("1")
                || times.startsWith("2")
                || times == "")
                return times
            return "0$times"
        }

        /**
         * Этот метод используется для приведения строки с именем преподавателя к удобочитаемому виду.
         * @param teacher имя преподавателя
         * @return обработанное имя преподавателя
         */
        private fun prepareTeacher(teacher: String?): String? {
            return teacher?.trim { it <= ' ' }
                ?.replace("\\s+".toRegex(), " ")
                ?.replace("/".toRegex(), "")
        }

        /**
         * Этот метод используется для приведения строки с номером кабинета к удобочитаемому виду.
         * @param roomNumber номер кабинета
         * @return обработанный номер кабинета
         */
        private fun prepareRoomNumber(roomNumber: String?): String? {
            return roomNumber?.trim { it <= ' ' }
                ?.replace("\\s*/\\s*".toRegex(), "/")
                ?.replace("\\s+".toRegex(), " ")
        }

        /**
         * Этот метод используется для приведения строки с названием предмета к удобочитаемому виду.
         * @param subject название предмета
         * @return обработанное название предмета
         */
        private fun prepareSubject(subject: String?): String {
            return subject?.trim { it <= ' ' }
                ?.replace("\\s+".toRegex(), " ")
                ?: ""
        }
    }
}