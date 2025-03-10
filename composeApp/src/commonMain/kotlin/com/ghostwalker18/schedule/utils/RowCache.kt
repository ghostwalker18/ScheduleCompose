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

package com.ghostwalker18.schedule.utils

import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.jetbrains.annotations.Contract

/**
 * Этот класс служит для реализации буферизированного псевдорандомного доступа
 * к строкам эксель-файла при использовании потокового чтения
 * файла без полной его загрузки в память.
 * **Важное ограничение: нельзя возвращаться назад.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
class RowCache private constructor(sheet: Sheet, private val size: Int) {
    private val iterator: Iterator<Row>
    private var lowBoundary = 0
    private var rows: Array<Row?>
    private val oldRows: Array<Row?>

    init {
        rows = arrayOfNulls(size)
        oldRows = arrayOfNulls(size)
        iterator = sheet.rowIterator()
        load()
    }

    /**
     * Этот метод служит для получения строки листа
     * @param row номер строки
     * @return строка
     * @throws IndexOutOfBoundsException если кэш для ряда уже недоступен
     */
    @Throws(IndexOutOfBoundsException::class)
    fun getRow(row: Int): Row? {
        if (row <= lowBoundary - size || row < 0) throw IndexOutOfBoundsException()
        if (row < lowBoundary + size) {
            return if (row > lowBoundary - size && row < lowBoundary) oldRows[size - (lowBoundary - row)]
            else rows[row - lowBoundary]
        } else {
            lowBoundary += size
            load()
            return getRow(row)
        }
    }

    /**
     * Этот метод загружает новые данные в кэш.
     */
    private fun load() {
        if (size >= 0) System.arraycopy(rows, 0, oldRows, 0, size)
        rows = arrayOfNulls(size)
        for (i in 0..<size) {
            if (iterator.hasNext())
                rows[i] = iterator.next()
        }
    }

    /**
     * Этот класс служит для построения объекта кэша.
     *
     * @author Ипатов Никита
     */
    class Builder {
        private var size = 10
        private var sheet: Sheet? = null

        /**
         * Этот метод задает размер кэша.
         * @param size размер кэша в строках
         * @return объект строителя
         */
        fun setSize(size: Int): Builder {
            this.size = size
            return this
        }

        /**
         * Этот метод задает лист для кэша.
         * @param sheet лист
         * @return объект строителя
         */
        fun setSheet(sheet: Sheet?): Builder {
            this.sheet = sheet
            return this
        }

        /**
         * Этот метод строит объект кэша.
         * @return кэш
         */
        fun build(): RowCache? {
            return sheet?.let { RowCache(it, size) }
        }
    }

    companion object {
        /**
         * Этот метод используется для получения построителя кэша.
         * @return строитель
         */
        @Contract(" -> new")
        fun builder(): Builder {
            return Builder()
        }
    }
}