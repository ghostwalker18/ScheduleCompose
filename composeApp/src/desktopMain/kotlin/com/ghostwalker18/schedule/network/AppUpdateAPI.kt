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

package com.ghostwalker18.schedule.network

import kotlinx.serialization.json.JsonObject
import retrofit2.Call
import retrofit2.http.GET

/**
 * Интерфейс для создания Retrofit2 API,
 * используемого при проверке наличия обновления приложения.
 *
 * @author  Ипатов Никита
 * @since 3.0
 */
interface AppUpdateAPI {

    /**
     * Это свойство используется для получения информации о последнем доступном релизе приложения.
     */
    @get: GET("https://api.github.com/repos/ghostwalker18/ScheduleCompose/releases/latest")
    val latestDesktopReleaseInfo: Call<JsonObject>
}