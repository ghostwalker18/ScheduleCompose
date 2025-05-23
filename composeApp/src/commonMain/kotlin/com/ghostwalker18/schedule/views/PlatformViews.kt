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

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import java.util.Calendar
import org.jetbrains.compose.resources.StringResource

/**
 * Эта функция представляет собой элемент интерфейса, используемый для
 * отображения расписания занятий при портретной ориентации экрана.
 * @param date дата для показа расписания на неделю
 *
 * @author  Ипатов Никита
 * @since 1.0
 */
@Composable
expect fun DaysFragmentPortrait(
    date: Calendar = Calendar.getInstance()
)

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
 * Эта функция добавляет текстовую подсказку к элементу для десктопной версии приложения.
 * @param toolTip текст всплывающей подсказки
 * @param content контент для отображения подсказки
 *
 * @author Ипатов Никита
 */
@Composable
expect fun ContentWrapper(
    toolTip: StringResource,
    content: @Composable () -> Unit
)

/**
 * Эта функция отображает интерфейс приложения фотографий к заметке.
 *
 * @author Ипатов Никита
 * @since 5.0
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
expect fun AttachNotePhotoView(
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null
)

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
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
expect fun PhotoPreview(
    modifier: Modifier = Modifier,
    photoIDs: List<String> = listOf(),
    isEditable: Boolean = false,
    sharedTransitionScope: SharedTransitionScope? = null,
    animatedVisibilityScope: AnimatedVisibilityScope? = null,
    onDeleteListener: (id: String) -> Unit = {}
)

/**
 * Эта функция предназначена для отображения элемента управления голосового ввода текста
 * @param onInput callback, вызываемый для результата преобразования речи в текст
 *
 * @author Ипатов Никита
 */
@Composable
expect fun VoiceInput(
    onInput: (text: String) -> Unit
)

@Composable
expect fun AddReminder()