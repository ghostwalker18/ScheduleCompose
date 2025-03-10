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

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.views.ShareAppActivity
import com.ghostwalker18.schedule.views.ShareScreenPortrait
import io.appmetrica.analytics.AppMetrica

class ShareAppActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            if(resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                ShareAppActivity()
            else
                ShareScreenPortrait()
        }
        if(ScheduleApp.getInstance().isAppMetricaActivated)
            AppMetrica.reportEvent("Поделились приложением");
    }
}