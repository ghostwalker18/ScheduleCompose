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

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ghostwalker18.schedule.database.AppDatabase
import com.ghostwalker18.schedule.database.AppDatabase.Companion.EXPORT_DATABASE_NAME
import com.ghostwalker18.schedule.database.AppDatabase.Companion.IMPORT_DATABASE_NAME
import com.ghostwalker18.schedule.database.AppDatabase.Companion.createAppDatabase
import com.ghostwalker18.schedule.database.AppDatabase.Companion.getInstance
import com.ghostwalker18.schedule.models.Lesson
import com.ghostwalker18.schedule.models.Note
import java.io.File
import java.util.stream.Collectors

actual fun getDatabaseBuilder(dbName: String): RoomDatabase.Builder<AppDatabase>{
    val context = ScheduleApp.instance as Context
    val dbFile = context.getDatabasePath(dbName)
    return Room.databaseBuilder<AppDatabase>(
        context = context,
        name = dbFile.absolutePath
    )
}

actual fun getDecoratedDBBuilder(builder: RoomDatabase.Builder<AppDatabase>, file: File?): RoomDatabase.Builder<AppDatabase>{
    return if(file != null){
        builder.createFromFile(file)
    }
    else
        builder
}

actual suspend fun exportDBFile(dataType: String): File? {
    val context = ScheduleApp.instance as Context
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
    return context.getDatabasePath(EXPORT_DATABASE_NAME)
}

actual suspend fun importDBFile(dbFile: File, dataType: String, importPolicy: String) {
    val importDB = createAppDatabase(IMPORT_DATABASE_NAME, dbFile)
    val instance = getInstance()
    val context = ScheduleApp.instance as Context
    if (dataType == "schedule" || dataType == "schedule_and_notes") {
        if (importPolicy == "replace") instance.lessonDao().deleteAllLessons()
        val lessons: List<Lesson> = importDB.lessonDao().getAllLessons()
        instance.lessonDao().insertMany(lessons)
    }
    if (dataType == "notes" || dataType == "schedule_and_notes") {
        if (importPolicy == "replace") instance.noteDao().deleteAllNotes()
        val notes: List<Note> = importDB.noteDao().getAllNotes()
        instance.noteDao().insertMany(
            notes
                .stream()
                .map(Note::copy)
                .collect(Collectors.toList())
        )
    }
    importDB.close()
    context.getDatabasePath(IMPORT_DATABASE_NAME).delete()
}

actual fun deleteExportDBFile(){
    val context = ScheduleApp.instance as Context
    val exportDBFile = context.getDatabasePath(EXPORT_DATABASE_NAME)
    if (exportDBFile.exists())
        exportDBFile.delete()
}