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

import com.ghostwalker18.schedule.MainScreenController
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.ghostwalker18.schedule.ScheduleApp
import io.appmetrica.analytics.AppMetrica
import com.ghostwalker18.schedule.models.Lesson
import org.jetbrains.compose.resources.StringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.nothing_to_share
import java.io.File


class MainScreenControllerAndroid(private val context: Context) : MainScreenController {
    override fun shareSchedule(lessons: Collection<Lesson>): Pair<Boolean, StringResource> {
        if(lessons.isEmpty()){
            return Pair(true, Res.string.nothing_to_share)
        }
        else {
            val schedule = StringBuilder()
            for (lesson in lessons) {
                schedule.append(lesson.toString(), "\n")
            }
            val intent = Intent(Intent.ACTION_SEND)
            intent.setType("text/plain")

            intent.putExtra(Intent.EXTRA_TEXT, schedule.toString())
            val shareIntent = Intent.createChooser(intent, null)

            if (ScheduleApp.getInstance().isAppMetricaActivated)
                AppMetrica.reportEvent("Поделились расписанием занятий")

            startActivity(context, shareIntent, null)
            return Pair(false, Res.string.nothing_to_share)
        }
    }

    override fun shareTimes(): Pair<Boolean, StringResource> {
        val mondayTimes = File(context.filesDir, "mondayTimes.jpg")
        val otherTimes = File(context.filesDir, "otherTimes.jpg")

        if (mondayTimes.exists() && otherTimes.exists()) {
            val imageUris = ArrayList<Uri>()

            val mondayTimesURI = FileProvider.getUriForFile(
                context,
                "com.ghostwalker18.schedule.timefilesprovider", mondayTimes
            )
            imageUris.add(mondayTimesURI)

            val otherTimesURI = FileProvider.getUriForFile(
                context,
                "com.ghostwalker18.schedule.timefilesprovider", otherTimes
            )
            imageUris.add(otherTimesURI)

            val shareIntent = Intent(Intent.ACTION_SEND_MULTIPLE)
            shareIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            shareIntent.setType("image/*")
            startActivity(context, Intent.createChooser(shareIntent, null), null)

            if (ScheduleApp.getInstance().isAppMetricaActivated)
                AppMetrica.reportEvent("Поделились расписанием звонков")

            return Pair(false, Res.string.nothing_to_share)
        } else {
            return Pair(true, Res.string.nothing_to_share)
        }
    }
}