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

import MainScreenController
import Navigator
import NotesScreenController
import SettingsActivityController
import ShareScreenController
import URLs
import android.app.Application
import androidx.preference.PreferenceManager
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import com.ghostwalker18.schedule.models.ScheduleRepositoryAndroid
import com.ghostwalker18.schedule.network.NetworkService
import com.ghostwalker18.schedule.notifications.NotificationManagerWrapper
import com.ghostwalker18.schedule.platform.*
import com.google.android.material.color.DynamicColors
import com.google.firebase.FirebaseApp
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.get
import database.AppDatabase
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import models.NotesRepository
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
class ScheduleApp : Application() {
    lateinit var navigator: Navigator
    lateinit var mainActivityController: MainScreenController
    lateinit var notesActivityController: NotesScreenController
    lateinit var shareActivityController: ShareScreenController
    lateinit var settingsActivityController: SettingsActivityController
    lateinit var preferences: ObservableSettings
    private lateinit var localeChangedListener: SettingsListener
    private lateinit var themeChangedListener: SettingsListener
    lateinit var database: AppDatabase
    lateinit var notesRepository: NotesRepository
    lateinit var scheduleRepository: ScheduleRepositoryAndroid
    internal var isAppMetricaActivated = false


    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)
        instance = this
        database = AppDatabase.getInstance()
        preferences = SharedPreferencesSettings(
            PreferenceManager.getDefaultSharedPreferences(this)
        )
        localeChangedListener = preferences.addStringListener("language", "ru"){
            setLocale(it)
        }
        themeChangedListener = preferences.addStringListener("theme", "system"){
            setTheme(it)
        }
        scheduleRepository = ScheduleRepositoryAndroid(
            database,
            NetworkService(this, URLs.BASE_URI, preferences).getScheduleAPI(),
            preferences
        )
        scheduleRepository.update()
        notesRepository = NotesRepository(database)
        val theme = preferences["theme", "system"]
        setTheme(theme)
        navigator = NavigatorAndroid(this)
        mainActivityController = MainScreenControllerAndroid(this)
        notesActivityController = NotesScreenControllerAndroid(this)
        shareActivityController = ShareScreenControllerAndroid(this)
        settingsActivityController = SettingsScreenControllerAndroid(this)

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

    /**
     * Этот метод позволяет установить язык приложения
     * @param localeCode код языка
     */
    private fun setLocale(localeCode: String) {
        val localeListCompat = if (localeCode == "system") LocaleListCompat.getEmptyLocaleList()
        else LocaleListCompat.create(Locale(localeCode))
        AppCompatDelegate.setApplicationLocales(localeListCompat)
    }

    /**
     * Этот метод позволяет установить тему приложения
     * @param theme код темы (system, day, night)
     */
    private fun setTheme(theme: String) {
        when (theme) {
            "system" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            "night" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "day" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    companion object{
        private lateinit var instance: ScheduleApp
        fun getInstance() = instance
    }
}