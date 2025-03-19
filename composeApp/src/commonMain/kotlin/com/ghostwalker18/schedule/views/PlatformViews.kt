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

package com.ghostwalker18.schedule.views

import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier

/**
 * Эта функция представляет собой элемент интерфейса, используемый для
 * отображения расписания занятий при портретной ориентации экрана.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Composable
expect fun DaysFragmentPortrait()

/**
 * Эта функция представляет собой элемент интерфейса для отображения
 * расписания звонков при портретной ориентации экрана.
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Composable
expect fun TimesFragmentPortrait()

/**
 * Эта функция представляет собой экран приложения при портретной ориентации экрана
 * для возможности поделиться им.
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
expect fun ShareAppScreenPortrait()

/**
 * Эта функция позволяет открыть диалог для скачивания файлов расписания.
 * @param isEnabled отображать ли окно на экране
 * @param title заголовок окна
 * @param links ссылки на файлы для скачивания
 * @param mimeType MIME тип файлов
 *
 * @author Ипатов Никита
 * @since 1.0
 */
@Composable
expect fun DownloadDialog(
    isEnabled: MutableState<Boolean>,
    title: String,
    links: Array<String>,
    mimeType: String
)

/**
 * Эта функция отображает интерфейс приложения фотографий к заметке.
 *
 * @author Ипатов Никита
 * @since 5.0
 */
@Composable
expect fun AttachNotePhotoView()

/**
 * Эта функция предназначена для отображения фотографий, прикладываемых к заметкам.
 * @param modifier визуальный модификатор
 * @param photoIDs URI фотографий заметки
 * @param isEditable доступно ли удаление фотографий
 * @param onDeleteListener callback, вызываемый при удалении фотографии
 *
 * @author Ипатов Никита
 * @since 5.0
 */
@Composable
expect fun PhotoPreview(
    modifier: Modifier = Modifier,
    photoIDs: List<String> = listOf(),
    isEditable: Boolean = false,
    onDeleteListener: (id: String) -> Unit = {}
)