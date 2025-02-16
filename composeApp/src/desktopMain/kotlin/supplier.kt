import com.ghostwalker18.scheduledesktop2.ScheduleApp
import database.AppDatabase
import models.ScheduleRepository

actual fun getScheduleRepository(): ScheduleRepository = ScheduleApp.getInstance().getScheduleRepository()
actual fun getDatabase(): AppDatabase = ScheduleApp.getInstance().getDatabase()