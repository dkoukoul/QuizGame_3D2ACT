package co.d2act.quizgame

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import co.d2act.quizgame.Globals.CLICK
import co.d2act.quizgame.Globals.COLOR
import co.d2act.quizgame.Globals.QRSCAN
import co.d2act.quizgame.Globals.SHAKE
import co.d2act.quizgame.Globals.SPEAK
import co.d2act.quizgame.Globals.answeredQuestions
import co.d2act.quizgame.Globals.questionTypes
import com.google.zxing.integration.android.IntentIntegrator
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


@Suppress("DEPRECATION")
class Question : AppCompatActivity(), SensorEventListener {

    private val DEBUG = false
    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    private var lastUpdate: Long = 0
    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f
    private val shakeThreshold = 600
    private val shakeSpeedThreshold = 1000
    private var shakeDuration = 0
    private val cameraCaptureImageRequest = 102
    private var mCurrentPhotoPath = ""
    private var imageUri: Uri? = null
    private var shakingAnswer = 0
    private var shortRevision: String = ""
    private var doubleBackToExitPressedOnce = false
    private var firstAttempt = true
    private val colorDiff = 20
    private var firstAnswer = ""
    private var secondAnswer = ""
    lateinit var speechRecognizer: SpeechRecognizer


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        speechRecognizer.setRecognitionListener(VoiceListener(this))
        updateContent()
    }


    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun updateContent() {
        val qInstruction = findViewById<TextView>(R.id.question_instruction)
        val question = findViewById<TextView>(R.id.question)
        val feedbackLayout = findViewById<LinearLayout>(R.id.feedback_layout)
        feedbackLayout.visibility = View.GONE
        val answer1 = findViewById<Button>(R.id.answer1)
        val answer2 = findViewById<Button>(R.id.answer2)
        val answer3 = findViewById<Button>(R.id.answer3)
        when(Globals.getSection()) {
            1 -> {
                if (answeredQuestions.contains(Globals.getQuestion())) {
                    Globals.nextQuestion()
                }
                when(Globals.getQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q1_1_instruction)
                        question.text = getString(R.string.q1_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a1_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a1_1_c)
                        shortRevision = getString(R.string.short_revision_1_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q1_2_instruction)
                        question.text = getString(R.string.q1_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a1_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a1_2_c)
                        shortRevision = getString(R.string.short_revision_1_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q1_3_instruction)
                        question.text = getString(R.string.q1_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a1_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a1_3_c)
                        shortRevision = getString(R.string.short_revision_1_3)
                    }
                }
            }
            2 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        //make specific part of text BOLD
                        val text = getString(R.string.q2_1_instruction)
                        val ss = SpannableString(text)
                        val boldSpan = StyleSpan(Typeface.BOLD)
                        ss.setSpan(boldSpan, 17, 38, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        qInstruction.text = ss
                        question.text = getString(R.string.q2_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a2_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a2_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a2_1_c)
                        shortRevision = getString(R.string.short_revision_2_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q1_2_instruction)
                        question.text = getString(R.string.q2_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a2_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a2_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a2_2_c)
                        shortRevision = getString(R.string.short_revision_2_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q2_3_instruction)
                        question.text = getString(R.string.q2_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a2_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a2_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a2_3_c)
                        shortRevision = getString(R.string.short_revision_2_3)
                    }
                }
            }
            3 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q3_1_instruction)
                        question.text = getString(R.string.q3_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a3_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a3_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a3_1_c)
                        shortRevision = getString(R.string.short_revision_3_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q3_2_instruction)
                        question.text = getString(R.string.q3_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a3_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a3_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a3_2_c)
                        shortRevision = getString(R.string.short_revision_3_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q3_3_instruction)
                        question.text = getString(R.string.q3_3)
                        answer1.text = getString(R.string.a) +" " + getString(R.string.a3_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a3_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a3_3_c)
                        shortRevision = getString(R.string.short_revision_3_3)
                    }
                }
            }
            4 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q4_1_instruction)
                        question.text = getString(R.string.q4_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a4_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a4_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a4_1_c)
                        shortRevision = getString(R.string.short_revision_4_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q4_2_instruction)
                        question.text = getString(R.string.q4_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a4_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a4_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a4_2_c)
                        shortRevision = getString(R.string.short_revision_4_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q4_3_instruction)
                        question.text = getString(R.string.q4_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a4_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a4_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a4_3_c)
                        shortRevision = getString(R.string.short_revision_4_3)
                    }
                }
            }
            5 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q5_1_instruction)
                        question.text = getString(R.string.q5_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a5_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a5_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a5_1_c)
                        shortRevision = getString(R.string.short_revision_5_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q5_2_instruction)
                        question.text = getString(R.string.q5_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a5_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a5_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a5_2_c)
                        shortRevision = getString(R.string.short_revision_5_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q5_3_instruction)
                        question.text = getString(R.string.q5_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a5_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a5_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a5_3_c)
                        shortRevision = getString(R.string.short_revision_5_3)
                    }
                }
            }
            6 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q6_1_instruction)
                        question.text = getString(R.string.q6_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a6_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a6_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a6_1_c)
                        shortRevision = getString(R.string.short_revision_6_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q6_2_instruction)
                        question.text = getString(R.string.q6_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a6_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a6_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a6_2_c)
                        shortRevision = getString(R.string.short_revision_6_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q6_3_instruction)
                        question.text = getString(R.string.q6_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a6_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a6_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a6_3_c)
                        shortRevision = getString(R.string.short_revision_6_3)
                    }
                }
            }
            7 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q7_1_instruction)
                        question.text = getString(R.string.q7_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a7_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a7_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a7_1_c)
                        shortRevision = getString(R.string.short_revision_7_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q7_2_instruction)
                        question.text = getString(R.string.q7_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a7_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a7_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a7_2_c)
                        shortRevision = getString(R.string.short_revision_7_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q7_3_instruction)
                        question.text = getString(R.string.q7_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a7_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a7_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a7_3_c)
                        shortRevision = getString(R.string.short_revision_7_3)
                    }
                }
            }
            8 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q8_1_instruction)
                        question.text = getString(R.string.q8_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a8_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a8_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a8_1_c)
                        shortRevision = getString(R.string.short_revision_8_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q8_2_instruction)
                        question.text = getString(R.string.q8_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a8_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a8_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a8_2_c)
                        shortRevision = getString(R.string.short_revision_8_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q8_3_instruction)
                        question.text = getString(R.string.q8_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a8_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a8_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a8_3_c)
                        shortRevision = getString(R.string.short_revision_8_3)
                    }
                }
            }
            9 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q9_1_instruction)
                        question.text = getString(R.string.q9_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a9_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a9_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a9_1_c)
                        shortRevision = getString(R.string.short_revision_9_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q9_2_instruction)
                        question.text = getString(R.string.q9_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a9_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a9_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a9_2_c)
                        shortRevision = getString(R.string.short_revision_9_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q9_3_instruction)
                        question.text = getString(R.string.q9_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a9_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a9_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a9_3_c)
                        shortRevision = getString(R.string.short_revision_9_3)
                    }
                }
            }
            10 -> {
                when(Globals.getRandomQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q10_1_instruction)
                        question.text = getString(R.string.q10_1)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a10_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a10_1_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a10_1_c)
                        shortRevision = getString(R.string.short_revision_10_1)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q10_2_instruction)
                        question.text = getString(R.string.q10_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a10_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a10_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a10_2_c)
                        shortRevision = getString(R.string.short_revision_10_2)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q10_3_instruction)
                        question.text = getString(R.string.q10_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a10_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a10_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a10_3_c)
                        shortRevision = getString(R.string.short_revision_10_3)
                    }
                }
            }
        }
        val answerButton = findViewById<ImageButton>(R.id.button_answer)
        //Set answer type
        when(questionTypes[Globals.getSection()-1][Globals.getQuestion()-1]) {
            CLICK -> {
                answerButton.visibility = View.GONE
                answer1.setOnClickListener {
                    checkAnswer(1)
                }

                answer2.setOnClickListener {
                    checkAnswer(2)
                }

                answer3.setOnClickListener {
                    checkAnswer(3)
                }
                answer1.isEnabled = true
                answer2.isEnabled = true
                answer3.isEnabled = true
            }
            SHAKE -> {
                sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
                sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
                sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
                answerButton.visibility = View.GONE
                answer1.isEnabled = false
                answer2.isEnabled = false
                answer3.isEnabled = false
                answer1.setOnClickListener {
                    checkAnswer(1)
                }
                answer2.setOnClickListener {
                    checkAnswer(2)
                }
                answer3.setOnClickListener {
                    checkAnswer(3)
                }
            }
            SPEAK -> {
                answerButton.visibility = View.VISIBLE
                answerButton.setImageResource(R.drawable.ic_mic)
                answerButton.setOnClickListener {
                    voiceRecognition()
                }
                answer1.isEnabled = false
                answer2.isEnabled = false
                answer3.isEnabled = false
            }
            QRSCAN -> {
                answerButton.visibility = View.VISIBLE
                answerButton.setImageResource(R.drawable.ic_qr_code)
                answerButton.setOnClickListener {
                    if ((Globals.getSection() == 2) && (Globals.getQuestion() == 1)) {
                        scanCode(1)
                    } else {
                        scanCode(0)
                    }
                }
                answer1.isEnabled = false
                answer2.isEnabled = false
                answer3.isEnabled = false
            }
            COLOR -> {
                answerButton.visibility = View.VISIBLE
                answerButton.setImageResource(R.drawable.ic_camera)
                answerButton.setOnClickListener {
                    captureImage()
                }
                answer1.isEnabled = false
                answer2.isEnabled = false
                answer3.isEnabled = false
                answer1.setTextColor(getColor(R.color.white))
                answer2.setTextColor(getColor(R.color.white))
                answer3.setTextColor(getColor(R.color.white))

                //Change button background color
                var buttonDrawableBlue: Drawable = answer1.background
                buttonDrawableBlue = DrawableCompat.wrap(buttonDrawableBlue)
                var buttonDrawableRed: Drawable = answer2.background
                buttonDrawableRed = DrawableCompat.wrap(buttonDrawableRed)
                var buttonDrawableGreen: Drawable = answer3.background
                buttonDrawableGreen = DrawableCompat.wrap(buttonDrawableGreen)
                DrawableCompat.setTint(buttonDrawableGreen, getColor(R.color.dark_green))
                DrawableCompat.setTint(buttonDrawableRed, getColor(R.color.dark_red))
                DrawableCompat.setTint(buttonDrawableBlue, getColor(R.color.dark_blue))
                when(Globals.getSection()){
                    5 -> {
                        answer1.background = buttonDrawableRed
                        answer2.background = buttonDrawableGreen
                        answer3.background = buttonDrawableBlue
                    }
                    7 -> {
                        answer1.background = buttonDrawableRed
                        answer2.background = buttonDrawableBlue
                        answer3.background = buttonDrawableGreen
                    }
                    8 -> {
                        answer1.background = buttonDrawableRed
                        answer2.background = buttonDrawableGreen
                        answer3.background = buttonDrawableBlue
                    }
                    9 -> {
                        answer1.background = buttonDrawableBlue
                        answer2.background = buttonDrawableRed
                        answer3.background = buttonDrawableGreen
                    }
                }
            }
        }
    }

    /**
     * Will initiate the intent for voice recognition
     */
    private fun voiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
        speechRecognizer.startListening(intent)
    }


    /**
     * Will initiate the intent for QR Code scanning
     */
    private fun scanCode(number: Int) {
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setOrientationLocked(false)
        when(number) {
            1 -> intentIntegrator.setPrompt(getString(R.string.scan_1_qr_code))
            2 -> intentIntegrator.setPrompt(getString(R.string.scan_2_qr_code))
            else -> intentIntegrator.setPrompt(getString(R.string.scan_qr_code))
        }
        intentIntegrator.initiateScan()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == cameraCaptureImageRequest) {
            getColorFromImage()
        } else {
            val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
            if (intentResult != null) {
                if (intentResult.contents == null) {
                    Toast.makeText(baseContext, "Cancelled", Toast.LENGTH_SHORT).show()
                } else {
                    qrCodeHandler(intentResult.contents)
                    if (DEBUG) {
                        findViewById<TextView>(R.id.status).text = intentResult.contents
                    }
                }
            }
        }

    }

    private fun qrCodeHandler(contents: String) {
        try {
            val section = contents.split("_")[0].toInt()
            val question = contents.split("_")[1].toInt()

            //Handle special case for 2_1 which require two QR codes
            if ((Globals.getSection() == 2) && (Globals.getQuestion() == 1)) {
                //Record first answer and open scanner again for second
                if (firstAnswer.isNotEmpty()) {
                    secondAnswer = contents
                } else {
                    firstAnswer = contents
                }
                if (firstAnswer.isNotEmpty() && secondAnswer.isNotEmpty()) {
                    //TODO: handle the case where user answers twice with the same qr code 2_1_1a or 2_1_1b
                    if (firstAnswer.contains("2_1_1") && secondAnswer.contains("2_1_1")){
                        correctAnswer()
                    } else {
                        wrongAnswer()
                    }
                } else {
                    scanCode(2)
                }
            } else if (section == Globals.getSection() && question == Globals.getQuestion()) {
                val answer = contents.split("_")[2].toInt()
                checkAnswer(answer)
            } else {
                wrongAnswer()
            }
        } catch (e: Exception) {
            Toast.makeText(baseContext, "Invalid QR Code", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Puts a list of strings in an edit text on an alert dialog and let user
     * edit, confirm before submitting it as an answer. Gets submitted text and
     * checks it against checkAnswer
     */
    fun confirmVoiceRecognition(words: ArrayList<String>) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.voice_recognition_confirm_title))
        builder.setMessage(getString(R.string.voice_recognition_confirm_message))
        val dialogView = layoutInflater.inflate(R.layout.voice_egognition_layout, null)
        builder.setView(dialogView)

        val editText = dialogView.findViewById<EditText>(R.id.voice_regognition_text)
        editText.setText(words.joinToString(" "))
        builder.setPositiveButton(getString(R.string.alert_submit_button)) { dialog, _ ->
            val answer1 = findViewById<Button>(R.id.answer1)
            val answer2 = findViewById<Button>(R.id.answer2)
            val answer3 = findViewById<Button>(R.id.answer3)
            val answer = editText.text.toString()
            if (answer.equals(answer1.text.toString().substring(3).replace("?",""), ignoreCase = true)) {
                checkAnswer(1)
            } else if (answer.equals(answer2.text.toString().substring(3).replace("?",""), ignoreCase = true)) {
                checkAnswer(2)
            } else if (answer.equals(answer3.text.toString().substring(3).replace("?",""), ignoreCase = true)) {
                checkAnswer(3)
            } else {
               //Could not match to a possible answer
                Toast.makeText(this, getString(R.string.toast_wrong_speech), Toast.LENGTH_LONG).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.alert_cancel_button)) { dialog, _ ->
            dialog.dismiss()
        }
        val alert: AlertDialog = builder.create()
        alert.show()
        alert.window?.setGravity(Gravity.TOP);
    }

    /**
     * Returns the dominant color name
     * (Red, Green or Blue) of a given bitmap
     */
    @SuppressLint("SetTextI18n")
    private fun getDominantColor(bitmap: Bitmap): String{
        var  redBucket = 0
        var  greenBucket = 0
        var  blueBucket = 0
        var  pixelCount = 0
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val c: Int = bitmap.getPixel(x, y)
                pixelCount++
                redBucket += Color.red(c)
                greenBucket += Color.green(c)
                blueBucket += Color.blue(c)
            }
        }

        val red = redBucket / pixelCount
        val green = greenBucket / pixelCount
        val blue = blueBucket / pixelCount

        if (DEBUG) {
            findViewById<TextView>(R.id.status).text = "R: $red G: $green B: $blue"
        }
        return if ((red > green+colorDiff) && (red > blue+colorDiff)) {
            "red"
        } else if ((green > red+colorDiff) && (green > blue+colorDiff)) {
            "green"
        } else if ((blue > red+colorDiff) && (blue > green+colorDiff)) {
            "blue"
        } else {
            "other"
        }
    }

    /**
     *
     */
    private fun getColorFromImage() {
        if (mCurrentPhotoPath.isEmpty()) {
            return
        }
        val cr = contentResolver
        val `in`: InputStream? = imageUri?.let { cr.openInputStream(it) }
        val options = BitmapFactory.Options()
        options.inSampleSize = 8
        val bmp = BitmapFactory.decodeStream(`in`, null, options)
        if (bmp != null) {
            val colorAnswer = getDominantColor(bmp)
            if (colorAnswer.equals("blue", ignoreCase = true)) {
                checkAnswer(1)
            } else if (colorAnswer.equals("red", ignoreCase = true)) {
                checkAnswer(2)
            } else if (colorAnswer.equals("green", ignoreCase = true)) {
                checkAnswer(3)
            } else {//other
                Toast.makeText(this,getString(R.string.toast_wrong_color), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkAnswer(answer: Int) {
        val section = Globals.getSection()-1
        val question = Globals.getQuestion()-1
        if (Globals.answers[section][question] == answer) {
            correctAnswer()
        } else {
            wrongAnswer()
        }
    }

    private fun correctAnswer() {
        showDialog(getString(R.string.dialog_correct_answer), true)
    }

    private fun wrongAnswer() {
        if (firstAttempt) {
            showDialog(getString(R.string.dialog_wrong_answer_first_attempt), false)
        } else {
            showDialog(getString(R.string.dialog_wrong_answer_second_attempt), false)
        }

        //show feedback
        val feedbackLayout = findViewById<LinearLayout>(R.id.feedback_layout)
        val feedback = findViewById<TextView>(R.id.feedback)
        feedback.text = shortRevision
        feedbackLayout.visibility = View.VISIBLE
        /*Toast.makeText(this,getString(R.string.toast_wrong_answer),Toast.LENGTH_LONG).show()*/
    }

    private fun showDialog(message: String, correct: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("")
        //builder.setMessage(message)

        //Correct answer, go to Next
        if (correct) {
            builder.setMessage(getString(R.string.dialog_correct_answer))
            Globals.addScore(firstAttempt)
            builder.setPositiveButton(getString(R.string.button_next)) { dialog, _ ->
                dialog.dismiss()
                goToNext()
            }
        } else {
            //Wrong answer, first attempt, try again
            if (firstAttempt) {
                builder.setMessage(getString(R.string.dialog_wrong_answer_first_attempt))
                builder.setPositiveButton(getString(R.string.button_try)) { dialog, _ ->
                    dialog.dismiss()
                    firstAttempt = false
                }
            } else {
                //Wrong answer, 2nd attempt, go to next
                builder.setMessage(getString(R.string.dialog_wrong_answer_second_attempt))
                builder.setPositiveButton(getString(R.string.button_next)) { _, _ ->
                    goToNext()
                }
            }
        }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    private fun goToNext() {
        answeredQuestions.add(Globals.getQuestion())
        var gotoNextSession = false
        if (Globals.answeredQuestions.size == 3) {
            gotoNextSession = true
            Globals.nextSection()
        }

        //Go to next section
        if (gotoNextSession) {
            if (Globals.getSection() > 10) {
                //End of game
                val scoreIntent = Intent(this, ScoreActivity::class.java)
                startActivity(scoreIntent)
            } else {
                val sectionActivity = Intent(applicationContext, Section::class.java)
                startActivity(sectionActivity)
            }
        }
        //Go to next question
        else {
            Globals.nextQuestion()
            val questionActivity = Intent(applicationContext, Question::class.java)
            startActivity(questionActivity)
        }
        Globals.saveCache()
        finish()
    }

    @SuppressLint("SimpleDateFormat")
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        val image: File = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }



    private fun captureImage() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val imageFile = createImageFile()
        imageUri = FileProvider.getUriForFile(Objects.requireNonNull(applicationContext), BuildConfig.APPLICATION_ID + ".provider", imageFile)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(intent, cameraCaptureImageRequest)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    override fun onSensorChanged(event: SensorEvent?) {
        /* shaking */
        if (event != null) {
            val curTime :Long = System.currentTimeMillis()
            if (curTime - lastUpdate > 100) {
                val diffTime: Long = curTime - lastUpdate
                lastUpdate = curTime
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val speed: Float = kotlin.math.abs(x + y + z - lastX - lastY - lastZ) / diffTime * 10000
                if (speed > shakeSpeedThreshold) {
                    //Capture a shake
                    shakeDuration += 100
                }
                //check if shake was long enough
                if (shakeDuration > shakeThreshold) {
                    if (DEBUG) {
                        findViewById<TextView>(R.id.status).text = "SHAKE!!!"
                    }
                    //avoid double jump
                    lastUpdate += 800
                    if (shakingAnswer == 3) {
                        shakingAnswer = 1
                    } else {
                        shakingAnswer++
                    }
                    setButtonOnShake(shakingAnswer)
                    //Reset
                    shakeDuration = 0
                } else {
                    if (DEBUG) {
                        findViewById<TextView>(R.id.status).text = "shaking was $shakeDuration"
                    }
                }
                lastX = x
                lastY = y
                lastZ = z
            }
        }
    }

    private fun setButtonOnShake(button: Int) {
        val answer1 = findViewById<Button>(R.id.answer1)
        val answer2 = findViewById<Button>(R.id.answer2)
        val answer3 = findViewById<Button>(R.id.answer3)
        when(button) {
            1 -> {
                answer1.isEnabled = true
                answer2.isEnabled = false
                answer3.isEnabled = false
            }
            2 -> {
                answer1.isEnabled = false
                answer2.isEnabled = true
                answer3.isEnabled = false
            }
            3 -> {
                answer1.isEnabled = false
                answer2.isEnabled = false
                answer3.isEnabled = true
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        if (questionTypes[Globals.getSection()-1][Globals.getQuestion()-1] == SHAKE) {
            sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
            sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onBackPressed() {
        if(doubleBackToExitPressedOnce) {
            //clear cache
            Globals.clearCache()
            this.finishAffinity()
            /*super.onBackPressed()*/
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.toast_back_button), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}