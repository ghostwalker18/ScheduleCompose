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

import androidx.room.Room
import androidx.room.RoomDatabase
import com.ghostwalker18.schedule.database.AppDatabase
import com.ghostwalker18.schedule.database.AppDatabase.Companion.EXPORT_DATABASE_NAME
import com.ghostwalker18.schedule.database.AppDatabase.Companion.createAppDatabase
import com.ghostwalker18.schedule.database.AppDatabase.Companion.getInstance
import com.ghostwalker18.schedule.platform.OsUtils
import java.io.File

fun getDatabaseDir(): File{
    val userDirectory = File(System.getProperty("user.home"))
    val currentOS = OsUtils.hostOS
    return when(currentOS){
        OsUtils.OSType.Windows -> File(userDirectory, "AppData/Local/SchedulePCCE/database")
        OsUtils.OSType.MacOS -> TODO()
        OsUtils.OSType.Linux -> File(userDirectory, "SchedulePCCE/database")
        OsUtils.OSType.Unknown -> TODO()
    }
}

actual fun getDatabaseBuilder(dbName: String): RoomDatabase.Builder<AppDatabase> {
    val dbFile = File(getDatabaseDir(), dbName)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}

actual fun getDecoratedDBBuilder(builder: RoomDatabase.Builder<AppDatabase>,
                                 file: File?): RoomDatabase.Builder<AppDatabase> {
    return builder
}

actual suspend fun exportDBFile(dataType: String): File? {
    val exportDB = createAppDatabase(EXPORT_DATABASE_NAME, null)
    val instance = getInstance()
    exportDB.lessonDao().deleteAllLessons()
    exportDB.noteDao().deleteAllNotes()
    if (dataType == "schedule" || dataType == "schedule_and_notes") {
        val lessons = instance.lessonDao().getAllLessons()
        exportDB.lessonDao().insertMany(lessons)
    }
    if (dataType == "notes" || dataType == "schedule_and_notes") {
        val notes = instance.noteDao().getAllNotes()
        exportDB.noteDao().insertMany(notes)
    }
    exportDB.close()
    val exportDBFile = File(getDatabaseDir(), EXPORT_DATABASE_NAME)
    return exportDBFile
}

actual suspend fun importDBFile(dbFile: File,
                                dataType: String,
                                importPolicy: String) {/*Cannot be implemented yet*/}

actual fun deleteExportDBFile(){
    val exportDBFile = File(getDatabaseDir(), EXPORT_DATABASE_NAME)
    if (exportDBFile.exists())
        exportDBFile.delete()
}