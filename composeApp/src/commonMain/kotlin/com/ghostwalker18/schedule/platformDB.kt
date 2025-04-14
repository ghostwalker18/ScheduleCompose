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

import androidx.room.RoomDatabase
import com.ghostwalker18.schedule.database.AppDatabase
import java.io.File

/**
 * Эта функция предоставляет доступ к построителю БД для текущей платформы.
 */
expect fun getDatabaseBuilder(dbName: String): RoomDatabase.Builder<AppDatabase>

/**
 * Эта функция модифицирует построителя БД в соответствии с возможностями и потребностями
 * текущей платформы
 */
expect fun getDecoratedDBBuilder(builder: RoomDatabase.Builder<AppDatabase>,
                                 file: File? = null): RoomDatabase.Builder<AppDatabase>

/**
 * Эта функция экспортирует файл БД приложения с указанным типом данных для экспорта
 */
expect suspend fun exportDBFile(dataType: String): File?

/**
 * Эта функция импортирует файл БД приложения с указанным типом данных для импорта
 * и указанной политикой импорта.
 */
expect suspend fun importDBFile(dbFile: File, dataType: String, importPolicy: String)

/**
 * Этот метод удаляет файл экспортной БД
 */
expect fun deleteExportDBFile()