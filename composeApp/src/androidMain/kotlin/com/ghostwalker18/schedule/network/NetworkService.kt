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

import android.content.Context
import com.russhwolf.settings.Settings
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.Executors

/**
 * Этот класс используется для предоставления приложению услуг доступа к сети.
 *
 * @author Ipatov Nikita
 * @since 3.1
 * @see CacheInterceptor
 */
class NetworkService(
    private val context: Context,
    private val baseUri: String,
    private val preferences: Settings
) {

    private val sizeOfCache: Long = (10 * 1024 * 1024 // 10 MiB
            ).toLong()
    /**
     * Этот метод позволяет получить API сайта ПТГХ.
     * @return API сайта для доступа к скачиванию файлов расписания
     */
    fun getScheduleAPI(): ScheduleNetworkAPI {
        val apiBuilder = Retrofit.Builder()
            .baseUrl(baseUri)
            .callbackExecutor(Executors.newFixedThreadPool(4))
            .addConverterFactory(JsoupConverterFactory())

        val isCachingEnabled = preferences.getBoolean("isCachingEnabled", true)
        if (isCachingEnabled) {
            val cache = Cache(File(context.cacheDir, "http"), sizeOfCache)
            val client = OkHttpClient().newBuilder()
                .cache(cache)
                .addInterceptor(CacheInterceptor())
                .build()
            apiBuilder.client(client)
        }

        return apiBuilder
            .build()
            .create(ScheduleNetworkAPI::class.java)
    }
}