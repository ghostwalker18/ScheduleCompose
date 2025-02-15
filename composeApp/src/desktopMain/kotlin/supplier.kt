import com.ghostwalker18.scheduledesktop2.ScheduleApp
import models.IScheduleRepository

actual fun getScheduleRepository(): IScheduleRepository = ScheduleApp.getInstance().getScheduleRepository()