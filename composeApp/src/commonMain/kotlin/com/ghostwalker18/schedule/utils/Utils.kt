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

package com.ghostwalker18.schedule.utils

import java.io.*
import java.nio.file.Files
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * Вспомогательный объект содержащий утилитарные методы, используемые по всему приложени.
 * @author Ипатов Никита
 * @since 1.0
 */
object Utils {

    /**
     * Это перечисление показывает доступность занятия для посещения - прошло, идет, не началось
     */
    enum class LessonAvailability {
        ENDED, STARTED, NOT_STARTED
    }

    /**
     * Этот метод позволяет определить, доступно ли занятие для посещения на текущий момент времени.
     *
     * @param lessonTimes время проведения занятия
     * @param lessonDate дата занятия
     * @return доступность для посещения
     */
    @Synchronized
    fun isLessonAvailable(lessonDate: Calendar, lessonTimes: String?): LessonAvailability? {
        if (lessonTimes == null)
            return null
        try {
            val currentTime = Calendar.getInstance()
            val startTime = lessonTimes.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0]
            val endTime = lessonTimes.split("-".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]

            val start = lessonDate.clone() as Calendar
            start[Calendar.HOUR] =
                startTime.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
            start[Calendar.MINUTE] =
                startTime.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()

            val end = lessonDate.clone() as Calendar
            end[Calendar.HOUR] = endTime.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].toInt()
            end[Calendar.MINUTE] =
                endTime.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].toInt()

            return if (currentTime.before(start)) LessonAvailability.NOT_STARTED
            else if (currentTime.before(end)) LessonAvailability.STARTED
            else LessonAvailability.ENDED
        } catch (e: Exception) {
            return null
        }
    }

    /**
     * Этот метод используется для проверки, является ли заданная дата сегодняшним днем.
     * @param date дата для проверки
     * @return сегодня ли дата
     */
    @Synchronized
    fun isDateToday(date: Calendar): Boolean {
        val rightNow = Calendar.getInstance()
        return rightNow[Calendar.YEAR] == date[Calendar.YEAR] && rightNow[Calendar.MONTH] == date[Calendar.MONTH] && rightNow[Calendar.DAY_OF_MONTH] == date[Calendar.DAY_OF_MONTH]
    }

    /**
     * Этот метод используется для генерации даты для заголовка UI элемента.
     * @param date дата
     * @return представление даты в формате ХХ/ХХ
     */
    fun generateDateForTitle(date: Calendar): String {
        //Month is a number in 0 - 11
        val month = date[Calendar.MONTH] + 1
        //Formatting month number with leading zero
        var monthString = month.toString()
        if (month < 10) {
            monthString = "0$monthString"
        }
        val day = date[Calendar.DAY_OF_MONTH]
        var dayString = day.toString()
        //Formatting day number with leading zero
        if (day < 10) {
            dayString = "0$dayString"
        }
        return "$dayString/$monthString"
    }

    /**
     * Этот метод позволяет получить имя скачиваемого файла из ссылки на него.
     *
     * @param link ссылка на файл
     * @return имя файла
     */
    fun getNameFromLink(link: String): String {
        val parts = link.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        return parts[parts.size - 1]
    }

    /**
     * Этот метод выполняет замену недопустимых символов в имени файла на ~.
     */
    fun escapeIllegalCharacters(link: String): String{
        return link.replace(Regex(":"), "~")
    }

    /**
     * Этот метод создает ZIP-архив из указанных файлов.
     * @param sourceFiles файлы для архивации
     * @param archive файл архива
     */
    fun zip(sourceFiles: Array<File>, archive: File) {
        val bufferSize = 4096
        try {
            ZipOutputStream(
                BufferedOutputStream(
                    Files.newOutputStream(
                        archive.toPath()
                    )
                )
            ).use { out ->
                val data = ByteArray(bufferSize)
                for (sourceFile in sourceFiles) {
                    FileInputStream(sourceFile).use { fi ->
                        BufferedInputStream(fi, bufferSize).use { origin ->
                            val entry = ZipEntry(sourceFile.name)
                            out.putNextEntry(entry)

                            var count: Int
                            while ((origin.read(data, 0, bufferSize).also { count = it }) != -1) {
                                out.write(data, 0, count)
                            }
                        }
                    }
                }
            }
        } catch (ignores: Exception) { /**/
        }
    }

    /**
     * Этот метод распаковывает указанный ZIP-архив.
     * @param archive архив для распаковки
     * @param outputDirectory место для извлеченных файлов
     */
    @Throws(IOException::class)
    fun unzip(archive: File, outputDirectory: File) {
        if (!outputDirectory.exists()) outputDirectory.mkdirs()
        ZipInputStream(Files.newInputStream(archive.toPath())).use { zin ->
            var ze: ZipEntry?
            while ((zin.nextEntry.also { ze = it }) != null) {
                FileOutputStream(
                    File(outputDirectory, ze!!.name)
                ).use { fout ->
                    var c = zin.read()
                    while (c != -1) {
                        fout.write(c)
                        c = zin.read()
                    }
                    zin.closeEntry()
                }
            }
        }
    }
}