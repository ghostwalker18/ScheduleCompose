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

package com.ghostwalker18.schedule.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ghostwalker18.schedule.Orientation
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.getNavigator
import com.ghostwalker18.schedule.getScreenOrientation
import com.ghostwalker18.schedule.platform.NavigatorAndroid
import com.ghostwalker18.schedule.utils.setContentWithTheme
import com.ghostwalker18.schedule.views.*
import java.util.*

/**
 * Этот класс представляет собой основной экран приложения.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class MainActivity : AppCompatActivity() {

    @OptIn(ExperimentalSharedTransitionApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentWithTheme {
            val navController = rememberNavController()
            ScheduleApp.getInstance().navigator = NavigatorAndroid(navController)
            SharedTransitionLayout {
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
                        when(getScreenOrientation()){
                            Orientation.Portrait -> ShareAppScreenPortrait()
                            else -> ShareAppScreenLand()
                        }
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
                        NotesScreen(
                            group = group,
                            date = date,
                            sharedTransitionScope = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }

                    composable(
                        route="notePhoto/{photoID}",
                        arguments = listOf(
                            navArgument("photoID"){ type = NavType.StringType}
                        )
                    ){
                            stackEntry ->
                        val photoID = stackEntry.arguments?.getString("photoID")!!
                        PhotoViewScreen(
                            photoID = photoID,
                            sharedTransitionScope  = this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        ){
                            getNavigator().goBack()
                        }
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
                        EditNoteScreen(
                            noteID = noteID,
                            group = group,
                            date = date,
                            sharedTransitionScope =  this@SharedTransitionLayout,
                            animatedVisibilityScope = this@composable
                        )
                    }
                }
            }
            when(intent.extras?.getString("shortcut_id")){
                "notes" -> getNavigator().goNotesActivity("ИС-22", Calendar.getInstance())
                "add_note" -> getNavigator().goEditNoteActivity("ИС-22", Calendar.getInstance(), 0)
            }
        }
    }
}