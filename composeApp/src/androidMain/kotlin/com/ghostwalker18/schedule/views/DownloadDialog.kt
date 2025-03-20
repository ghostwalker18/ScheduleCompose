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

package com.ghostwalker18.schedule.views

import android.app.DownloadManager
import android.net.Uri
import android.os.Environment
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ghostwalker18.schedule.ScheduleApp
import io.appmetrica.analytics.AppMetrica
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.*
import com.ghostwalker18.schedule.utils.Utils

@Composable
actual fun DownloadDialog(
    isEnabled: MutableState<Boolean>,
    title: String,
    links: Array<String>,
    mimeType: String
){
    var visibility by isEnabled
    val downloadManager: DownloadManager = LocalContext
        .current
        .getSystemService(DownloadManager::class.java)
    if(visibility){
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
                                Thread {
                                    for (link in links) {
                                        val request = DownloadManager.Request(Uri.parse(link))
                                            .setMimeType(mimeType)
                                            .setNotificationVisibility(
                                                DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED
                                            )
                                            .setTitle(title)
                                            .setDestinationInExternalPublicDir(
                                                Environment.DIRECTORY_DOWNLOADS,
                                                Utils.getNameFromLink(link)
                                            )
                                        downloadManager.enqueue(request)
                                    }
                                    if (ScheduleApp.instance.isAppMetricaActivated)
                                        AppMetrica.reportEvent("Скачали файлы расписания")
                                }.start()
                            }
                    )
                }
            },
            onDismissRequest = { visibility = false }
        )
    }
}