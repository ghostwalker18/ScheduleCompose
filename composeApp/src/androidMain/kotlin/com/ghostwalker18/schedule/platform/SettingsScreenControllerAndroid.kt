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

import SettingsActivityController
import android.R
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import com.ghostwalker18.schedule.ScheduleApp
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.developer_email
import scheduledesktop2.composeapp.generated.resources.nothing_to_share


class SettingsScreenControllerAndroid(private val context: Context) : SettingsActivityController {

    override fun connectToDeveloper(): Pair<Boolean, StringResource> {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:")) // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL,
                arrayOf(runBlocking { getString(Res.string.developer_email) }))
            intent.putExtra(Intent.EXTRA_SUBJECT, runBlocking { getString(R.string.email_subject) } )
            startActivity(context,
                Intent.createChooser(intent, runBlocking { getString(R.string.connect_to_developer) }),
            null)
        } catch (e: ActivityNotFoundException) {
            return Pair(true, Res.string.no_email_client_found)
        } catch (ignored: Exception) { /*Not required*/ }
        return Pair(false, Res.string.nothing_to_share)
    }
}