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

package com.ghostwalker18.schedule.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import com.ghostwalker18.schedule.converters.DateConverters
import com.ghostwalker18.schedule.converters.PhotoURIArrayConverter
import com.ghostwalker18.schedule.database.DataBaseMigrations.migrations
import com.ghostwalker18.schedule.getDatabaseBuilder
import com.ghostwalker18.schedule.getDecoratedDBBuilder
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.models.Note
import kotlinx.coroutines.Dispatchers
import java.io.File

/**
 * Этот класс используется Room для генерации класса для ORM операций с БД приложения.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Database(entities = [Lesson::class, Note:: class], version = 5, exportSchema = true)
@TypeConverters(DateConverters::class, PhotoURIArrayConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun lessonDao(): LessonDao

    abstract fun noteDao(): NoteDao

    suspend fun exportDBFile(dataType: String): File? {
        return com.ghostwalker18.schedule.exportDBFile(dataType)
    }

    suspend fun importDBFile(dbFile: File, dataType: String, importPolicy: String){
        com.ghostwalker18.schedule.importDBFile(dbFile, dataType, importPolicy)
    }

    fun deleteExportDBFile() = com.ghostwalker18.schedule.deleteExportDBFile()

    companion object {
        const val APP_DATABASE_NAME = "database.db"
        const val EXPORT_DATABASE_NAME = "export_database.db"
        const val IMPORT_DATABASE_NAME = "import_database.db"
        private var _instance: AppDatabase? = null
        /**
         * Это свойство позволяет получить сконфигурированную базу данных приложения по умолчанию.
         * @return база данных Room
         */
        val instance: AppDatabase
            get() {
                if (_instance == null) {
                    synchronized(AppDatabase::class.java) {
                        if (_instance == null) {
                            _instance = createAppDatabase(APP_DATABASE_NAME, null)
                        }
                    }
                }
                return _instance!!
            }

        fun createAppDatabase(dbName: String, dbFile: File? = null): AppDatabase{
            val callback = object : Callback() {
                override fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)
                    connection.execSQL(UPDATE_DAY_TRIGGER_1)
                    connection.execSQL(UPDATE_DAY_TRIGGER_2)
                }
            }
            val builder = getDatabaseBuilder(dbName)
                .addCallback(callback)
                .setQueryCoroutineContext(Dispatchers.Main)
                .setJournalMode(JournalMode.TRUNCATE)
            for (migration in migrations)
                builder.addMigrations(migration)
            val platformBuilder = getDecoratedDBBuilder(builder, dbFile)
            return platformBuilder.build()
        }

        const val UPDATE_DAY_TRIGGER_1 =
            "CREATE TRIGGER IF NOT EXISTS update_day_stage1 " +
                    "BEFORE INSERT ON tblSchedule " +
                    "BEGIN " +
                    "DELETE FROM tblSchedule WHERE groupName = NEW.groupName AND " +
                    "                lessonDate = NEW.lessonDate AND " +
                    "                lessonNumber = NEW.lessonNumber;" +
                    "END;"

        const val UPDATE_DAY_TRIGGER_2 =
            "CREATE TRIGGER IF NOT EXISTS update_day_stage2 " +
                    "AFTER INSERT ON tblSchedule " +
                    "BEGIN " +
                    "DELETE FROM tblSchedule WHERE subjectName = '';"+
                    "END;"
    }
}