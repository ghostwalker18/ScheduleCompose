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
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import com.ghostwalker18.schedule.activities.EditNoteActivity
import com.ghostwalker18.schedule.activities.NotesActivity
import com.ghostwalker18.schedule.activities.SettingsActivity
import com.ghostwalker18.schedule.activities.ShareAppActivity
import com.ghostwalker18.schedule.activities.ImportActivity
import com.ghostwalker18.schedule.converters.DateConverters
import java.util.*

class NavigatorAndroid(private val context: Context) : Navigator{

    override fun goBack() {
    }

    override fun goSettingsActivity() {
        val intent = Intent(context, SettingsActivity::class.java)
        startActivity(context, intent, null)
    }

    override fun goShareAppActivity() {
        val intent = Intent(context, ShareAppActivity::class.java)
        startActivity(context, intent, null)
    }

    override fun goImportActivity() {
        val intent = Intent(context, ImportActivity::class.java)
        startActivity(context, intent, null)
    }

    override fun goNotesActivity(group: String, date: Calendar) {
        val bundle = Bundle()
        val intent = Intent(context, NotesActivity::class.java)
        bundle.putString("group", group)
        bundle.putString("date", DateConverters().toString(date))
        intent.putExtras(bundle)
        startActivity(context, intent, null)
    }

    override fun goEditNoteActivity(group: String, date: Calendar, noteID: Int?) {
        val intent = Intent(context, EditNoteActivity::class.java)
        val bundle = Bundle()
        bundle.putString("group", group)
        bundle.putString("date", DateConverters().toString(date))
        bundle.putInt("noteID", noteID ?: 0)
        intent.putExtras(bundle)
        startActivity(context, intent, null)
    }
}