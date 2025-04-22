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
import androidx.compose.ui.Modifier

@Composable
actual fun DaysFragmentPortrait() {/*Not required*/}

@Composable
actual fun TimesFragmentPortrait() {/*Not required*/}

@Composable
actual fun ShareAppScreenPortrait() {/*Not required*/}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
actual fun AttachNotePhotoView(
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?
){/*Not required*/}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
actual fun PhotoPreview(
    modifier: Modifier,
    photoIDs: List<String>,
    isEditable: Boolean,
    sharedTransitionScope: SharedTransitionScope?,
    animatedVisibilityScope: AnimatedVisibilityScope?,
    onDeleteListener: (id: String) -> Unit
) {/*Not required*/}

@Composable
actual fun SpeechInput(
    onInput: (text: String) -> Unit
) { /*Not required*/ }