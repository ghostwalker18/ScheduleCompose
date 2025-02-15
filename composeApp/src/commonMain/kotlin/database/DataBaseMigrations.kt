package com.ghostwalker18.schedule.database

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL
import java.util.*


/**
 * Этот класс используется для проведения миграций между версиями БД приложения.
 *
 * @author Ипатов Никита
 * @since 4.0
 */
object DataBaseMigrations {
    private val MIGRATION_1_2: Migration = object : Migration(1, 2) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                ("CREATE TABLE IF NOT EXISTS tblNote ( 'noteGroup' TEXT NOT NULL, " +
                        "'noteTheme' TEXT, 'noteText' TEXT NOT NULL, 'notePhotoID' TEXT, " +
                        "'id' INTEGER NOT NULL, 'noteDate' TEXT NOT NULL, PRIMARY KEY(`id`))")
            )
        }
    }
    private val MIGRATION_2_3: Migration = object : Migration(2, 3) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL("DROP TRIGGER IF EXISTS update_day_stage1")
            connection.execSQL(AppDatabase.UPDATE_DAY_TRIGGER_1)
        }
    }
    private val MIGRATION_3_4: Migration = object : Migration(3, 4) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                "UPDATE tblNote " +
                        "SET notePhotoID = '[\"' || notePhotoID || '\"]'"
            )
            connection.execSQL(
                "ALTER TABLE tblNote " +
                        "RENAME COLUMN notePhotoID TO notePhotoIDs"
            )
        }
    }
    private val MIGRATION_4_5: Migration = object : Migration(4, 5) {
        override fun migrate(connection: SQLiteConnection) {
            connection.execSQL(
                ("UPDATE tblSchedule " +
                        "SET lessonDate = " +
                        "    SUBSTR(lessonDate, 7, 4) || '-' || " +
                        "    SUBSTR(lessonDate, 4, 2) || '-' || " +
                        "    SUBSTR(lessonDate, 1, 2)")
            )
        }
    }
    val migrations: Collection<Migration>
        /**
         * Этот метод возвращает список всех миграций БД приложения
         * @return миграции приложения между версиями БД
         */
        get() = Arrays.asList(
            MIGRATION_1_2,
            MIGRATION_2_3,
            MIGRATION_3_4,
            MIGRATION_4_5
        )
}