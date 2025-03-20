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

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import androidx.core.content.FileProvider
import com.ghostwalker18.schedule.R
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.ShareController
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.models.Note
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.developer_email
import scheduledesktop2.composeapp.generated.resources.nothing_to_share
import java.io.File

class ShareControllerAndroid(private val context: Context) : ShareController {

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
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(shareIntent, null)
            if (ScheduleApp.getInstance().isAppMetricaActivated)
                AppMetrica.reportEvent("Поделились расписанием занятий")

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

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            intent.setType("image/*")

            val shareIntent = Intent.createChooser(intent, null)
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(shareIntent, null)
            if (ScheduleApp.getInstance().isAppMetricaActivated)
                AppMetrica.reportEvent("Поделились расписанием звонков")

            return Pair(false, Res.string.nothing_to_share)
        } else {
            return Pair(true, Res.string.nothing_to_share)
        }
    }

    override fun shareNotes(notes: Collection<Note>): Pair<Boolean, StringResource> {
        val notesToShare = StringBuilder()
        for (note in notes) {
            notesToShare.append(note.toString()).append("\n")
        }

        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT, notesToShare.toString())

        val shareIntent = Intent.createChooser(intent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(context, shareIntent, null)
        if (ScheduleApp.getInstance().isAppMetricaActivated)
            AppMetrica.reportEvent("Поделились заметками")

        return Pair(false, Res.string.nothing_to_share)
    }

    override fun shareLink(): Pair<Boolean, StringResource> {
        val intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/plain")
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.rustore_link) )

        val shareIntent = Intent.createChooser(intent, null)
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

        startActivity(context, intent, null)
        return Pair(false, Res.string.nothing_to_share)
    }

    override fun connectToDeveloper(): Pair<Boolean, StringResource> {
        try {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.setData(Uri.parse("mailto:")) // only email apps should handle this
            intent.putExtra(Intent.EXTRA_EMAIL,
                arrayOf(runBlocking { getString(Res.string.developer_email) }))
            intent.putExtra(Intent.EXTRA_SUBJECT, runBlocking { getString(Res.string.developer_email) } )

            val shareIntent = Intent.createChooser(
                intent,
                runBlocking { getString(Res.string.developer_email) }
            )
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            context.startActivity(shareIntent, null)
        } catch (e: ActivityNotFoundException) {
            return Pair(true, Res.string.developer_email)
        } catch (ignored: Exception) { /*Not required*/ }
        return Pair(false, Res.string.nothing_to_share)
    }
}