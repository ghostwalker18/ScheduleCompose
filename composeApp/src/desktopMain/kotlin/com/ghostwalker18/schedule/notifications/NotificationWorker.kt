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

package com.ghostwalker18.schedule.notifications

import androidx.compose.ui.window.Notification
import androidx.compose.ui.window.TrayState
import com.ghostwalker18.schedule.network.AppUpdateAPI
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.jetbrains.compose.resources.getString
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import scheduledesktop2.composeapp.generated.resources.*
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.update_available
import scheduledesktop2.composeapp.generated.resources.update_info

/**
 * Этот класс исполняет работу по работе с уведомлениями для десктопного приложения.
 */
class NotificationWorker(
    private val tray: TrayState,
    private val updateAPI: AppUpdateAPI
) {

    /**
     * Этот метод проверяет наличие уведомлений для десктопного приложения.
     */
    fun checkForUpdates(){
        updateAPI.latestDesktopReleaseInfo.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val latestVersion = response.body()?.get("tag_name")?.jsonPrimitive?.content
                val currentVersion = "3.0"
                latestVersion?.let {
                    if(latestVersion > currentVersion){
                        val title = runBlocking { getString(Res.string.update_available) }
                        val message = runBlocking {
                            getString(Res.string.update_info, latestVersion) + " " + getString(Res.string.repo_address)
                        }
                        tray.sendNotification(
                            Notification(
                                title = title,
                                message = message,
                                type = Notification.Type.Info
                            )
                        )
                    }
                }
            }

            override fun onFailure(p0: Call<JsonObject>, p1: Throwable) { /*Not required*/ }
        })
    }
}