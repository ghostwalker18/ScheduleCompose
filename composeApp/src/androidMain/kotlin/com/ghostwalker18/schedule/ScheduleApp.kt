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

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import com.ghostwalker18.schedule.database.AppDatabase
import com.ghostwalker18.schedule.models.NotesRepository
import com.ghostwalker18.schedule.models.ScheduleRepository
import com.ghostwalker18.schedule.models.ScheduleRepositoryAndroid
import com.ghostwalker18.schedule.network.NetworkService
import com.ghostwalker18.schedule.notifications.NotificationManagerWrapper
import com.ghostwalker18.schedule.notifications.ScheduleUpdateNotificationWorker
import com.ghostwalker18.schedule.platform.*
import com.ghostwalker18.schedule.utils.AndroidUtils
import com.google.android.material.color.DynamicColors
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.get
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import ru.rustore.sdk.pushclient.RuStorePushClient
import ru.rustore.sdk.pushclient.common.logger.DefaultLogger
import ru.rustore.sdk.universalpush.RuStoreUniversalPushClient
import ru.rustore.sdk.universalpush.firebase.provides.FirebasePushProvider
import ru.rustore.sdk.universalpush.rustore.providers.RuStorePushProvider
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * <h1>Schedule</h1>
 * <p>
 *      Программа представляет собой мобильную реализацию приложения расписания ПАСТ.
 * </p>
 *
 * @author  Ипатов Никита
 * @version  5.0
 */
@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class ScheduleApp : Application() {
    lateinit var _navigator: Navigator
    actual val navigator by lazy { _navigator }

    private lateinit var _importScreenController: ImportScreenController
    actual val importScreenController by lazy { _importScreenController }

    private lateinit var _shareController: ShareControllerAndroid
    actual val shareController by lazy { _shareController  as ShareController}

    private lateinit var _preferences: ObservableSettings
    actual val preferences by lazy { _preferences }

    private lateinit var localeChangedListener: SettingsListener
    private lateinit var themeChangedListener: SettingsListener
    private lateinit var scheduleUpdateChangedListener: SettingsListener
    private lateinit var appUpdateChangedListener: SettingsListener

    lateinit var database: AppDatabase
        private set

    lateinit var _notesRepository: NotesRepository
        private set
    actual val notesRepository by lazy { _notesRepository }

    lateinit var _scheduleRepository: ScheduleRepositoryAndroid
        private set
    actual val scheduleRepository by lazy { _scheduleRepository as ScheduleRepository }

    private lateinit var _themeState: MutableStateFlow<String>
    lateinit var themeState: StateFlow<String>
        private set

    internal var isAppMetricaActivated = false


    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        _instance = this

        _preferences = SharedPreferencesSettings(
            PreferenceManager.getDefaultSharedPreferences(this)
        )

        _themeState = MutableStateFlow(preferences["theme", "system"])
        themeState = _themeState
        themeChangedListener = preferences.addStringListener(
            "theme", "system"
        ){
            _themeState.value = it
        }

        localeChangedListener = preferences.addStringListener(
            "language", "ru"
        ){
            setLocale(it)
        }

        appUpdateChangedListener = preferences.addBooleanListener(
            "update_notifications", false
        ) {
            if (it) {
                RuStoreUniversalPushClient.subscribeToTopic("update_notifications")
            } else
                RuStoreUniversalPushClient.unsubscribeFromTopic("update_notifications")
        }

        scheduleUpdateChangedListener = preferences.addBooleanListener(
            "schedule_notifications", false
        ){
            if (it) {
                val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.UNMETERED)
                    .build()
                val request =
                    PeriodicWorkRequest.Builder(
                        workerClass = ScheduleUpdateNotificationWorker::class.java,
                        repeatInterval = 30,
                        repeatIntervalTimeUnit = TimeUnit.MINUTES
                    )
                        .addTag("update_schedule")
                        .setConstraints(constraints)
                        .build()
                WorkManager.getInstance(this).enqueue(request)
            } else {
                WorkManager.getInstance(this).cancelAllWorkByTag("update_schedule")
            }
        }

        database = AppDatabase.getInstance()
        _scheduleRepository = ScheduleRepositoryAndroid(
            this,
            database,
            NetworkService(this, URLs.BASE_URI, preferences).getScheduleAPI(),
            preferences
        )
        scheduleRepository.update()
        _notesRepository = NotesRepository(database)

        _shareController = ShareControllerAndroid(this)

        //Initializing of third-party analytics and push services.
        /*try {
            val appMetricaApiKey = getString(R.string.app_metrica_api_key) //from non-public strings
            val config: AppMetricaConfig = AppMetricaConfig.newConfigBuilder(appMetricaApiKey).build()
            // Initializing the AppMetrica SDK.
            AppMetrica.activate(this, config)
            isAppMetricaActivated = true
            FirebaseApp.initializeApp(this)
            // Initializing the RuStore Push SDK.
            initPushes()
        } catch (e: Exception) { /*Not required*/ }*/
        AndroidUtils.checkNotificationsPermissions(this, preferences)
        AndroidUtils.clearPOICache(this)
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

    actual companion object{
        private lateinit var _instance: ScheduleApp
        actual val instance by lazy { _instance }
    }
}