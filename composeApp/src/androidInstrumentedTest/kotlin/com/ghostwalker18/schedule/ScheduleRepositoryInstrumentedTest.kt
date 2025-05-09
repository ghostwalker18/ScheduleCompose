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

import android.content.Context
import androidx.preference.PreferenceManager
import androidx.room.InvalidationTracker
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.ghostwalker18.schedule.database.AppDatabase
import com.ghostwalker18.schedule.database.LessonDao
import com.ghostwalker18.schedule.database.NoteDao
import com.ghostwalker18.schedule.models.ScheduleRepositoryAndroid
import com.ghostwalker18.schedule.network.NetworkService
import com.russhwolf.settings.SharedPreferencesSettings
import org.junit.Assert
import org.junit.BeforeClass
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Модульные тесты для класса ScheduleRepositoryAndroid
 *
 * @author Ипатов Никита
 */
@RunWith(AndroidJUnit4::class)
class ScheduleRepositoryInstrumentedTest {

    /**
     * Проверка получения ссылок на расписание для первого корпуса.
     */
    @Test
    fun retrieveScheduleLinksForFirstCorpus() {
        val links = repository.linksForFirstCorpusSchedule
        Assert.assertFalse(links.isEmpty())
        for (link in links)
            Assert.assertTrue(link.endsWith(".xlsx"))
    }

    /**
     * Проверка получения ссылок на расписание для второго корпуса.
     */
    @Test
    fun retrieveScheduleLinksForSecondCorpus() {
        val links = repository.linksForSecondCorpusSchedule
        Assert.assertFalse(links.isEmpty())
        for (link in links)
            Assert.assertTrue(link.endsWith(".xlsx"))
    }

    /**
     * Проверка сохранения группы в репозитории.
     */
    @Test
    fun savingGroup() {
        val group = "A-31"
        repository.savedGroup = group
        Assert.assertEquals("A-31", repository.savedGroup)
    }

    companion object{
        lateinit var repository: ScheduleRepositoryAndroid

        /**
         * Инициализация репозитория.
         */
        @JvmStatic
        @BeforeClass
        fun initRepo() {
            val appContext: Context = InstrumentationRegistry.getInstrumentation().targetContext
            val preferences = SharedPreferencesSettings(
                PreferenceManager.getDefaultSharedPreferences(appContext)
            )
            val api = NetworkService(appContext, URLs.BASE_URI, preferences).getScheduleAPI()
            repository = ScheduleRepositoryAndroid(appContext, AppDatabaseMock(), api, preferences)
        }
    }

    private class AppDatabaseMock: AppDatabase(){
        override fun lessonDao(): LessonDao { TODO("Not yet implemented") }
        override fun noteDao(): NoteDao { TODO("Not yet implemented") }
        override fun createInvalidationTracker(): InvalidationTracker { TODO("Not yet implemented") }
        override fun clearAllTables() { TODO("Not yet implemented") }
    }
}