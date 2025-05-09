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

import com.ghostwalker18.schedule.utils.Utils
import com.ghostwalker18.schedule.utils.Utils.LessonAvailability
import org.junit.Assert
import org.junit.Test
import java.util.Calendar
import java.util.concurrent.TimeUnit

/**
 * Модульные тесты для объекта Utils
 *
 * @author Ипатов Никита
 */
class UtilsUnitTest {

    /**
     * Тест метода isDateToday с сегодняшним днем и датой в прошлом.
     */
    @Test
    fun isDateTodayTest(){
        val now = Calendar.getInstance()
        Assert.assertTrue(Utils.isDateToday(now))
        now.set(Calendar.YEAR, 2007)
        Assert.assertFalse(Utils.isDateToday(now))
    }

    /**
     * Тест метода isLessonAvailable для всех вариантов доступности посещения.
     */
    @Test
    fun isLessonAvailableTest(){
        val lessonDate = Calendar.getInstance()
        lessonDate.set(2025, 4, 5)
        val lessonTimes = "10.30-11.40"

        val dateInPast = Calendar.getInstance()
        dateInPast.set(2007, 4, 5)

        Assert.assertEquals(
            LessonAvailability.NOT_STARTED,
            Utils.isLessonAvailable(lessonDate, lessonTimes, dateInPast)
        )

        val dateInFuture = Calendar.getInstance()
        dateInFuture.set(2077, 4, 1)

        Assert.assertEquals(
            LessonAvailability.ENDED,
            Utils.isLessonAvailable(lessonDate, lessonTimes, dateInFuture)
        )

        val dateJustInTime = Calendar.getInstance()
        dateJustInTime.set(2025, 4, 5)
        dateJustInTime.set(Calendar.HOUR_OF_DAY, 11)
        dateJustInTime.set(Calendar.MINUTE, 5)

        Assert.assertEquals(
            LessonAvailability.STARTED,
            Utils.isLessonAvailable(lessonDate, lessonTimes, dateJustInTime)
        )
    }

    /**
     * Тест метода calculateTimeDistance для минут.
     */
    @Test
    fun calculateTimeDistanceTest(){
        val startDate = Calendar.getInstance()
        val endDate = startDate.clone() as Calendar
        endDate.add(Calendar.DAY_OF_YEAR, 1)
        endDate.add(Calendar.HOUR, 3)
        endDate.add(Calendar.MINUTE, 13)
        Assert.assertEquals(
            1633L,
            Utils.calculateTimeDistance(startDate, endDate, TimeUnit.MINUTES)
        )
    }
}