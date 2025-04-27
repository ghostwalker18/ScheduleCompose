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

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.core.content.FileProvider
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.utils.Utils
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.nio.file.Files

/**
 * Этот класс представляет реализацию контроллера экспорта/импорта данных
 * приложения для Android.
 *
 * @author Ипатов Никита
 */
class ImportControllerAndroid(val context: Context) : ImportController() {
    private lateinit var documentPicker: ManagedActivityResultLauncher<Array<String>, Uri?>
    private lateinit var shareDBFileLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>

    @Composable
    override fun initController(){
        documentPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ){
            fileName ->
            scope.launch {
                val databaseCache = File(context.cacheDir, "database")
                if (!databaseCache.exists()) databaseCache.mkdir()
                val archive = File(databaseCache, DATABASE_ARCHIVE)
                var importedFile: File? = null
                if (fileName != null) {
                    //First, copy file that we got to cache directory to get access to it
                    try {
                        context.contentResolver.openInputStream(fileName).use {
                            origin ->
                            BufferedInputStream(origin, 4096).use {
                                `in` ->
                                Files.newOutputStream(archive.toPath()).use {
                                    out ->
                                    val data = ByteArray(4096)
                                    var count: Int
                                    while ((`in`.read(data, 0, 4096).also { count = it }) != -1)
                                        out.write(
                                            data,
                                            0,
                                            count
                                        )
                                    _status.value = OperationStatus.Unpacking
                                    Utils.unzip(archive, databaseCache)
                                    importedFile = File(databaseCache, "export_database.db")
                                    _status.value = OperationStatus.Doing
                                    ScheduleApp.instance.database.importDBFile(
                                        importedFile, dataType, importPolicy
                                    )
                                }
                            }
                        }
                        _status.value = OperationStatus.Ended
                    } catch (e: Exception) {
                        Log.e("import", e.message ?: "")
                        _status.value = OperationStatus.Error
                    }
                    finally {
                        archive.delete()
                        importedFile?.delete()
                    }
                }
            }

        }
        shareDBFileLauncher = rememberLauncherForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){
            ScheduleApp.instance.database.deleteExportDBFile()
            val exportedFile = File(context.cacheDir, "database/$DATABASE_ARCHIVE")
            if (exportedFile.exists()) exportedFile.delete()
        }
    }

    override fun importDB(){
        if (ScheduleApp.instance.isAppMetricaActivated)
            AppMetrica.reportEvent("Импортировали данные приложения")
        _status.value = OperationStatus.Started
        Thread { documentPicker.launch(arrayOf("application/zip")) }.start()
    }

    override fun exportDB(){
        _status.value = OperationStatus.Started
        if(ScheduleApp.instance.isAppMetricaActivated)
            AppMetrica.reportEvent("Экспортировали данные приложения")
        scope.launch {
            try {
                _status.value = OperationStatus.Doing
                val file = ScheduleApp.instance.database
                    .exportDBFile(dataType)
                val databaseCache = File(context.cacheDir, "database")
                if (!databaseCache.exists()) databaseCache.mkdir()
                val exportedFile = File(databaseCache, DATABASE_ARCHIVE)
                if (exportedFile.exists()) {
                    exportedFile.delete()
                    exportedFile.createNewFile()
                }
                _status.value = OperationStatus.Packing
                if(file != null){
                    Utils.zip(arrayOf(file), exportedFile)
                } else {
                    _status.value = OperationStatus.Error
                    return@launch
                }
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.putExtra(
                    Intent.EXTRA_STREAM,
                    FileProvider.getUriForFile(
                        context,
                        "com.ghostwalker18.schedule.timefilesprovider",
                        exportedFile
                    )
                )
                shareIntent.type = "application/zip"
                shareDBFileLauncher.launch(Intent.createChooser(shareIntent, null))
                _status.value = OperationStatus.Ended
            } catch (_: Exception) {
                _status.value = OperationStatus.Error
            }
        }
    }

    companion object {
        private const val DATABASE_ARCHIVE = "pcme_schedule.zip"
    }
}