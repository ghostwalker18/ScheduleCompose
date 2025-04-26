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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.core.content.FileProvider
import com.ghostwalker18.schedule.ImportController
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.utils.Utils
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.File
import java.nio.file.Files

class ImportControllerAndroid(val context: Context) : ImportController {
    override lateinit var dataType: String
    override lateinit var importPolicy: String
    private lateinit var documentPicker: ManagedActivityResultLauncher<Array<String>, Uri?>
    private lateinit var shareDBFileLauncher: ManagedActivityResultLauncher<Intent, ActivityResult>
    private lateinit var scope: CoroutineScope

    @Composable
    override fun initController(){
        scope = rememberCoroutineScope()
        documentPicker = rememberLauncherForActivityResult(
            ActivityResultContracts.OpenDocument()
        ){
            fileName ->
            scope.launch(
                context = Dispatchers.IO
            ){
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

                                    Utils.unzip(archive, databaseCache)
                                    importedFile = File(databaseCache, "export_database.db")
                                    ScheduleApp.instance.database.importDBFile(
                                        importedFile, dataType, importPolicy
                                    )
                                }
                            }
                        }
                    } catch (e: Exception) {
                        Log.e("import", e.message ?: "")
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

        Thread { documentPicker.launch(arrayOf("application/zip")) }.start()
    }

    override fun exportDB(){
        if(ScheduleApp.instance.isAppMetricaActivated)
            AppMetrica.reportEvent("Экспортировали данные приложения")
        scope.launch(
            context = Dispatchers.IO
        ){
            try {
                val file = ScheduleApp.instance.database
                    .exportDBFile(dataType)
                val databaseCache = File(context.cacheDir, "database")
                if (!databaseCache.exists()) databaseCache.mkdir()
                val exportedFile = File(databaseCache, DATABASE_ARCHIVE)
                if (exportedFile.exists()) {
                    exportedFile.delete()
                    exportedFile.createNewFile()
                }
                file?.let { Utils.zip(arrayOf(it), exportedFile) }
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
            } catch (_: Exception) { /*Not required*/ }
        }
    }

    companion object {
        private const val DATABASE_ARCHIVE = "pcme_schedule.zip"
    }
}