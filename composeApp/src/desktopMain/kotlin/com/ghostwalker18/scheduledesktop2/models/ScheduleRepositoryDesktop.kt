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

package com.ghostwalker18.scheduledesktop2.models

import androidx.compose.ui.graphics.toPainter
import com.github.pjfanning.xlsx.StreamingReader
import com.github.pjfanning.xlsx.exceptions.OpenException
import com.github.pjfanning.xlsx.exceptions.ParseException
import com.github.pjfanning.xlsx.exceptions.ReadException
import com.russhwolf.settings.Settings
import database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.Lesson
import models.ScheduleRepository
import network.ScheduleNetworkAPI
import okhttp3.ResponseBody
import org.apache.poi.openxml4j.util.ZipSecureFile
import org.apache.poi.ss.usermodel.Workbook
import org.jetbrains.compose.resources.getString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scheduledesktop2.composeapp.generated.resources.*
import utils.Utils
import java.io.File
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.ArrayList
import java.util.concurrent.Callable
import java.util.concurrent.CountDownLatch
import javax.imageio.ImageIO
import kotlin.reflect.KFunction1

/**
 * Этот класс представляет собой репозиторий данных десктопного приложения о расписании.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
class ScheduleRepositoryDesktop(
    override val db: AppDatabase,
    private val api: ScheduleNetworkAPI,
    private val preferences: Settings
): ScheduleRepository(db = db, api = api, preferences = preferences) {
    private val _scheduleFiles = mutableListOf<Pair<String, File>>()
    private val scope = CoroutineScope(Dispatchers.IO)

    override fun updateSchedule(
        linksGetter: Callable<List<String>>,
        parser: KFunction1<Workbook, List<Lesson>>
    ): UpdateResult {
        val scheduleLinks: List<String>
        try {
            scheduleLinks = linksGetter.call()
        } catch (e: Exception) {
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
        val successCounter: MutableList<UpdateResult> = ArrayList<UpdateResult>()
        val latch = CountDownLatch(scheduleLinks.size)
        for (link in scheduleLinks) {
            scope.launch {
                _status.value = Status(
                    getString(Res.string.schedule_download_status),
                    10
                )
            }
            api.getScheduleFile(link)?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>,
                                        response: Response<ResponseBody?>
                ) {
                    ZipSecureFile.setMinInflateRatio(0.005)
                    scope.launch {
                        _status.value = Status(
                            getString(Res.string.schedule_opening_status),
                            33
                        )
                    }
                    try {
                        response.body().use { body ->
                            body!!.byteStream().use { stream ->
                                StreamingReader.builder()
                                    .rowCacheSize(10)
                                    .bufferSize(10485670)
                                    .open(stream).use { excelFile ->
                                        scope.launch {
                                            _status.value = Status(
                                                getString(Res.string.schedule_parsing_status),
                                                50
                                            )
                                        }
                                        val scheduleFile = Files.createTempFile(null, ".tmp").toFile()
                                        scheduleFile.deleteOnExit()
                                        Files.copy(stream, scheduleFile.toPath(), StandardCopyOption.REPLACE_EXISTING)
                                        _scheduleFiles.add(Pair(Utils.getNameFromLink(link), scheduleFile))
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
                        }
                    } catch (e: OpenException) {
                        scope.launch {
                            _status.value = Status(
                                getString(Res.string.schedule_opening_error),
                                0
                            )
                        }
                    } catch (e: ReadException) {
                        scope.launch {
                            _status.value = Status(
                                getString(Res.string.schedule_opening_error),
                                0
                            )
                        }
                    } catch (e: ParseException) {
                        scope.launch {
                            _status.value = Status(
                                getString(Res.string.schedule_opening_error),
                                0
                            )
                        }
                    } catch (e: Exception) {
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
            return if (successCounter.size == scheduleLinks.size)
                UpdateResult.SUCCESS
            else
                UpdateResult.FAIL
        } catch (e: InterruptedException) {
            Thread.currentThread().interrupt()
            return UpdateResult.FAIL
        } catch (e: Exception) {
            return UpdateResult.FAIL
        }
    }

    override fun updateTimes(): UpdateResult {
        val mondayTimesFile = File(mondayTimesPath)
        val otherTimesFile = File(otherTimesPath)
        if (!preferences.getBoolean(
                "doNotUpdateTimes",
                true
            ) || !mondayTimesFile.exists() || !otherTimesFile.exists()
        ) {
            api.mondayTimes?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    try {
                        response.body().use { body ->
                            val bufferedImage = ImageIO.read(body!!.byteStream())
                            _mondayTimes.value = bufferedImage.toPainter()
                            ImageIO.write(bufferedImage, "jpg", mondayTimesFile)
                        }
                    } catch (ignored: java.lang.Exception) { /*Not required*/ }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) { /*Not required*/ }
            })
            api.otherTimes?.enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                    try {
                        response.body().use { body ->
                            val bufferedImage = ImageIO.read(body!!.byteStream())
                            _otherTimes.value = bufferedImage.toPainter()
                            ImageIO.write(bufferedImage, "jpg", mondayTimesFile)
                        }
                    } catch (ignored: java.lang.Exception) { /*Not required*/
                    }
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) { /*Not required*/ }
            })
        } else {
            try {
                val bitmap1 = ImageIO.read(mondayTimesFile)
                _mondayTimes.value = bitmap1.toPainter()
                val bitmap2 = ImageIO.read(otherTimesFile)
                _otherTimes.value = bitmap2.toPainter()
            } catch (ignored: java.lang.Exception) { /*Not required*/
            }
        }
        return UpdateResult.SUCCESS
    }
}