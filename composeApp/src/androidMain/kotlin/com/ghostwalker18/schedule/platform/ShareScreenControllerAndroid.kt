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

import com.ghostwalker18.schedule.ShareScreenController
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.ghostwalker18.schedule.R
import org.jetbrains.compose.resources.StringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.nothing_to_share

class ShareScreenControllerAndroid(private val context: Context) : ShareScreenController {

    override fun shareLink(): Pair<Boolean, StringResource> {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.rustore_link) )

        val shareIntent = Intent.createChooser(intent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(context, intent, null)
        return Pair(false, Res.string.nothing_to_share)
    }
}