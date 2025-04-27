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
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.utils.Utils
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.saveButtonText
import java.io.File
import javax.swing.JFileChooser

/**
 * Этот класс представляет реализацию контроллера экспорта/импорта данных
 * приложения для десктопа.
 *
 * @author Ипатов Никита
 */
class ImportControllerDesktop : ImportController() {

    @Composable
    override fun initController(){ /*Not required*/ }

    override fun importDB(){ /*Cannot be implemented yet*/ }

    override fun exportDB(){
        scope.launch {
            _status.value = OperationStatus.Started
            try {
                val fileChooser = JFileChooser()
                fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                val result = fileChooser.showDialog(null,
                    runBlocking { getString(Res.string.saveButtonText) })
                if (result == JFileChooser.APPROVE_OPTION) {
                    _status.value = OperationStatus.Doing
                    val exportFile = ScheduleApp.instance.database.exportDBFile(dataType)
                    val directory = fileChooser.selectedFile.absolutePath
                    val exportedFile = File(directory, DATABASE_ARCHIVE)
                    if (exportedFile.exists()) {
                        exportedFile.delete()
                        exportedFile.createNewFile()
                    }
                    if(exportFile != null){
                        _status.value = OperationStatus.Packing
                        Utils.zip(arrayOf(exportFile), exportedFile)
                    }
                    else{
                        _status.value = OperationStatus.Error
                        return@launch
                    }
                }
                ScheduleApp.instance.database.deleteExportDBFile()
                _status.value = OperationStatus.Ended
            } catch (_: Exception){
                _status.value = OperationStatus.Error
            }
        }
    }

    companion object {
        private const val DATABASE_ARCHIVE = "pcme_schedule.zip"
    }
}