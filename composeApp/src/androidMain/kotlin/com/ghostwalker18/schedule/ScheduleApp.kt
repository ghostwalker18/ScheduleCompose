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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
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
import com.google.firebase.FirebaseApp
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.SharedPreferencesSettings
import com.russhwolf.settings.get
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import java.util.concurrent.TimeUnit
import android.Manifest
import android.content.pm.PackageManager
import android.os.StrictMode
import android.util.Log
import ru.rustore.sdk.pushclient.common.logger.DefaultLogger
import io.appmetrica.analytics.AppMetrica
import io.appmetrica.analytics.AppMetricaConfig
import kotlinx.coroutines.launch
import ru.rustore.sdk.pushclient.RuStorePushClient
import ru.rustore.sdk.universalpush.RuStoreUniversalPushClient
import ru.rustore.sdk.universalpush.firebase.provides.FirebasePushProvider
import ru.rustore.sdk.universalpush.rustore.providers.RuStorePushProvider

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
    private lateinit var _navigator: Navigator

    private lateinit var _importController: ImportController
    actual val importController by lazy { _importController }

    private lateinit var _shareController: ShareController
    actual val shareController by lazy { _shareController }

    private lateinit var _preferences: ObservableSettings
    actual val preferences by lazy { _preferences }

    private lateinit var localeChangedListener: SettingsListener
    private lateinit var themeChangedListener: SettingsListener
    private lateinit var scheduleUpdateChangedListener: SettingsListener
    private lateinit var appUpdateChangedListener: SettingsListener

    lateinit var database: AppDatabase
        private set

    private lateinit var _notesRepository: NotesRepository
    actual val notesRepository by lazy { _notesRepository }

    private lateinit var _scheduleRepository: ScheduleRepository
    actual val scheduleRepository by lazy { _scheduleRepository }

    private lateinit var _themeState: MutableStateFlow<String>
    lateinit var themeState: StateFlow<String>
        private set

    internal var isAppMetricaActivated = false

    private val scope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        DynamicColors.applyToActivitiesIfAvailable(this)

        /*Used for checking performance issues*/
        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .detectAll()
                .penaltyLog()
                .build()
        )

        _instance = this

        _preferences = SharedPreferencesSettings(
            PreferenceManager.getDefaultSharedPreferences(this)
        )

        _themeState = MutableStateFlow(preferences[
            ScheduleAppSettings.AppSettings.Theme.key,
            ScheduleAppSettings.AppSettings.Theme.defaultValue
        ])
        themeState = _themeState
        themeChangedListener = preferences.addStringListener(
            ScheduleAppSettings.AppSettings.Theme.key,
            ScheduleAppSettings.AppSettings.Theme.defaultValue
        ){
            _themeState.value = it
        }

        localeChangedListener = preferences.addStringListener(
            ScheduleAppSettings.AppSettings.Language.key,
            ScheduleAppSettings.AppSettings.Language.defaultValue
        ){
            setLocale(it)
        }

        appUpdateChangedListener = preferences.addBooleanListener(
            ScheduleAppSettings.NotificationSettings.UpdateNotifications.key,
            ScheduleAppSettings.NotificationSettings.UpdateNotifications.defaultValue
        ) {
            if (it) {
                RuStoreUniversalPushClient.subscribeToTopic("update_notifications")
            } else
                RuStoreUniversalPushClient.unsubscribeFromTopic("update_notifications")
        }

        scheduleUpdateChangedListener = preferences.addBooleanListener(
            ScheduleAppSettings.NotificationSettings.ScheduleNotifications.key,
            ScheduleAppSettings.NotificationSettings.ScheduleNotifications.defaultValue
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

        database = AppDatabase.instance
        _scheduleRepository = ScheduleRepositoryAndroid(
            this,
            database,
            NetworkService(this, URLs.BASE_URI, preferences).getScheduleAPI(),
            preferences
        )
        scheduleRepository.update()
        _notesRepository = NotesRepository(database)

        _shareController = ShareControllerAndroid(this)
        _importController = ImportControllerAndroid(this)

        //Initializing of third-party analytics and push services.
        try {
            FirebaseApp.initializeApp(this)
            val appMetricaApiKey = getString(R.string.app_metrica_api_key) //from non-public strings
            val config = AppMetricaConfig.newConfigBuilder(appMetricaApiKey).build()
            // Initializing the AppMetrica SDK.
            AppMetrica.activate(this, config)
            isAppMetricaActivated = true
            // Initializing the RuStore Push SDK.
            initPushes()
        } catch (_: Exception) { /*Not required*/ }
        AndroidUtils.checkNotificationsPermissions(this, preferences)
        AndroidUtils.clearPOICache(this)
        scope.launch {
            if(ActivityCompat.checkSelfPermission(
                    this@ScheduleApp,
                    Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
            ){
                SpeechRecognizer.initModel(this@ScheduleApp)
            }
        }
    }

    actual fun getNavigator(): Navigator{
        return _navigator
    }

    /**
     * Этот метод позволяет задать навигатор приложения.
     */
    fun setNavigator(navigator: Navigator){
        _navigator = navigator
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
        NotificationManagerWrapper.getInstance(this).createNotificationChannel(
            getString(R.string.notifications_notification_note_reminder_channel_id),
            getString(R.string.notifications_notification_note_reminder_channel_name),
            getString(R.string.notifications_notification_note_reminder_channel_descr)
        )
        if (preferences.getBoolean(
                ScheduleAppSettings.NotificationSettings.UpdateNotifications.key,
                ScheduleAppSettings.NotificationSettings.UpdateNotifications.defaultValue
        )) RuStorePushClient.subscribeToTopic("update_notificatons")
    }

    /**
     * Этот метод позволяет установить язык приложения
     * @param localeCode код языка
     */
    private fun setLocale(localeCode: String) {
        val localeListCompat = if (localeCode == "system") LocaleListCompat.getEmptyLocaleList()

        else LocaleListCompat.create(
            Locale.Builder()
                .setLanguageTag(localeCode)
                .build()
        )
        AppCompatDelegate.setApplicationLocales(localeListCompat)
    }

    actual companion object{
        private lateinit var _instance: ScheduleApp
        actual val instance by lazy { _instance }
    }
}