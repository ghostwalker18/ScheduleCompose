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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import javax.swing.JFileChooser

@Composable
fun DownloadDialog(
    title: String,
    links: Array<String>,
    mimeType: String,
) {
    var visibility by remember { mutableStateOf(true) }
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
            contentColor = MaterialTheme.colors.primaryVariant,
            buttons = {
                Row(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            val fileChooser = JFileChooser()
                            fileChooser.fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
                            fileChooser.dialogTitle = runBlocking { getString(Res.string.download_file_dialog) }
                            val result = fileChooser.showDialog(null,
                                    runBlocking { getString(Res.string.saveButtonText) })
                            if (result == JFileChooser.APPROVE_OPTION) {
                                Thread {
                                    //Chosen directory is also a file, heh
                                    val directory = fileChooser.selectedFile.absolutePath
                                    for (link in links) {
                                        //val outputFile = File(directory + File.separator + Utils.getNameFromLink(link))
                                    }
                                    visibility = false
                                }.start()
                            } else {
                                visibility = false
                            }
                        },
                        modifier = Modifier.weight(0.5f)
                    ){
                        Text(stringResource(Res.string.download_ok))
                    }
                    Button(
                        onClick = {
                            visibility = false
                        },
                        modifier = Modifier.weight(0.5f)
                    ){
                        Text(stringResource(Res.string.download_cancel))
                    }
                }
            },
            onDismissRequest = { visibility = false }
        )
    }
}