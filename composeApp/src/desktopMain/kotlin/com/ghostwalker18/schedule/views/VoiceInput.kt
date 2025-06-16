/*
 * Copyright 2025 Ipatov Nikita
 *
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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ghostwalker18.schedule.SpeechRecognizer
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.voice_input_descr

@Composable
actual fun VoiceInput(
    onInput: (text: String) -> Unit
) {
    var isRecording by remember { mutableStateOf(false) }
    val recognizer = remember{ return@remember SpeechRecognizer() }
    val infiniteTransition = rememberInfiniteTransition("recording")
    val animatedColor by infiniteTransition.animateColor(
        initialValue = IconButtonDefaults.iconButtonColors().containerColor,
        targetValue = MaterialTheme.colors.primary,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "color"
    )
    val scope = rememberCoroutineScope()

    IconButton(
        modifier = Modifier
            .background(
                color =
                    if(isRecording)
                        animatedColor
                    else
                        IconButtonDefaults.iconButtonColors().containerColor,
                shape = CircleShape
            ),
        enabled = true,
        onClick = {
            if(!isRecording){
                isRecording = true
                recognizer.startRecognition()
            } else {
                isRecording = false
                scope.launch {
                    onInput(recognizer.getResult())
                }
            }
        }
    ){
        Box(
            contentAlignment = Alignment.Center
        ){
            AnimatedVisibility(
                enter = fadeIn(),
                exit = fadeOut(),
                visible = isRecording
            ){
                CircularProgressIndicator(
                    color = MaterialTheme.colors.primaryVariant,
                    trackColor = animatedColor
                )
            }
            Icon(
                imageVector = Icons.Filled.Mic,
                contentDescription = stringResource(Res.string.voice_input_descr)
            )
        }
    }
}