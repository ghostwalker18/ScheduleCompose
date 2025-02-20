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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ghostwalker18.scheduledesktop2.models.ScheduleRepositoryDesktop
import com.ghostwalker18.scheduledesktop2.network.NetworkService
import com.ghostwalker18.scheduledesktop2.views.EditNoteActivity
import com.ghostwalker18.scheduledesktop2.views.MainActivity
import com.ghostwalker18.scheduledesktop2.views.NotesActivity
import com.ghostwalker18.scheduledesktop2.views.SettingsActivity
import converters.DateConverters
import database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import models.NotesRepository
import models.ScheduleRepository
import java.util.*
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

/**
 * <h1>Schedule Desktop</h1>
 * <p>
 *      Программа представляет собой десктопную реализацию приложения расписания ПАСТ.
 * </p>
 *
 * @author  Ипатов Никита
 * @version  1.0
 */
class ScheduleApp : PreferenceChangeListener{
    val scheduleRepository: ScheduleRepository
    val notesRepository: NotesRepository
    val preferences: Preferences = Preferences.userNodeForPackage(ScheduleApp::class.java)
    private val db: AppDatabase
    private val themeState: MutableStateFlow<String> = MutableStateFlow(preferences["theme", "system"])
    lateinit var navigator: NavigatorDesktop

    init {
        instance = this
        db = AppDatabase.getInstance()
        preferences.addPreferenceChangeListener(this)
        scheduleRepository = ScheduleRepositoryDesktop(
            db,
            NetworkService(URLs.BASE_URI).getScheduleAPI(),
            preferences
        )
        notesRepository = NotesRepository(db)
        scheduleRepository.update()
        setupLocale()
    }

    @Composable
    @Preview
    fun App() {
        val theme by themeState.collectAsState()
        val navController = rememberNavController()
        navigator = NavigatorDesktop(navController)
        ScheduleTheme(
            when(theme){
                "day" -> false
                "night" -> true
                "system" -> isSystemInDarkTheme()
                else -> true
            }
        ){
            NavHost(
                navController = navController,
                startDestination = "main"
            ){
                composable(route = "main"){
                    MainActivity()
                }
                composable(route = "settings"){
                    SettingsActivity()
                }
                composable(
                    route = "notes/{group}/{date}",
                    arguments = listOf(
                        navArgument("group"){ type = NavType.StringType },
                        navArgument("date"){ type = NavType.StringType })
                ){
                    stackEntry ->
                    val group = stackEntry.arguments?.getString("group")
                    val date = DateConverters().fromString(
                        stackEntry.arguments?.getString("date")
                    )
                    NotesActivity(group, date)
                }
                composable(
                    route = "editNote/{group}/{date}/{noteID}",
                    arguments = listOf(
                        navArgument("group"){ type = NavType.StringType },
                        navArgument("date"){ type = NavType.StringType },
                        navArgument("noteID"){ type = NavType.IntType },
                    )
                ){
                    stackEntry ->
                    val group = stackEntry.arguments?.getString("group")
                    val date = DateConverters().fromString(
                        stackEntry.arguments?.getString("date")
                    )
                    val noteID = stackEntry.arguments?.getInt("noteID")
                    EditNoteActivity(noteID, group, date)
                }
            }

        }
    }

    private fun setupLocale(){
        val locale = Locale(ScheduleApp.preferences["language", "ru"])
        Locale.setDefault(locale)
    }

    companion object{
        private lateinit var instance: ScheduleApp
        val preferences: Preferences = Preferences.userNodeForPackage(ScheduleApp::class.java)

        fun getInstance(): ScheduleApp{
            return instance
        }
    }

    override fun preferenceChange(evt: PreferenceChangeEvent?) {
        when(evt?.key){
            "language" -> setupLocale()
            "theme" -> themeState.value = preferences["theme", "system"]
        }
    }
}