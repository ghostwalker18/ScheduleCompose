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

package network

import URLs
import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Интерфейс для создания Retrofit2 API,
 * используемого при скачивании файлов расписания и звонков.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
interface ScheduleNetworkAPI {
    @get:GET(URLs.MONDAY_TIMES_URL)
    val mondayTimes: Call<ResponseBody?>?

    @get:GET(URLs.OTHER_TIMES_URL)
    val otherTimes: Call<ResponseBody?>?

    /**
     * Получение файла расписания по заданному URL.
     *
     * @return асинхронный ответ сервера
     */
    @GET
    fun getScheduleFile(@Url url: String?): Call<ResponseBody?>?

    @get:GET(URLs.BASE_URI)
    val mainPage: Call<Document?>?
}