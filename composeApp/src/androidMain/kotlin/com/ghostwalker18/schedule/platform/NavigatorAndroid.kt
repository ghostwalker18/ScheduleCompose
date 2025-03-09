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

import Navigator
import android.content.Context
import java.util.*

class NavigatorAndroid(private val context: Context) : Navigator{

    override fun goBack() {
        TODO("Not yet implemented")
    }

    override fun goSettingsActivity() {
    }

    override fun goShareAppActivity() {
        TODO("Not yet implemented")
    }

    override fun goImportActivity() {
        TODO("Not yet implemented")
    }

    override fun goNotesActivity(group: String, date: Calendar) {
        TODO("Not yet implemented")
    }

    override fun goEditNoteActivity(group: String, date: Calendar, noteID: Int?) {
        TODO("Not yet implemented")
    }
}