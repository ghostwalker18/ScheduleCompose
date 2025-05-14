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
import androidx.compose.animation.SharedTransitionScope
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.savedstate.read
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.views.*

/**
 * Эта функция описывает базовые маршруты навигации,
 * актуальные для всех платформ приложения.
 *
 * @author Ипатов Никита
 */
@OptIn(ExperimentalSharedTransitionApi::class)
fun NavGraphBuilder.baseRoutes(
    sharedTransitionScope: SharedTransitionScope? = null
){

    composable(
        route = "main?date={date}",
        arguments = listOf(
            navArgument("date")
            {
                type = NavType.StringType
            }
        )
    ){
        stackEntry ->
        val date = stackEntry.arguments?.read {
            getStringOrNull("date")
        }
        if(date == null)
            MainScreen()
        else
            DateConverters().fromString(date)?.let{ MainScreen(it)}
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
        route = "schedule/{date}/{group}/{teacher}",
        arguments = listOf(
            navArgument("date")
            {
                type = NavType.StringType
            },
            navArgument("group")
            {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("teacher")
            {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        )
    ){
        stackEntry ->
        val date = DateConverters().fromString(
            stackEntry.arguments?.read{ getString("date") }
        )
        val group = stackEntry.arguments?.read{ getString("group") }
        val teacher = stackEntry.arguments?.read{ getString("teacher") }
        ScheduleItemScreen(
            group = group,
            teacher = teacher,
            date = date!!
        )
    }

    composable(
        route = "notes/{group}/{date}",
        arguments = listOf(
            navArgument("group"){ type = NavType.StringType },
            navArgument("date"){ type = NavType.StringType })
    ){
            stackEntry ->
        val group = stackEntry.arguments?.read{ getString("group") }
        val date = DateConverters().fromString(
            stackEntry.arguments?.read{ getString("date") }
        )
        NotesScreen(
            group = group,
            date = date,
            sharedTransitionScope = sharedTransitionScope,
            animatedVisibilityScope = this@composable
        )
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
        val group = stackEntry.arguments?.read{ getString("group") }
        val date = DateConverters().fromString(
            stackEntry.arguments?.read{ getString("date") }
        )
        val noteID = stackEntry.arguments?.read{ getInt("noteID") }
        EditNoteScreen(
            noteID = noteID,
            group = group,
            date = date,
            sharedTransitionScope =  sharedTransitionScope,
            animatedVisibilityScope = this@composable
        )
    }
}