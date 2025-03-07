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

package com.ghostwalker18.scheduledesktop2.platform

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ghostwalker18.scheduledesktop2.network.NetworkService
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scheduledesktop2.composeapp.generated.resources.*
import utils.Utils
import java.io.File
import java.nio.file.Files
import javax.swing.JFileChooser

/**
 * Эта функция позволяет открыть диалог для скачивания файлов расписания.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
fun DownloadDialog(
    isEnabled: MutableState<Boolean>,
    links: Array<String>,
){
    var visibility by isEnabled
    if(visibility)
    {
        AlertDialog(
            title = {
                Row {
                    Icon(Icons.Filled.Download, null)
                    Text(stringResource(Res.string.download_approvement))
                }
            },
            text = {
                Text(stringResource(Res.string.download_notice).format(links.size))
            },
            backgroundColor = MaterialTheme.colors.background,
            contentColor = MaterialTheme.colors.onBackground,
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(Res.string.download_cancel),
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(10.dp)
                            .clickable {
                                visibility = false
                            }
                    )
                    Text(
                        text = stringResource(Res.string.download_ok),
                        color = MaterialTheme.colors.primaryVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(0.5f)
                            .padding(10.dp)
                            .clickable {
                                val fileChooser = JFileChooser()
                                fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                                fileChooser.dialogTitle = runBlocking { getString(Res.string.download_file_dialog) }
                                val result = fileChooser.showDialog(null,
                                    runBlocking { getString(Res.string.saveButtonText) })
                                val api = NetworkService(URLs.BASE_URI).getScheduleAPI()
                                if (result == JFileChooser.APPROVE_OPTION) {
                                    Thread {
                                        //Chosen directory is also a file, heh
                                        val directory = fileChooser.selectedFile.absolutePath
                                        for (link in links) {
                                            val outputFile = File(directory
                                                    + File.separator + Utils.escapeIllegalCharacters(
                                                            Utils.getNameFromLink(link)
                                                    )
                                            )
                                            api.getScheduleFile(link)?.enqueue(
                                                object : Callback<ResponseBody?> {
                                                    override fun onResponse(
                                                        call: Call<ResponseBody?>,
                                                        response: Response<ResponseBody?>
                                                    ) {
                                                        response.body()?.byteStream()?.let {
                                                            Files.copy(it, outputFile.toPath())
                                                        }
                                                    }

                                                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {/*Not required*/}
                                                }
                                            )
                                        }
                                        visibility = false
                                    }.start()
                                } else {
                                    visibility = false
                                }
                            }
                    )
                }
            },
            onDismissRequest = { visibility = false }
        )
    }
}