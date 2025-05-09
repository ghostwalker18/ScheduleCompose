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

package com.ghostwalker18.schedule

import com.ghostwalker18.schedule.utils.RowCache
import com.github.pjfanning.xlsx.StreamingReader
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import java.io.File

/**
 * Модульные тесты для класса RowCache
 *
 * @author Ипатов Никита
 */
@RunWith(Theories::class)
class RowCacheUnitTest {
    private val file = File(
        javaClass.getResource("/testScheduleFile.xlsx")!!.path
    )
    private var cache: RowCache? = null

    @Before
    fun reloadCache() {
        val excelFile = StreamingReader.builder()
            .rowCacheSize(10)
            .bufferSize(4096)
            .open(file)
        cache = RowCache.builder()
            .setSheet(excelFile.getSheetAt(0))
            .setSize(ROW_CACHE_SIZE)
            .build()
    }

    /**
     * Проверка выдачи нужного ряда из кэша.
     * @param rowNum номер требуемого ряда
     */
    @Theory
    fun gettingCorrectRowOneShot(
        @FromDataPoints("rowNumberSet") rowNum: Int
    ) {
        val row = cache!!.getRow(rowNum)
        Assert.assertEquals(row!!.rowNum.toLong(), rowNum.toLong())
    }

    /**
     * Проверка последовательной выдачи нужных рядов из кэша.
     * @param rowLimit предел выдачи рядов
     */
    @Theory
    fun gettingCorrectRowSerial(
        @FromDataPoints("rowNumberSet") rowLimit: Int
    ) {
        for (i in 0..< rowLimit) {
            val row = cache!!.getRow(i)
            Assert.assertEquals(row!!.rowNum.toLong(), i.toLong())
        }
    }

    /**
     * Проверка некорректного доступа к кэшу.
     */
    @Test(expected = IndexOutOfBoundsException::class)
    fun incorrectOrderRowAccess() {
        cache!!.getRow(32)
        cache!!.getRow(0)
    }

    /**
     * Проверка некорректных номеров рядов
     */
    @Test(expected = IndexOutOfBoundsException::class)
    fun incorrectRowNumberAccess() {
        cache!!.getRow(-1)
    }

    /**
     * Проверка слишком большого номера рядя.
     */
    @Test(expected = StackOverflowError::class)
    fun tooHugeRowNumber() {
        cache!!.getRow(1000000000)
    }

    /**
     * Проверка выдачи немного устаревшего ряда.
     */
    @Test
    fun someRotten(){
        cache!!.getRow(8)
        val row = cache!!.getRow(5)
        Assert.assertEquals(5, row!!.rowNum.toLong())
    }

    companion object {
        private const val ROW_CACHE_SIZE = 7

        @get:DataPoints("rowNumberSet")
        val rowNumber: IntArray = intArrayOf(0, 1, 10, 21, 37, 101)
    }
}