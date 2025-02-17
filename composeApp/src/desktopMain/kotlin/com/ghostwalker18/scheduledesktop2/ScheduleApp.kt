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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.ghostwalker18.scheduledesktop2.models.ScheduleRepositoryDesktop
import com.ghostwalker18.scheduledesktop2.network.NetworkService
import com.ghostwalker18.scheduledesktop2.views.EditNoteActivity
import com.ghostwalker18.scheduledesktop2.views.MainActivity
import com.ghostwalker18.scheduledesktop2.views.NotesActivity
import com.ghostwalker18.scheduledesktop2.views.SettingsActivity
import database.AppDatabase
import models.NotesRepository
import models.ScheduleRepository
import java.util.prefs.Preferences

class ScheduleApp{
    private val db: AppDatabase
    val scheduleRepository: ScheduleRepository
    val notesRepository: NotesRepository
    val preferences: Preferences = Preferences.userNodeForPackage(ScheduleApp::class.java)
    lateinit var navigator: NavigatorDesktop

    init {
        instance = this
        db = AppDatabase.getInstance()
        scheduleRepository = ScheduleRepositoryDesktop(
            db,
            NetworkService(URLs.BASE_URI).getScheduleAPI(),
            preferences
        )
        notesRepository = NotesRepository(db)
        scheduleRepository.update()
    }

    fun getDatabase(): AppDatabase{
        return db
    }

    @Composable
    @Preview
    fun App() {
        val navController = rememberNavController()
        navigator = NavigatorDesktop(navController)
        ScheduleTheme(true){
            NavHost(navController = navController, startDestination = "main"){
                composable(route = "main"){
                    MainActivity()
                }
                composable(route = "settings"){
                    SettingsActivity()
                }
                composable(route = "notes") {
                    NotesActivity()
                }
                composable(route = "editNote"){
                    EditNoteActivity()
                }
            }

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