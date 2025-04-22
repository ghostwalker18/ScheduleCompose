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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import com.ghostwalker18.schedule.ImportController
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.utils.Utils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.saveButtonText
import java.io.File
import javax.swing.JFileChooser

class ImportControllerDesktop : ImportController {
    override lateinit var dataType: String
    override lateinit var importPolicy: String
    private lateinit var scope: CoroutineScope

    @Composable
    override fun initController(){
        scope = rememberCoroutineScope()
    }

    override fun importDB(){/*Cannot be implemented yet*/}

    override fun exportDB(){
        scope.launch(
            context = Dispatchers.IO
        ){
            val exportFile = ScheduleApp.instance.database.exportDBFile(dataType)
            val fileChooser = JFileChooser()
            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            val result = fileChooser.showDialog(null,
                runBlocking { getString(Res.string.saveButtonText) })
            if (result == JFileChooser.APPROVE_OPTION) {
                val directory = fileChooser.selectedFile.absolutePath
                val exportedFile = File(directory, DATABASE_ARCHIVE)
                if (exportedFile.exists()) {
                    exportedFile.delete()
                    exportedFile.createNewFile()
                }
                exportFile?.let { Utils.zip(arrayOf(it), exportedFile) }
            }
            ScheduleApp.instance.database.deleteExportDBFile()
        }
    }

    companion object {
        private const val DATABASE_ARCHIVE = "pcme_schedule.zip"
    }
}