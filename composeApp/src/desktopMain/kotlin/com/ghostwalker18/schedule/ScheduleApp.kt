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
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.ghostwalker18.schedule.models.ScheduleRepositoryDesktop
import com.ghostwalker18.schedule.network.NetworkService
import com.ghostwalker18.schedule.platform.*
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.get
import com.ghostwalker18.schedule.database.AppDatabase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.runBlocking
import com.ghostwalker18.schedule.models.NotesRepository
import com.ghostwalker18.schedule.models.ScheduleRepository
import com.ghostwalker18.schedule.ui.theme.ScheduleTheme
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.*
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
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ScheduleApp {
    actual val scheduleRepository: ScheduleRepository
    actual val notesRepository: NotesRepository
    actual val importController: ImportController
    actual val shareController: ShareController
    actual val preferences: ObservableSettings = PreferencesSettings(
        Preferences.userNodeForPackage(ScheduleApp::class.java)
    )
    private val db: AppDatabase
    private val themeState = MutableStateFlow(preferences["theme", "system"])
    var language by mutableStateOf(preferences.getString("language", "ru"))

    private lateinit var _navigator: Navigator
    actual val navigator by lazy { _navigator }

    private val themeChangedListener = preferences.addStringListener(
        "theme", "system"
    ){
        themeState.value = it
    }

    private val localeChangedListener = preferences.addStringListener(
        "language", "ru"
    ){
        language = it
    }

    init {
        _instance = this
        db = AppDatabase.getInstance()
        scheduleRepository = ScheduleRepositoryDesktop(
            db,
            NetworkService(URLs.BASE_URI).getScheduleAPI(),
            preferences
        )
        notesRepository = NotesRepository(db)
        importController = ImportControllerDesktop()
        shareController = ShareControllerDesktop()
        scheduleRepository.update()
        setupFileChooser()
    }

    /**
     * Этот метод отображает UI приложения
     */
    @OptIn(ExperimentalSharedTransitionApi::class)
    @Composable
    fun App() {
        val theme by themeState.collectAsState()
        val navController = rememberNavController()
        this._navigator = NavigatorDesktop(navController)

        CompositionLocalProvider(
            LocalAppLocaleISO provides language
        ){
            key(language){
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
                        baseRoutes()
                    }
                }
            }
        }
    }

    actual companion object{
        private lateinit var _instance: ScheduleApp
        actual val instance by lazy { _instance }
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