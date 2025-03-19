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

import androidx.compose.animation.ExperimentalSharedTransitionApi
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
import com.ghostwalker18.schedule.models.ScheduleRepositoryDesktop
import com.ghostwalker18.schedule.network.NetworkService
import com.ghostwalker18.schedule.platform.*
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.get
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import com.ghostwalker18.schedule.models.NotesRepository
import com.ghostwalker18.schedule.views.*
import com.ghostwalker18.schedule.models.ScheduleRepository
import com.ghostwalker18.schedule.ui.theme.ScheduleTheme
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.*
import java.util.*
import java.util.prefs.Preferences
import javax.swing.UIManager

/**
 * <h1>Schedule Desktop</h1>
 * <p>
 *      Программа представляет собой десктопную реализацию приложения расписания ПАСТ.
 * </p>
 *
 * @author  Ипатов Никита
 * @version  1.0
 */
class ScheduleApp {
    val scheduleRepository: ScheduleRepository
    val notesRepository: NotesRepository
    val mainActivityController: MainScreenControllerDesktop
    val notesActivityController: NotesScreenControllerDesktop
    val shareActivityController: ShareScreenConrollerDesktop
    val settingsActivityController: SettingsScreenControllerDesktop
    val importScreenController: ImportScreenControllerDesktop
    val preferences: ObservableSettings = PreferencesSettings(Preferences.userNodeForPackage(ScheduleApp::class.java))
    private val db: AppDatabase
    private val themeState = MutableStateFlow(preferences["theme", "system"])
    lateinit var navigator: NavigatorDesktop

    val themeChangedListener = preferences.addStringListener("theme", "system"){
        themeState.value = it
    }

    val localeChangedListener = preferences.addStringListener("language", "ru"){
        setupLocale(it)
    }

    init {
        instance = this
        db = AppDatabase.getInstance()
        scheduleRepository = ScheduleRepositoryDesktop(
            db,
            NetworkService(URLs.BASE_URI).getScheduleAPI(),
            preferences
        )
        notesRepository = NotesRepository(db)
        mainActivityController = MainScreenControllerDesktop()
        notesActivityController = NotesScreenControllerDesktop()
        shareActivityController = ShareScreenConrollerDesktop()
        settingsActivityController = SettingsScreenControllerDesktop()
        importScreenController = ImportScreenControllerDesktop()
        scheduleRepository.update()
        setupLocale(preferences["language", "ru"])
        setupFileChooser()
    }

    /**
     * Этот метод отображает UI приложения
     */
    @OptIn(ExperimentalSharedTransitionApi::class)
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
                    MainScreen()
                }
                composable(route = "settings"){
                    SettingsScreen()
                }
                composable(route = "shareApp") {
                    ShareAppScreenLand()
                }
                composable(route = "import") {
                    ImportScreen()
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
                    NotesScreen(group, date)
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
                    EditNoteScreen(noteID, group, date)
                }
            }

        }
    }

    companion object{
        private lateinit var instance: ScheduleApp
        val preferences: Preferences = Preferences.userNodeForPackage(ScheduleApp::class.java)

        /**
         * Этот метод позволяет получить доступ к экземпляру приложения.
         */
        fun getInstance(): ScheduleApp {
            return instance
        }
    }


    /**
     * Этот метод настраивает локаль приложения.
     */
    private fun setupLocale(locale: String){
        val locale = Locale(Companion.preferences["language", "ru"])
        Locale.setDefault(locale)
    }

    /**
     * Этот метод задает визуальное представление и локализацию системного элемента для выбора файлов.
     */
    private fun setupFileChooser(){
        UIManager.put(
            "FileChooser.lookInLabelText",
            runBlocking { getString(Res.string.lookInLabelText) }
        )
        UIManager.put(
            "FileChooser.filesOfTypeLabelText",
            runBlocking { getString(Res.string.filesOfTypeLabelText) }
        )
        UIManager.put(
            "FileChooser.folderNameLabelText",
            runBlocking { getString(Res.string.folderNameLabelText) }
        )
        UIManager.put(
            "FileChooser.upFolderToolTipText",
            runBlocking { getString(Res.string.upFolderToolTipText) }
        )
        UIManager.put(
            "FileChooser.homeFolderToolTipText",
            runBlocking { getString(Res.string.homeFolderToolTipText) }
        )
        UIManager.put(
            "FileChooser.newFolderToolTipText",
            runBlocking { getString(Res.string.newFolderToolTipText) }
        )
        UIManager.put(
            "FileChooser.listViewButtonToolTipText",
            runBlocking { getString(Res.string.listViewButtonToolTipText) }
        )
        UIManager.put(
            "FileChooser.detailsViewButtonToolTipText",
            runBlocking { getString(Res.string.detailsViewButtonToolTipText) }
        )
        UIManager.put(
            "FileChooser.saveButtonText",
            runBlocking {  getString(Res.string.saveButtonText) }
        )
        UIManager.put(
            "FileChooser.cancelButtonText",
            runBlocking { getString(Res.string.cancelButtonText) }
        )
    }
}