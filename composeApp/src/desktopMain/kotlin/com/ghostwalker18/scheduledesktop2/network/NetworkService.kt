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

package com.ghostwalker18.scheduledesktop2.network

import network.CacheInterceptor
import network.JsoupConverterFactory
import network.ScheduleNetworkAPI
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.io.File
import com.ghostwalker18.scheduledesktop2.ScheduleApp
import java.util.concurrent.Executors
import java.util.prefs.Preferences

/**
 * Этот класс используется для предоставления приложению услуг доступа к сети.
 *
 * @author Ipatov Nikita
 * @since 1.0
 */
class NetworkService(private val baseUri: String) {
    private val sizeOfCache: Long = 10 * 1024 * 1024
    private val preferences: Preferences = ScheduleApp.preferences


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
            try {
                val path = javaClass.getResource("/cache")?.path
                path?.let {
                    val cache = Cache(File(it), sizeOfCache)
                    val client = OkHttpClient().newBuilder()
                        .cache(cache)
                        .addInterceptor(CacheInterceptor())
                        .build()
                    apiBuilder.client(client)
                }
            } catch (e: Exception) {
                System.err.println("Cannot enable caching: " + e.message)
            }
        }

        return apiBuilder
            .build()
            .create(ScheduleNetworkAPI::class.java)
    }
}