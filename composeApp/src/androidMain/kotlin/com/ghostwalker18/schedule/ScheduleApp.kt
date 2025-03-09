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

package com.ghostwalker18.schedule

import android.R
import android.app.Application
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ghostwalker18.schedule.models.ScheduleRepositoryAndroid
import com.ghostwalker18.schedule.network.NetworkService
import com.ghostwalker18.schedule.notifications.NotificationManagerWrapper
import com.ghostwalker18.schedule.platform.MainScreenControllerAndroid
import com.ghostwalker18.schedule.platform.NotesScreenControllerAndroid
import com.ghostwalker18.schedule.platform.SettingsScreenControllerAndroid
import com.ghostwalker18.schedule.platform.ShareScreenControllerAndroid
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import database.AppDatabase
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import models.NotesRepository
import models.ScheduleRepository
import ru.rustore.sdk.pushclient.RuStorePushClient
import ru.rustore.sdk.pushclient.common.logger.DefaultLogger
import ru.rustore.sdk.universalpush.RuStoreUniversalPushClient
import ru.rustore.sdk.universalpush.firebase.provides.FirebasePushProvider
import ru.rustore.sdk.universalpush.rustore.providers.RuStorePushProvider
import java.util.*

/**
 * <h1>Schedule</h1>
 * <p>
 *      Программа представляет собой мобильную реализацию приложения расписания ПАСТ.
 * </p>
 *
 * @author  Ипатов Никита
 * @version  5.0
 */
class ScheduleApp : Application(), SharedPreferences.OnSharedPreferenceChangeListener {
    lateinit var mainActivityController: MainScreenControllerAndroid
    lateinit var notesActivityController: NotesScreenControllerAndroid
    lateinit var shareActivityController: ShareScreenControllerAndroid
    lateinit var settingsActivityController: SettingsScreenControllerAndroid
    lateinit var preferences: SharedPreferences
    lateinit var database: AppDatabase
    private lateinit var notesRepository: NotesRepository
    private lateinit var scheduleRepository: ScheduleRepositoryAndroid
    private var isAppMetricaActivated = false


    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        instance = this
        database = AppDatabase.getInstance(this)
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        scheduleRepository = ScheduleRepositoryAndroid(
            database,
            NetworkService(this, ScheduleRepository.BASE_URI, preferences).getScheduleAPI(),

        )
        scheduleRepository.update()
        notesRepository = NotesRepository(database)
        val theme = preferences.getString("theme", "")
        //setTheme(theme)
        preferences.registerOnSharedPreferenceChangeListener(this)


        //Initializing of third-party analytics and push services.
        try {
            val appMetricaApiKey = getString(R.string.app_metrica_api_key) //from non-public strings
            val config: AppMetricaConfig = AppMetricaConfig.newConfigBuilder(appMetricaApiKey).build()
            // Initializing the AppMetrica SDK.
            AppMetrica.activate(this, config)
            isAppMetricaActivated = true
            FirebaseApp.initializeApp(this)
            // Initializing the RuStore Push SDK.
            initPushes()
        } catch (e: Exception) { /*Not required*/
        }
    }

    /**
     * Этот метод используется для инициализации доставки Push-уведомлений RuStore и Firebase.
     */
    private fun initPushes() {
        RuStoreUniversalPushClient.init(
            this,
            RuStorePushProvider(
                this,
                getString(R.string.rustore_api_key),  //from non-public strings
                DefaultLogger()
            ),
            FirebasePushProvider(this),
            null
        )
        RuStoreUniversalPushClient.getTokens()
            .addOnSuccessListener { result ->
                Log.w(
                    "AppPushes", "getToken onSuccess = $result"
                )
            }
            .addOnFailureListener { throwable ->
                Log.e(
                    "AppPushes", "getToken onFailure", throwable
                )
            }

        //Do not forget to add same calls in NotificationLocaleUpdater for locale changes updates
        NotificationManagerWrapper.getInstance(this).createNotificationChannel(
            getString(R.string.notifications_notification_app_update_channel_id),
            getString(R.string.notifications_notification_app_update_channel_name),
            getString(R.string.notifications_notification_app_update_channel_descr)
        )
        NotificationManagerWrapper.getInstance(this).createNotificationChannel(
            getString(R.string.notifications_notification_schedule_update_channel_id),
            getString(R.string.notifications_notification_schedule_update_channel_name),
            getString(R.string.notifications_notification_schedule_update_channel_descr)
        )
        if (preferences.getBoolean("update_notifications", false)) RuStorePushClient.subscribeToTopic("update_notificatons")
        if (preferences.getBoolean(
                "schedule_notifications",
                false
            )
        ) RuStorePushClient.subscribeToTopic("schedule_notifications")
    }

    override fun onSharedPreferenceChanged(p0: SharedPreferences?, p1: String?) {
        TODO("Not yet implemented")
    }

    /**
     * Этот метод позволяет установить язык приложения
     * @param localeCode код языка
     */
    private fun setLocale(localeCode: String) {
        val localeListCompat = if (localeCode == "system") LocaleListCompat.getEmptyLocaleList()
        else LocaleListCompat.create(Locale(localeCode))
        AppCompatDelegate.setApplicationLocales(localeListCompat)
    }

    companion object{
        private lateinit var instance: ScheduleApp
        fun getInstance() = instance
    }
}