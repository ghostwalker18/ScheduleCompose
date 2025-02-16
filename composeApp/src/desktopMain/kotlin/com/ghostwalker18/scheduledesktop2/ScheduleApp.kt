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

package com.ghostwalker18.scheduledesktop2

import ScheduleTheme
import URLs
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import com.ghostwalker18.scheduledesktop2.models.ScheduleRepositoryDesktop
import com.ghostwalker18.scheduledesktop2.network.NetworkService
import com.ghostwalker18.scheduledesktop2.views.MainActivity
import database.AppDatabase
import models.ScheduleRepository
import java.util.prefs.Preferences

class ScheduleApp{
    private val db: AppDatabase
    private val scheduleRepository: ScheduleRepository
    private val preferences: Preferences = Preferences.userNodeForPackage(ScheduleApp::class.java)

    init {
        instance = this
        db = AppDatabase.getInstance()
        scheduleRepository = ScheduleRepositoryDesktop(
            NetworkService(URLs.BASE_URI).getScheduleAPI(),
            preferences)
        scheduleRepository.update()
    }



    fun getScheduleRepository(): ScheduleRepository{
        return scheduleRepository
    }

    fun getDatabase(): AppDatabase{
        return db
    }

    @Composable
    @Preview
    fun App() {
        ScheduleTheme(true){
            MainActivity()
        }
    }

    companion object{
        private lateinit var instance: ScheduleApp
        val preferences: Preferences = Preferences.userNodeForPackage(ScheduleApp::class.java)

        fun getInstance(): ScheduleApp{
            return instance
        }
    }
}