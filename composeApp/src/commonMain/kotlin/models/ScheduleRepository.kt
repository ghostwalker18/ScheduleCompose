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

package models

import androidx.compose.ui.graphics.painter.Painter
import converters.IConverter
import converters.XMLStoLessonsConverter
import database.AppDatabase
import kotlinx.coroutines.flow.*
import network.ScheduleNetworkAPI
import org.apache.poi.ss.usermodel.Workbook
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.IOException
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.prefs.Preferences
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
                                  private val preferences: Preferences) {
    private val mainSelector = "h2:contains(Расписание занятий и объявления:) + div > table > tbody"
    protected val mondayTimesPath = "mondayTimes.jpg"
    protected val otherTimesPath = "otherTimes.jpg"
    private var updateFutures: MutableList<CompletableFuture<UpdateResult>> = mutableListOf()
    private val updateExecutorService: ExecutorService = Executors.newFixedThreadPool(4)
    private val converter: IConverter = XMLStoLessonsConverter()
    private var allJobsDone = true
    protected val _mondayTimes = MutableStateFlow<Painter?>(null)
    protected val _otherTimes = MutableStateFlow<Painter?>(null)
    protected  val _status = MutableStateFlow(Status("", 0))

    var updateResult: CompletableFuture<UpdateResult>? = null

    val status = _status.asStateFlow()

    val teachers: Flow<Array<String>>
        get() = db.lessonDao().getTeachers()

    val groups: Flow<Array<String>>
        get() = db.lessonDao().getGroups()

    var savedGroup: String?
        get() = preferences.get("savedGroup", null)
        set (value) = preferences.put("savedGroup", value)

    val mondayTimes: StateFlow<Painter?>
        get() = _mondayTimes.asStateFlow()

    val otherTimes: StateFlow<Painter?>
        get() = _otherTimes.asStateFlow()

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
            } catch (e: IOException) {
                return links
            }
        }

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
            } catch (e: IOException) {
                return links
            }
        }

    enum class UpdateResult {
        SUCCESS, FAIL;

        companion object{
            fun fromInt(i: Int): UpdateResult  = if (i == 0) SUCCESS else FAIL

            fun toInt(result: UpdateResult): Int = if (result == SUCCESS) 0 else 1
        }
    }

    data class Status(val status: String, val progress: Int)

    fun getSubjects(group: String?): Flow<Array<String>> {
        return db.lessonDao().getSubjectsForGroup(group)
    }

    fun getLessons(date: Calendar, teacher: String?, group: String?): Flow<Array<Lesson>> {
        return if (teacher != null && group != null) db.lessonDao().getLessonsForGroupWithTeacher(date, group, teacher)
        else if (teacher != null) db.lessonDao().getLessonsForTeacher(date, teacher)
        else if (group != null) db.lessonDao().getLessonsForGroup(date, group)
        else flowOf(emptyArray<Lesson>())
    }

    suspend fun getLastKnownLessonDate(group: String?): Calendar? {
        return db.lessonDao().getLastKnownLessonDate(group)
    }

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
                            preferences.put(
                                "previous_update_result",
                                UpdateResult.toInt(UpdateResult.FAIL).toString()
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
     * Этот метод используется для обновления БД приложения занятиями
     * @param linksGetter метод для получения ссылок на файлы расписания
     * @param parser парсер файлов расписания
     */
    protected abstract fun updateSchedule(
        linksGetter: Callable<List<String>>,
        parser: KFunction1<Workbook, List<Lesson>>
    ): UpdateResult

    protected abstract fun updateTimes(): UpdateResult
}