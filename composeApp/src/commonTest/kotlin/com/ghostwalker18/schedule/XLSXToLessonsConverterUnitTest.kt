package com.ghostwalker18.schedule

import com.ghostwalker18.schedule.converters.IConverter
import com.ghostwalker18.schedule.converters.XLSXStoLessonsConverter
import org.apache.commons.math3.util.Pair
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.experimental.theories.DataPoints
import org.junit.experimental.theories.FromDataPoints
import org.junit.experimental.theories.Theories
import org.junit.experimental.theories.Theory
import org.junit.runner.RunWith
import java.lang.reflect.Method

/**
 * Модульные классы для класса XMLSToLessonsConverter
 *
 * @author Ипатов Никита
 */
@RunWith(Theories::class)
class XLSXToLessonsConverterUnitTest {
    private val converter: IConverter = XLSXStoLessonsConverter()

    /**
     * Проверка обработки имени преподавателя, ввод не соответствует желаемому.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareTeacherTestIncorrectInput() {
        val actualResult = prepareTeacher!!.invoke(converter, " Иванов    И.И. ") as String?
        Assert.assertEquals("Иванов И.И.", actualResult)
    }

    /**
     * Проверка обработки имени преподавателя, ввод уже соответствует желаемому.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareTeacherTestCorrectInput() {
        val actualResult = prepareTeacher!!.invoke(converter, "Иванов И.И.") as String?
        Assert.assertEquals("Иванов И.И.", actualResult)
    }

    /**
     * Проверка обработки имени преподавателя, ввод некорректен.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareTeacherTestNullInput() {
        val input: String? = null
        val actualResult = prepareTeacher!!.invoke(converter, input) as String?
        Assert.assertNull(actualResult)
    }

    /**
     * Проверка обработки названия предмета, ввод не соответствует желаемому.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareSubjectTestIncorrectInput() {
        val actualResult = prepareSubject!!.invoke(converter, " 3D  \n моделирование ") as String?
        Assert.assertEquals("3D моделирование", actualResult)
    }

    /**
     * Проверка обработки названия предмета, ввод уже соответствует желаемому.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareSubjectTestCorrectInput() {
        val actualResult = prepareSubject!!.invoke(converter, "3D моделирование") as String?
        Assert.assertEquals("3D моделирование", actualResult)
    }

    /**
     * Проверка обработки названия предмета, ввод некорректен.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareSubjectTestNullInput() {
        val input: String? = null
        val actualResult = prepareSubject!!.invoke(converter, input) as String?
        Assert.assertEquals("", actualResult)
    }

    /**
     * Проверка обработки времени занятия, ввод не соответствует желаемому.
     * @throws Exception
     */
    @Theory
    @Throws(Exception::class)
    fun prepareTimesTestIncorrectInput(
        @FromDataPoints("prepareTimesIncorrectSet") pair: Pair<String?, String?>
    ) {
        val actualResult = prepareTimes!!.invoke(converter, pair.getFirst()) as String?
        Assert.assertEquals(pair.getSecond(), actualResult)
    }

    /**
     * Проверка обработк времени занятия, ввод уже соответствует желаемому.
     * @throws Exception
     */
    @Theory
    @Throws(Exception::class)
    fun prepareTimesTestCorrectInput(
        @FromDataPoints("prepareTimesCorrectSet") pair: Pair<String?, String?>
    ) {
        val actualResult = prepareTimes!!.invoke(converter, pair.getFirst()) as String?
        Assert.assertEquals(pair.getSecond(), actualResult)
    }

    /**
     * Проверка обработки времени занятия, ввод некорректен.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareTimesTestNullInput() {
        val input: String? = null
        val actualResult = prepareTimes!!.invoke(converter, input) as String?
        Assert.assertNull(actualResult)
    }

    /**
     * Проверка обработки номера кабинета, ввод не соответствует желаемому.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareRoomNumberTestIncorrectInput() {
        val actualResult = prepareRoomNumber!!.invoke(converter, " 32/ 45") as String?
        Assert.assertEquals("32/45", actualResult)
    }

    /**
     * Проверка обработки номера кабинета, ввод уже соответствует желаемому.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareRoomNumberTestCorrectInput() {
        val actualResult = prepareRoomNumber!!.invoke(converter, "32 45") as String?
        Assert.assertEquals("32 45", actualResult)
    }

    /**
     * Проверка обработки номера кабинета, ввод некорректен.
     * @throws Exception
     */
    @Test
    @Throws(Exception::class)
    fun prepareRoomNumberTestNullInput() {
        val input: String? = null
        val actualResult = prepareRoomNumber!!.invoke(converter, input) as String?
        Assert.assertNull(actualResult)
    }

    companion object {
        private var prepareTeacher: Method? = null
        private var prepareSubject: Method? = null
        private var prepareTimes: Method? = null
        private var prepareRoomNumber: Method? = null

        @JvmStatic
        @DataPoints("prepareTimesIncorrectSet")
        fun prepareTimesIncorrectSet(): Array<Pair<String?, String?>> {
            return arrayOf<Pair<String?, String?>>(
                Pair<String?, String?>("9:30- 10:30 ", "09:30-10:30"),
                Pair<String?, String?>("8:30 -10:30", "08:30-10:30"),
                Pair<String?, String?>(" 7:30 - 10:30", "07:30-10:30"),
            )
        }

        @JvmStatic
        @DataPoints("prepareTimesCorrectSet")
        fun prepareTimesCorrectSet(): Array<Pair<String?, String?>> {
            return arrayOf<Pair<String?, String?>>(
                Pair<String?, String?>("19:15-20:30", "19:15-20:30"),
                Pair<String?, String?>("20:00-20:30", "20:00-20:30"),
                Pair<String?, String?>("07:30-10:30", "07:30-10:30"),
            )
        }

        /**
         * Получение доступа к проверяемым приватным методам.
         * @throws Exception
         */
        @JvmStatic
        @Throws(Exception::class)
        @BeforeClass
        fun getMethods(){
            prepareTeacher = XLSXStoLessonsConverter::class.java
                .getDeclaredMethod("prepareTeacher", java.lang.String::class.java)
            prepareTeacher!!.setAccessible(true)
            prepareSubject = XLSXStoLessonsConverter::class.java
                .getDeclaredMethod("prepareSubject", java.lang.String::class.java)
            prepareSubject!!.setAccessible(true)
            prepareTimes = XLSXStoLessonsConverter::class.java
                .getDeclaredMethod("prepareTimes", java.lang.String::class.java)
            prepareTimes!!.setAccessible(true)
            prepareRoomNumber = XLSXStoLessonsConverter::class.java
                .getDeclaredMethod("prepareRoomNumber", java.lang.String::class.java)
            prepareRoomNumber!!.setAccessible(true)
        }
    }
}