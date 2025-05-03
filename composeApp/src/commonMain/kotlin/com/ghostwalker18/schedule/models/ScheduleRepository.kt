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

import androidx.compose.ui.graphics.painter.Painter
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.ghostwalker18.schedule.converters.IConverter
import com.ghostwalker18.schedule.converters.XLSXStoLessonsConverter
import com.ghostwalker18.schedule.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import com.ghostwalker18.schedule.network.ScheduleNetworkAPI
import kotlinx.coroutines.CoroutineScope
import org.apache.poi.ss.usermodel.Workbook
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.reflect.KFunction1

/**
 * Этот класс представляет собой репозиторий данных приложения о расписании.
 * @property db база данных приложения
 * @property api сетевое API приложения
 * @property preferences сохраненные настройки приложения
 * @author  Ипатов Никита
 * @since 1.0
 */
abstract class ScheduleRepository(protected open val db: AppDatabase,
                                  private val api: ScheduleNetworkAPI,
                                  private val preferences: Settings) {
    private val mainSelector = "h2:contains(Расписание занятий и объявления:) + div > table > tbody"
    private var updateFutures: MutableList<CompletableFuture<UpdateResult>> = mutableListOf()
    private val updateExecutorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val converter: IConverter = XLSXStoLessonsConverter()
    private var allJobsDone = true
    protected val _mondayTimes = MutableStateFlow<Painter?>(null)
    protected val _otherTimes = MutableStateFlow<Painter?>(null)
    protected  val _status = MutableStateFlow(Status("", 0))
    protected val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Это свойство показывает результат предпоследнего обновления репозитория.
     */
    val lastUpdateResult: UpdateResult = UpdateResult
        .fromInt(preferences["previous_update_result", UpdateResult.SUCCESS.toInt()])

    /**
     * Это свойство показывает результат последнего обновления репозитория.
     */
    var updateResult: CompletableFuture<UpdateResult>? = null
        protected set

    /**
     * Это свойство показывает статус обновления репозитория.
     */
    val status = _status.asStateFlow()

    /**
     * Это свойство представляет собой  список всех преподавателей техникума.
     */
    val teachers: Flow<Array<String>>
        get() = db.lessonDao().getTeachers().flowOn(Dispatchers.IO)

    /**
     * Это свойство предоставляет собой список всех групп в техникуме.
     */
    val groups: Flow<Array<String>>
        get() = db.lessonDao().getGroups().flowOn(Dispatchers.IO)

    /**
     * Это свойство представляет собой выбранную и сохраненную пользователем группу.
     */
    var savedGroup: String?
        get() = preferences["savedGroup"]
        set (value) = preferences.putString("savedGroup", value ?: "")

    /**
     *  Это свойство представляет собой изображение расписания звонков в техникуме на понедельник.
     */
    val mondayTimes: StateFlow<Painter?>
        get() = _mondayTimes.asStateFlow()

    /**
     * Это свойство представляет собой изображение расписания звонков в техникуме со вторника по пятницу.
     */
    val otherTimes: StateFlow<Painter?>
        get() = _otherTimes.asStateFlow()

    /**
     * Это свойство представляет собой список ссылок на расписание для второго корпуса техникума.
     */
    val linksForSecondCorpusSchedule: List<String>
        get() {
            val links: MutableList<String> = ArrayList()
            try {
                val doc: Document? = api.mainPage?.execute()?.body()
                val linkElements: Elements? = doc
                    ?.select(mainSelector)?.get(0)
                    ?.select("tr")?.get(1)
                    ?.select("td")?.get(1)
                    ?.select("a")
                if (linkElements != null) {
                    for (linkElement in linkElements) {
                        if (linkElement.attr("href").endsWith(".xlsx"))
                            links.add(linkElement.attr("href"))
                    }
                }
                return links
            } catch (_: IOException) {
                return links
            }
        }

    /**
     * Это свойство представляет собой список ссылок на расписание для первого корпуса техникума.
     */
    val linksForFirstCorpusSchedule: List<String>
        get() {
            val links: MutableList<String> = ArrayList()
            try {
                val doc: Document? = api.mainPage?.execute()?.body()
                val linkElements: Elements? = doc
                    ?.select(mainSelector)?.get(0)
                    ?.select("tr")?.get(1)
                    ?.select("td")?.get(0)
                    ?.select("a")
                if (linkElements != null) {
                    for (linkElement in linkElements) {
                        if (linkElement.attr("href").endsWith(".xlsx"))
                            links.add(linkElement.attr("href"))
                    }
                }
                return links
            } catch (_: IOException) {
                return links
            }
        }

    /**
     * Это перечисление описывает результат обновления репозитория.
     */
    enum class UpdateResult {
        SUCCESS, FAIL;

        fun toInt(): Int = if (this == SUCCESS) 0 else 1

        companion object{
            fun fromInt(i: Int): UpdateResult = if (i == 0) SUCCESS else FAIL
        }
    }

    /**
     * Этот класс описывает статус обновления расписания.
     * @property status статус обновления
     * @property progress прогресс обновления в процентах
     */
    data class Status(val status: String, val progress: Int)

    /**
     * Этот метод позволяет получить список предметов у заданной группы.
     */
    fun getSubjects(group: String?): Flow<Array<String>> {
        return db.lessonDao().getSubjectsForGroup(group).flowOn(Dispatchers.IO)
    }

    /**
     * Этот метод позволяет получить список занятий на заданную дату для заданных группы и преподавателя.
     */
    fun getLessons(date: Calendar, teacher: String?, group: String?): Flow<Array<Lesson>> {
        return if (teacher != null && group != null)
            db.lessonDao().getLessonsForGroupWithTeacher(date, group, teacher).flowOn(Dispatchers.IO)
        else if (teacher != null) db.lessonDao().getLessonsForTeacher(date, teacher).flowOn(Dispatchers.IO)
        else if (group != null) db.lessonDao().getLessonsForGroup(date, group).flowOn(Dispatchers.IO)
        else flowOf(emptyArray<Lesson>()).flowOn(Dispatchers.IO)
    }

    /**
     * Этот метод возвращает последнюю дату для которой доступно расписание занятий для заданной группы.
     */
    suspend fun getLastKnownLessonDate(group: String?): Calendar? {
        return db.lessonDao().getLastKnownLessonDate(group)
    }

    /**
     * Этот метод позволяет обновить расписание занятий  и звоноков в репозитории
     * в соответствии с настройками приложения.
     */
    fun update() {
        val downloadFor = preferences["downloadFor", "all"]
        if (allJobsDone) {
            allJobsDone = false
            updateFutures.clear()
            if (downloadFor == "all" || downloadFor == "first")
                updateFutures.add(
                    CompletableFuture.supplyAsync({ this.updateFirstCorpus() }, updateExecutorService)
                )
            if (downloadFor == "all" || downloadFor == "second")
                updateFutures.add(
                    CompletableFuture.supplyAsync({ this.updateSecondCorpus() }, updateExecutorService)
                )
            updateFutures.add(
                CompletableFuture.supplyAsync({ this.updateTimes() }, updateExecutorService)
            )

            updateResult = CompletableFuture.allOf(*updateFutures.toTypedArray<CompletableFuture<*>>())
                .thenApplyAsync({
                    allJobsDone = true
                    for (future in updateFutures) {
                        if (future.getNow(UpdateResult.FAIL) == UpdateResult.FAIL) {
                            preferences.putInt(
                                "previous_update_result",
                                UpdateResult.FAIL.toInt()
                            )
                            return@thenApplyAsync UpdateResult.FAIL
                        }
                    }
                    UpdateResult.SUCCESS
                }, updateExecutorService)
        }
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями для первого корпуса
     */
    private fun updateFirstCorpus(): UpdateResult {
        return updateSchedule(
            Callable<List<String>> { return@Callable linksForFirstCorpusSchedule },
            converter::convertFirstCorpus
        )
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями для второго корпуса
     */
    private fun updateSecondCorpus(): UpdateResult {
        return updateSchedule(
            Callable<List<String>> { return@Callable linksForSecondCorpusSchedule },
            converter::convertSecondCorpus
        )
    }

    /**
     * Этот метод используется для обновления БД приложения занятиями.
     * @param linksGetter метод для получения ссылок на файлы расписания
     * @param parser парсер файлов расписания
     * @return результат обновления
     */
    protected abstract fun updateSchedule(
        linksGetter: Callable<List<String>>,
        parser: KFunction1<Workbook, List<Lesson>>
    ): UpdateResult

    /**
     * Этот метод используется для обновления расписания звонков.
     * @return результат обновления
     */
    protected abstract fun updateTimes(): UpdateResult

    companion object{
        const val MONDAY_TIMES_PATH = "mondayTimes.jpg"
        const val OTHER_TIMES_PATH = "otherTimes.jpg"
    }
}