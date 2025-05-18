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

package com.ghostwalker18.schedule

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import java.io.File
import java.util.*
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

/**
 * Этот класс осуществляет преобразование речи в голос на Desktop.
 *
 * @author Ипатов Никита
 */
class SpeechRecognizer {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val microphone: TargetDataLine
    private var recognizer: Recognizer? = null
    private val buffer = ByteArray(BUFFER_SIZE)
    private var shouldRecord = false
    private var speechToText = ""
    /**
     * Это свойство отображает готовность распознавателя к работе.
     */
    val isReady = _isReady.asStateFlow()

    init {
        LibVosk.setLogLevel(LogLevel.INFO)
        microphone = AudioSystem.getLine(info) as TargetDataLine
        microphone.open(format)
        if(model == null)
            initModel()
    }

    /**
     * Этот метод используется для начала обработки речи.
     */
    fun startRecognition(){
        shouldRecord = true
        recognizer = Recognizer(model, SAMPLE_RATE)
        scope.launch {
            microphone.start()
            var bytesRead: Int
            while (shouldRecord){
                bytesRead = microphone.read(buffer, 0, CHUNK_SIZE)
                recognizer?.let {
                    if(it.acceptWaveForm(buffer, bytesRead))
                        speechToText += extractResult(it.result)
                }
            }
        }
    }

    /**
     * Этот метод используется для получения результата обработки речи.
     */
    fun getResult(): String {
        shouldRecord = false
        microphone.stop()
        recognizer = null
        val res = speechToText
        speechToText = ""
        return res
    }

    /**
     * Этот метод преобразует результат обработки голоса
     * из формата библиотеки к формату обычной строки.
     */
    private fun extractResult(result: String): String {
        return result
            .removeSurrounding("{\n", "\n}")
            .split(":")[1]
            .let{
                it.substring(2, it.length - 1)
            }
            .replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(
                    Locale.Builder()
                        .setLanguageTag("ru")
                        .build()
                ) else it.toString()
            } + ". "
    }

    companion object {
        private const val SAMPLE_RATE = 16000f
        private const val BUFFER_SIZE = 4096
        private const val CHUNK_SIZE = 1024
        private val format = AudioFormat(
            SAMPLE_RATE, 16,
            1, true, false)
        private val info = DataLine.Info(TargetDataLine::class.java, format)
        private var model: Model? = null
        private val _isReady = MutableStateFlow(false)

        /**
         * Этот метод инициализирует модель распознавания речи.
         */
        fun initModel(){
            try {
                val modelUrl = ScheduleApp::class.java.getResource("/model-ru-ru")
                val path = modelUrl?.toURI()?.let { File(it).absolutePath }
                this.model = Model(path)
                _isReady.value = true
            } catch (e: Exception){
                println(e.message.toString())
            }
        }
    }
}