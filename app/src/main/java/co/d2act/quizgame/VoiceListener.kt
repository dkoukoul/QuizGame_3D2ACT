package co.d2act.quizgame

import android.content.Context
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList

class VoiceListener(context: Question) : RecognitionListener {
    val ctx = context
    override fun onReadyForSpeech(params: Bundle) {
        Toast.makeText(ctx, ctx.getString(R.string.reading_prompt), Toast.LENGTH_LONG).show()
    }

    override fun onBeginningOfSpeech() {
        var dosomething = ""
    }

    override fun onRmsChanged(rmsdB: Float) {
        var dosomething = ""
    }

    override fun onBufferReceived(buffer: ByteArray) {
        var dosomething = ""
    }

    override fun onEndOfSpeech() {
        var dosomething = ""
    }

    override fun onError(error: Int) {
        var dosomething = ""
    }

    override fun onResults(results: Bundle) {
        val data: ArrayList<String>? = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
        if (data != null) {
            ctx.confirmVoiceRecognition(data)
        } else {
            Toast.makeText(ctx, ctx.getString(R.string.reading_prompt_no_results), Toast.LENGTH_LONG).show()
        }
    }

    override fun onPartialResults(partialResults: Bundle) {
        var dosomething = ""
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        var dosomething = ""
    }
}