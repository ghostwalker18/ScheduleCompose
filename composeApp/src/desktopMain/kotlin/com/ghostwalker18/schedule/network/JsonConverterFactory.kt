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

package com.ghostwalker18.schedule.network

import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import okhttp3.ResponseBody
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type

/**
 * Этот класс используется для преобразования тела ответа Retrofit в Json.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class JsonConverterFactory : Converter.Factory() {

    override fun responseBodyConverter(
        type: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): Converter<ResponseBody, *>? {
        if (type === JsonObject::class.java) {
            return JsonConverter()
        }
        return null
    }

    class JsonConverter : Converter<ResponseBody, JsonObject>{

        override fun convert(value: ResponseBody): JsonObject {
            return Json.parseToJsonElement(value.bytes().toString(Charsets.UTF_8)).jsonObject
        }
    }
}