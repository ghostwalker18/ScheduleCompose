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

package com.ghostwalker18.schedule.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.unit.TextUnit

/**
 * Это свойство задает общие настройки шрифтов мобильного и десктопного приложения.
 */
expect val Typography: Typography

/**
 * Это свойство задает размер шрифта для таблицы с расписанием
 * мобильного и десктопного приложения.
 */
expect val ScheduleTableFontSize: TextUnit