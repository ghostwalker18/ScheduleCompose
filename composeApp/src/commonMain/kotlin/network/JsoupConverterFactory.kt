package network

import okhttp3.ResponseBody
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.parser.Parser
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.lang.reflect.Type


/**
 * Этот класс используется для преобразования тела ответа Retrofit в Document библиотеки Jsoup.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class JsoupConverterFactory

    : Converter.Factory() {
    override fun responseBodyConverter(
        type: Type,
        annotations: Array<Annotation?>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type === Document::class.java) {
            return JsoupConverter(retrofit.baseUrl().toString())
        }
        return null
    }

    private class JsoupConverter
        (private val baseUri: String) : Converter<ResponseBody, Document> {
        @Throws(IOException::class)
        override fun convert(value: ResponseBody): Document {
            val parser: Parser = Parser.htmlParser()
            return Jsoup.parse(value.byteStream(), "UTF-8", baseUri, parser)
        }
    }
}