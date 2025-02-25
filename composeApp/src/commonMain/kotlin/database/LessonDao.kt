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

package database

import androidx.room.*
import converters.DateConverters
import kotlinx.coroutines.flow.Flow
import models.Lesson
import java.util.*

/**
 * Интерфейс DAO для работы с таблицой БД, содержащей сведения о занятиях.
 * Используется Room для генерации.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Dao
@TypeConverters(DateConverters::class)
interface LessonDao {

    /**
     * Этот метод позволяет получить список учителей из БД.
     * @return списко учителей
     */
    @Query("SELECT DISTINCT teacherName FROM tblSchedule ORDER BY teacherName ASC")
    fun getTeachers(): Flow<Array<String>>

    /**
     * Этот метод позволяет получить списко групп из БД.
     * @return список групп
     */
    @Query("SELECT DISTINCT groupName FROM tblSchedule ORDER BY groupName ASC")
    fun getGroups(): Flow<Array<String>>

    /**
     * Этот метод позволяет позволяет получить список занятий на заданную дату у заданной группы,
     * которые проводит заданный преподаватель.
     * @param date дата
     * @param group группа
     * @param teacher преподаватель
     * @return список занятий
     */
    @Query("SELECT * FROM tblSchedule " +
            "WHERE lessonDate = :date AND groupName= :group AND teacherName LIKE '%' || :teacher || '%' " +
            "ORDER BY lessonTimes")
    fun getLessonsForGroupWithTeacher(
        date: Calendar,
        group: String,
        teacher: String
    ): Flow<Array<Lesson>>

    /**
     * Этот метод позволяет получить список занятий на заданный день у заданной группы.
     * @param date дата
     * @param group группа
     * @return список занятий
     */
    @Query("SELECT * FROM tblSchedule WHERE lessonDate = :date AND groupName= :group " +
            "ORDER BY lessonTimes")
    fun getLessonsForGroup(date: Calendar, group: String): Flow<Array<Lesson>>

    /**
     * Этот метод позволяет получить список занятий на заданный день у заданного преподавателя.
     * @param date дата
     * @param teacher преподаватель
     * @return список занятий
     */
    @Query("SELECT * FROM tblSchedule " +
            "WHERE lessonDate = :date AND teacherName LIKE '%' || :teacher || '%' " +
            "ORDER BY lessonTimes")
    fun getLessonsForTeacher(date: Calendar?, teacher: String?): Flow<Array<Lesson>>

    /**
     * Этот метод позволяет получить список всех предметов у группы.
     * @param group название группы
     * @return список предметов
     */
    @Query("SELECT DISTINCT subjectName FROM tblSchedule WHERE groupName = :group " +
            "ORDER BY subjectName ASC")
    fun getSubjectsForGroup(group: String?): Flow<Array<String>>

    /**
     * Этот метод позволяет получить последнюю дату,
     * для которой для заданной группы указано расписание.
     * @param group группа
     * @return последняя дата, для которой существует расписание
     */
    @Query("SELECT MAX(lessonDate) FROM tblSchedule WHERE groupName =:group")
    suspend fun getLastKnownLessonDate(group: String?): Calendar?

    /**
     * Этот метод позволяет вставить элемент Lesson в БД.
     * @param lesson занятия
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(lesson: Lesson)

    /**
     * Этот метод позволяет вставить элементы Lesson в БД.
     * @param lessons занятия
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(vararg lessons: Lesson)

    /**
     * Этот метод позволяет вставить элементы Lesson в БД.
     * @param lessons занятия
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(lessons: List<Lesson>)

    /**
     * Этот метод позволяет обновить элемент Lesson В БД.
     * @param lesson занятие
     */
    @Update
    suspend fun update(lesson: Lesson)
}