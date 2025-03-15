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

package com.ghostwalker18.schedule.activities

import android.app.UiModeManager
import android.appwidget.AppWidgetManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.ghostwalker18.schedule.R
import com.ghostwalker18.schedule.ScheduleApp.Companion.getInstance
import com.ghostwalker18.schedule.ScheduleWidget
import com.ghostwalker18.schedule.models.ScheduleRepository
import io.appmetrica.analytics.AppMetrica
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

/**
 * Этот класс представляет собой экран настроек виджета приложения
 *
 * @author  Ипатов Никита
 * @since 2.3
 * @see ScheduleWidget
 */
class WidgetSettingsActivity
    : AppCompatActivity(), View.OnClickListener, OnSharedPreferenceChangeListener {
    private var fragment: SettingsFragment? = null
    private var preferences: SharedPreferences? = null
    private var preview: ImageView? = null
    private var widgetID = AppWidgetManager.INVALID_APPWIDGET_ID
    private var resultValue: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_settings)
        val addButton = findViewById<Button>(R.id.add)
        addButton.setOnClickListener(this)

        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            widgetID = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }
        if (widgetID == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
        }

        fragment = SettingsFragment(widgetID)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, fragment!!)
                .commit()
        }
        preferences = getSharedPreferences("WIDGET_$widgetID", MODE_PRIVATE)
        preferences?.registerOnSharedPreferenceChangeListener(this)

        preview = findViewById(R.id.widget_preview)

        resultValue = Intent()
        resultValue!!.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetID)
        setResult(RESULT_CANCELED, resultValue)
    }

    override fun onClick(view: View) {
        setResult(RESULT_OK, resultValue)

        if (getInstance().isAppMetricaActivated) AppMetrica.reportEvent("Добавили виджет")

        val appWidgetManager = AppWidgetManager.getInstance(this)
        preferences!!.edit()
            .putBoolean("isEdited", true)
            .commit()
        ScheduleWidget.updateAppWidget(this, appWidgetManager, widgetID)
        finish()
    }

    override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String?) {
        var previewName = "widget"

        val isDynamicColors = preferences!!.getBoolean("dynamic_colors", false)
        if (isDynamicColors) previewName += "_dynamic"

        val theme = preferences!!.getString("theme", "system")!!
        when (theme) {
            "night" -> previewName += "_dark"
            "day" -> previewName += "_light"
            "system" -> {
                val uiModeManager = getSystemService(UI_MODE_SERVICE) as UiModeManager
                val currentNightMode = uiModeManager.nightMode
                previewName += when (currentNightMode) {
                    UiModeManager.MODE_NIGHT_YES -> "_dark"
                    else -> "_light"
                }
            }
        }

        val day = preferences!!.getString("day", "today")!!
        when (day) {
            "today" -> previewName += "_today"
            "tomorrow" -> previewName += "_tomorrow"
        }

        val imageId = resources.getIdentifier(previewName, "drawable", packageName)
        preview!!.setImageResource(imageId)
    }

    class SettingsFragment
        : PreferenceFragmentCompat, OnSharedPreferenceChangeListener {
        private val repository: ScheduleRepository = getInstance().scheduleRepository
        private var widgetId: Int = 0
        private var preferences: SharedPreferences? = null
        private var groupChoicePreference: ListPreference? = null
        private val scope = CoroutineScope(Dispatchers.Main)

        constructor(widgetId: Int) : super() {
            this.widgetId = widgetId
        }

        constructor() : super()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            if (savedInstanceState != null) widgetId = savedInstanceState.getInt("id")
        }

        override fun onSaveInstanceState(outState: Bundle) {
            outState.putInt("id", widgetId)
            super.onSaveInstanceState(outState)
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            repository.update()

            setPreferencesFromResource(R.xml.widget_preferences, rootKey)
            preferenceManager.sharedPreferencesName = "WIDGET_$widgetId"
            preferences = preferenceManager.sharedPreferences
            preferences!!.registerOnSharedPreferenceChangeListener(this)

            val dayChoicePreference = preferenceScreen.findPreference<ListPreference>("day")
            dayChoicePreference!!.value = "today"
            dayChoicePreference.summary = dayChoicePreference.entry

            val themeChoicePreference = preferenceScreen.findPreference<ListPreference>("theme")
            themeChoicePreference!!.value = "system"
            themeChoicePreference.summary = themeChoicePreference.entry

            groupChoicePreference = preferenceScreen.findPreference("group")
            /**
             * TODO: fix not attach to context bug
             */
            scope.launch {
                repository.groups.collect { groups ->
                    val groupsNew = groups.toMutableList()
                    groupsNew.sortWith(Comparator.naturalOrder())

                    groupsNew.add(0, getString(R.string.last_chosen))
                    groupChoicePreference!!.entries = groupsNew.toTypedArray()

                    groupsNew.removeAt(0)

                    groupsNew.add(0, "last")
                    groupChoicePreference!!.entryValues = groupsNew.toTypedArray()
                    groupChoicePreference!!.summary = groupChoicePreference!!.entry
                }
            }
        }

        override fun onSharedPreferenceChanged(sharedPreferences: SharedPreferences, s: String?) {
            val preference = preferenceScreen.findPreference<Preference>(s!!)
            if (preference is ListPreference) {
                preference.setSummary(preference.entry)
            }
        }
    }
}