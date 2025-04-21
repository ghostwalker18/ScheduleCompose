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
import com.ghostwalker18.schedule.platform.OsUtils
import java.io.File

actual fun getDatabaseBuilder(dbName: String): RoomDatabase.Builder<AppDatabase> {
    val userDirectory = File(System.getProperty("user.home"))
    val currentOS = OsUtils.hostOS
    val databaseDir = when(currentOS){
        OsUtils.OSType.Windows -> File(userDirectory, "AppData/Local/SchedulePCCE/database")
        OsUtils.OSType.MacOS -> TODO()
        OsUtils.OSType.Linux -> File(userDirectory, "SchedulePCCE/database")
        OsUtils.OSType.Unknown -> TODO()
    }
    val dbFile = File(databaseDir, AppDatabase.APP_DATABASE_NAME)
    return Room.databaseBuilder<AppDatabase>(
        name = dbFile.absolutePath,
    )
}

actual fun getDecoratedDBBuilder(builder: RoomDatabase.Builder<AppDatabase>,
                                 file: File?): RoomDatabase.Builder<AppDatabase> {
    return builder
}

actual suspend fun exportDBFile(dataType: String): File? {
    return null
}

actual suspend fun importDBFile(dbFile: File,
                                dataType: String,
                                importPolicy: String) {/*Cannot be implemented yet*/}

actual fun deleteExportDBFile(){/*Cannot be implemented yet*/}