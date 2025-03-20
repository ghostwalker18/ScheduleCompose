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
import com.ghostwalker18.schedule.*
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
            ScheduleApp.instance._navigator = NavigatorAndroid(navController)
            SharedTransitionLayout {
                NavHost(
                    navController = navController,
                    startDestination = "main"
                ){
                    baseRoutes(this@SharedTransitionLayout)

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
                            ScheduleApp.instance.navigator.goBack()
                        }
                    }
                }
            }
            when(intent.extras?.getString("shortcut_id")){
                "notes" -> ScheduleApp.instance.scheduleRepository.savedGroup?.let {
                    ScheduleApp.instance.navigator.goNotesActivity(it, Calendar.getInstance())
                }
                "add_note" -> ScheduleApp.instance.scheduleRepository.savedGroup?.let {
                    ScheduleApp.instance.navigator.goEditNoteActivity(it, Calendar.getInstance(), 0)
                }
            }
        }
    }
}