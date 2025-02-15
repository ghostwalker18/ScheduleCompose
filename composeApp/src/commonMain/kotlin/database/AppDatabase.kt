package com.ghostwalker18.schedule.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.ghostwalker18.schedule.database.DataBaseMigrations.migrations
import kotlinx.coroutines.Dispatchers
import java.io.File


/**
 * Этот класс используется Room для генерации класса для ORM операций с БД приложения.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
private const val APP_DATABASE_NAME: String = "database.db"

@Database(entities = [Lesson::class, Note:: class], version = 5)
@TypeConverters(DateConverters::class)
abstract class AppDatabase : RoomDatabase(){
    abstract fun lessonDao() : LessonDao
    abstract fun noteDao() : NoteDao


    companion object{

        fun getInstance() : AppDatabase{
            val dbFile = File(System.getProperty("java.io.tmpdir"), APP_DATABASE_NAME)
            val callback = object : Callback(){
                override fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)
                    connection.execSQL(UPDATE_DAY_TRIGGER_1)
                    connection.execSQL(UPDATE_DAY_TRIGGER_2)
                }
            }
            val builder = Room.databaseBuilder<AppDatabase>(
                name = dbFile.absolutePath,
            )
                .addCallback(callback)
                .setJournalMode(JournalMode.TRUNCATE)
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
            for(migration in migrations)
                builder.addMigrations(migration)
            return builder.build()
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