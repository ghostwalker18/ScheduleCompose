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

package com.ghostwalker18.schedule.converters

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

/**
 * Этот класс используется для ORM.
 * Содержит методы для преобразования ArrayList of Uri в String для БД и наоборот
 *
 * @author  Ипатов Никита
 * @since 4.0
 */
class PhotoURIArrayConverter {

    /**
     * Этот метод преобразует List of String сущности в String для БД.
     *
     * @param photoIDs  the entity attribute value to be converted
     * @return converted data
     */
    @TypeConverter
    fun toString(photoIDs: List<String>): String{
        return Json.encodeToString(photoIDs)
    }

    /**
     * Этот метод преобразует String из БД в List of String сущности.
     *
     * @param idsString  the data from the database column to be converted
     * @return converted data
     */
    @TypeConverter
    fun fromString(idsString: String): List<String>{
        return Json.decodeFromString<List<String>>(idsString)
    }
}