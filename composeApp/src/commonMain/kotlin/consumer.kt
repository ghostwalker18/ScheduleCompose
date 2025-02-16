import database.AppDatabase
import models.ScheduleRepository

expect fun getScheduleRepository(): ScheduleRepository
expect fun getDatabase(): AppDatabase