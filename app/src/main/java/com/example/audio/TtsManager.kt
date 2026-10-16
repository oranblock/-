package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _speechRate = MutableStateFlow(1.15f)
    val speechRate: StateFlow<Float> = _speechRate.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.let { engine ->
                val result = engine.setLanguage(Locale.US)
                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    isInitialized = true
                    engine.setSpeechRate(_speechRate.value)
                    engine.setPitch(1.0f)
                    engine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                        }

                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                        }
                    })
                }
            }
        }
    }

    fun speak(text: String) {
        if (!isInitialized || tts == null) return
        stop()
        tts?.setSpeechRate(_speechRate.value)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "phonoflicks_utterance")
    }

    fun stop() {
        tts?.stop()
        _isSpeaking.value = false
    }

    fun setSpeed(rate: Float) {
        _speechRate.value = rate
        tts?.setSpeechRate(rate)
    }

    fun cycleSpeed(): Float {
        val nextSpeed = when {
            _speechRate.value < 1.1f -> 1.25f
            _speechRate.value < 1.3f -> 1.5f
            else -> 1.0f
        }
        setSpeed(nextSpeed)
        return nextSpeed
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
        isInitialized = false
    }
}
