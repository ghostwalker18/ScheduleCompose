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

import Navigator
import androidx.navigation.NavController

class NavigatorDesktop(private val navController: NavController) : Navigator {

    override fun goBack() {
        navController.navigateUp()
    }

    override fun goSettingsActivity(){
        navController.navigate("settings")
    }

    override fun goNotesActivity() {
        navController.navigate("notes")
    }

    override fun goEditNoteActivity(){
        navController.navigate("editNote")
    }
}