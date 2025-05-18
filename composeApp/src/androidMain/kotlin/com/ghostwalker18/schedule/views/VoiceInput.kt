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

import android.Manifest
import android.app.Activity
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat.shouldShowRequestPermissionRationale
import com.ghostwalker18.schedule.ScheduleApp
import com.ghostwalker18.schedule.SpeechRecognizer
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import scheduledesktop2.composeapp.generated.resources.Res
import scheduledesktop2.composeapp.generated.resources.mic_permission_required
import scheduledesktop2.composeapp.generated.resources.voice_input_descr

/**
 * Эта функция используется для отображения элемента голосового ввода текста.
 * @param onInput callback, вызываемый при распознавании голосового ввода
 *
 * @author Ипатов Никита
 */
@Composable
actual fun VoiceInput(
    onInput: (text: String) -> Unit
){
    val scope = rememberCoroutineScope()
    var isRecording by remember { mutableStateOf(false) }
    var hasPermissionRecordAudio by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var isInputEnabled by remember { mutableStateOf(false) }
    var recognizer: SpeechRecognizer? = null
    if(hasPermissionRecordAudio){
        recognizer = remember{ return@remember SpeechRecognizer(
            ScheduleApp.instance as Context
        )}
        scope.launch {
            recognizer.isReady.collect{
                isInputEnabled = it
            }
        }
    }
    val audioRecordPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) {
        hasPermissionRecordAudio = it
    }
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

    IconButton(
        modifier = Modifier
            .background(
                color = if(isRecording && hasPermissionRecordAudio)
                    animatedColor
                else
                    IconButtonDefaults.iconButtonColors().containerColor,
                shape = CircleShape
            ),
        enabled = true,
        onClick = {
            if(!isRecording){
                if (shouldShowRequestPermissionRationale(
                        context as Activity,
                        Manifest.permission.RECORD_AUDIO)
                ) {
                    val toast = Toast.makeText(
                        context,
                        runBlocking { getString(Res.string.mic_permission_required) },
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                } else {
                    audioRecordPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                }
                isRecording = true
                recognizer?.startRecognition()
            } else {
                isRecording = false
                recognizer?.getResult()?.let{ onInput(it) }
            }
        }
    ){
        Icon(
            imageVector = Icons.Filled.Mic,
            contentDescription = stringResource(Res.string.voice_input_descr))
    }
}