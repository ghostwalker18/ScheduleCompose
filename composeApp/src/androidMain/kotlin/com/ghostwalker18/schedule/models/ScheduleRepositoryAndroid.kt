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

package com.ghostwalker18.schedule.models

import com.russhwolf.settings.Settings
import com.ghostwalker18.schedule.database.AppDatabase
import com.ghostwalker18.schedule.network.ScheduleNetworkAPI
import org.apache.poi.ss.usermodel.Workbook
import java.util.concurrent.Callable
import kotlin.reflect.KFunction1

class ScheduleRepositoryAndroid(override val db: AppDatabase,
                                private val api: ScheduleNetworkAPI,
                                private val preferences: Settings
) : ScheduleRepository(db = db, api = api, preferences = preferences){
    override fun updateSchedule(
        linksGetter: Callable<List<String>>,
        parser: KFunction1<Workbook, List<Lesson>>
    ): UpdateResult {
        TODO("Not yet implemented")
    }

    override fun updateTimes(): UpdateResult {
        TODO("Not yet implemented")
    }
}