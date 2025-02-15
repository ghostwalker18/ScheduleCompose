package network

import okhttp3.ResponseBody
import org.jsoup.nodes.Document
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Url

/**
 * Интерфейс для создания Retrofit2 API,
 * используемого при скачивании файлов расписания и звонков.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */

const val BASE_URI = "https://ptgh.onego.ru/9006/"
const val MONDAY_TIMES_URL =
    "https://r1.nubex.ru/s1748-17b/47698615b7_fit-in~1280x800~filters:no_upscale()__f44488_08.jpg"
const val OTHER_TIMES_URL =
    "https://r1.nubex.ru/s1748-17b/320e9d2d69_fit-in~1280x800~filters:no_upscale()__f44489_bb.jpg"

interface ScheduleNetworkAPI {
    @get:GET(MONDAY_TIMES_URL)
    val mondayTimes: Call<ResponseBody?>?

    @get:GET(OTHER_TIMES_URL)
    val otherTimes: Call<ResponseBody?>?

    /**
     * Получение файла расписания по заданному URL.
     *
     * @return асинхронный ответ сервера
     */
    @GET
    fun getScheduleFile(@Url url: String?): Call<ResponseBody?>?

    @get:GET(BASE_URI)
    val mainPage: Call<Document?>?
}