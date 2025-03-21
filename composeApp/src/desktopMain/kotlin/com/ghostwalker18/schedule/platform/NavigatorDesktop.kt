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

package com.ghostwalker18.schedule.platform

import com.ghostwalker18.schedule.Navigator
import androidx.navigation.NavController
import com.ghostwalker18.schedule.converters.DateConverters
import java.util.*

/**
 * Этот класс осуществляет навигацию по десктопному приложению
 * @property navController навигационный контроллер
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class NavigatorDesktop(private val navController: NavController) : Navigator {

    override fun goBack() {
        navController.navigateUp()
    }

    override fun goScheduleScreen(date: Calendar, group: String?, teacher: String?){
        val dateString = DateConverters().toString(date)!!
        navController.navigate("schedule/$dateString/$group/$teacher")
    }

    override fun goSettingsActivity(){
        navController.navigate("settings")
    }

    override fun goShareAppActivity(){
        navController.navigate("shareApp")
    }

    override fun goImportActivity(){
        navController.navigate("import")
    }

    override fun goNotesActivity(group: String, date: Calendar) {
        val dateString = DateConverters().toString(date)!!
        navController.navigate("notes/$group/$dateString")
    }

    override fun goEditNoteActivity(group: String, date: Calendar, noteID: Int?){
        val dateString = DateConverters().toString(date)!!
        navController.navigate("editNote/$group/$dateString/$noteID")
    }
}