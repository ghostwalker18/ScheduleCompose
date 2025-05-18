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

package com.ghostwalker18.schedule.models

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import com.ghostwalker18.schedule.database.AppDatabase
import com.ghostwalker18.schedule.network.ScheduleNetworkAPI
import com.github.pjfanning.xlsx.StreamingReader
import com.github.pjfanning.xlsx.exceptions.OpenException
import com.github.pjfanning.xlsx.exceptions.ParseException
import com.github.pjfanning.xlsx.exceptions.ReadException
import com.russhwolf.settings.Settings
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.apache.poi.openxml4j.util.ZipSecureFile
import org.apache.poi.ss.usermodel.Workbook
import org.jetbrains.compose.resources.getString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.schedule_download_error
import scheduledesktop2.composeapp.generated.resources.schedule_download_status
import scheduledesktop2.composeapp.generated.resources.schedule_opening_status
import java.io.File
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import kotlin.reflect.KFunction1

/**
 * Этот класс представляет собой репозиторий данных десктопного приложения о расписании.
 * @param context контекст приложения
 *
 * @author  Ипатов Никита
 * @since 5.0
 */
class ScheduleRepositoryAndroid(
    private val context: Context,
    override val db: AppDatabase,
    private val api: ScheduleNetworkAPI,
    private val preferences: Settings
) : ScheduleRepository(db = db, api = api, preferences = preferences){

    override fun updateSchedule(
        linksGetter: Callable<List<String>>,
        parser: KFunction1<Workbook, List<Lesson>>
    ): UpdateResult {
        val scheduleLinks: List<String>
        try {
            scheduleLinks = linksGetter.call()
        } catch (_: java.lang.Exception) {
            return UpdateResult.FAIL
        }
        if (scheduleLinks.isEmpty()) {
            scope.launch {
                _status.value = Status(
                    getString(Res.string.schedule_download_error),
                    0
                )
            }
            return UpdateResult.FAIL
        }
        val successCounter: MutableList<UpdateResult> = ArrayList()
        val latch = CountDownLatch(scheduleLinks.size)
        for (link in scheduleLinks) {
            scope.launch {
                _status.value = Status(
                    getString(Res.string.schedule_download_status),
                    10
                )
            }
            api.getScheduleFile(link)!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    ZipSecureFile.setMinInflateRatio(0.0005)
                    scope.launch {
                        _status.value = Status(
                            getString(Res.string.schedule_opening_status),
                            33
                        )
                    }
                    try {
                        response.body().use { body ->
                            StreamingReader.builder()
                                .rowCacheSize(10)
                                .bufferSize(10485670)
                                .open(body!!.byteStream()).use { excelFile ->
                                    scope.launch {
                                        _status.value = Status(
                                            getString(Res.string.schedule_parsing_status),
                                            50
                                        )
                                    }
                                    val lessons: List<Lesson> = parser.invoke(excelFile)
                                    scope.launch{
                                        db.lessonDao().insertMany(lessons)
                                        _status.value = Status(
                                            getString(Res.string.processing_completed_status),
                                            100
                                        )
                                    }
                                    successCounter.add(UpdateResult.SUCCESS)
                                }
                        }
                    } catch (_: OpenException) {
                        scope.launch {
                            _status.value = Status(
                                getString(Res.string.schedule_opening_error),
                                0
                            )
                        }
                    } catch (_: ReadException) {
                        scope.launch {
                            _status.value = Status(
                                getString(Res.string.schedule_opening_error),
                                0
                            )
                        }
                    } catch (_: ParseException) {
                        scope.launch {
                            _status.value = Status(
                                getString(Res.string.schedule_opening_error),
                                0
                        )
                    }
                    } catch (_: Exception) {
                        scope.launch {
                            _status.value = Status(
                                getString(Res.string.schedule_opening_error),
                                0
                            )
                        }
                    } finally {
                        latch.countDown()
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    latch.countDown()
                    scope.launch {
                        _status.value = Status(
                            getString(Res.string.schedule_download_error),
                            0
                        )
                    }
                }
            })
        }
        try {
            latch.await()
            return if (successCounter.size == scheduleLinks.size) UpdateResult.SUCCESS
            else UpdateResult.FAIL
        } catch (_: Exception) {
            return UpdateResult.FAIL
        }
    }

    override fun updateTimes(): UpdateResult {
        val mondayTimesFile = File(context.filesDir, MONDAY_TIMES_PATH)
        val otherTimesFile = File(context.filesDir, OTHER_TIMES_PATH)
        if (!preferences.getBoolean(
                "doNotUpdateTimes",
                true
            ) || !mondayTimesFile.exists() || !otherTimesFile.exists()
        ) {
            api.mondayTimes!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    try {
                        response.body().use { body ->
                            context
                                .openFileOutput(MONDAY_TIMES_PATH, Context.MODE_PRIVATE).use { outputStream ->
                                    val bitmap = BitmapFactory.decodeStream(body?.byteStream())
                                    _mondayTimes.value = BitmapPainter(bitmap.asImageBitmap())
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                }
                        }
                    } catch (_: Exception) { /*Not required*/ }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) { /*Not required*/ }
            })
            api.otherTimes!!.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    try {
                        response.body().use { body ->
                            context
                                .openFileOutput(OTHER_TIMES_PATH, Context.MODE_PRIVATE).use { outputStream ->
                                    val bitmap = BitmapFactory.decodeStream(body?.byteStream())
                                    _otherTimes.value = BitmapPainter(bitmap.asImageBitmap())
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                }
                        }
                    } catch (_: Exception) { /*Not required*/ }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) { /*Not required*/ }
            })
        } else {
            val bitmap1 = BitmapFactory.decodeFile(mondayTimesFile.absolutePath)
            _mondayTimes.value = BitmapPainter(bitmap1.asImageBitmap())
            val bitmap2 = BitmapFactory.decodeFile(otherTimesFile.absolutePath)
            _otherTimes.value = BitmapPainter(bitmap2.asImageBitmap())
        }
        return UpdateResult.SUCCESS
    }
}