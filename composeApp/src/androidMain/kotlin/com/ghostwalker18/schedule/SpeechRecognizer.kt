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

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.vosk.LibVosk
import org.vosk.LogLevel
import org.vosk.Model
import org.vosk.Recognizer
import org.vosk.android.RecognitionListener
import org.vosk.android.SpeechService
import org.vosk.android.StorageService

/**
 * Этот класс осуществляет преобразование речи в голос.
 *
 * @author Ипатов Никита
 */
class SpeechRecognizer(val context: Context): RecognitionListener {
    private var model: Model? = null
    private var speechService: SpeechService? = null
    private var speechToText: String = ""
    private val _isReady = MutableStateFlow(false)

    /**
     * Это свойство отображает готовность распознавателя к работе.
     */
    val isReady = _isReady.asStateFlow()

    init {
        LibVosk.setLogLevel(LogLevel.INFO)
        initModel()
    }

    /**
     * Этот метод используется для начала обработки речи.
     */
    fun startRecognition(){
        try{
            val rec = Recognizer(model, 16000.0f)
            speechService = SpeechService(rec, 16000.0f)
            speechService?.startListening(this)
        } catch(_: Exception){}
    }

    /**
     * Этот метод используется для получения результата обработки речи.
     */
    fun getResult(): String {
        speechService?.stop()
        speechService = null
        val res = speechToText
        speechToText = ""
        return res
    }

    override fun onResult(hypothesis: String?) {
        if (hypothesis != null) {
            Log.i("Result", hypothesis)
        }
        speechToText += ( extractResult(hypothesis ?: "") + ". ")
    }

    override fun onPartialResult(hypothesis: String?) {/*Not required*/}

    override fun onFinalResult(hypothesis: String?) {/*Not required*/}

    override fun onError(p0: Exception?) {/*Not required*/}

    override fun onTimeout() {/*Not required*/}

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
    }

    /**
     * Этот метод загружает модель для использования.
     */
    private fun initModel() {
        StorageService.unpack(
            context, "model-ru-ru", "model",
            { model: Model? ->
                this.model = model
                _isReady.value = true
            },
            { })
    }
}