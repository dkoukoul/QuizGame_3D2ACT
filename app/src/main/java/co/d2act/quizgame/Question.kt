package co.d2act.quizgame

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.graphics.drawable.DrawableCompat
import co.d2act.quizgame.Globals.CLICK
import co.d2act.quizgame.Globals.COLOR
import co.d2act.quizgame.Globals.SCAN
import co.d2act.quizgame.Globals.SHAKE
import co.d2act.quizgame.Globals.SPEAK
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
    private val speechToTextRequest = 103
    private var mCurrentPhotoPath = ""
    private var imageUri: Uri? = null
    private var feedback: ArrayList<ArrayList<ArrayList<String>>> = arrayListOf(arrayListOf(arrayListOf()))
    private var secondTry = false
    private var shakingAnswer = 0
    private var shortRevision: String = ""
    private var doubleBackToExitPressedOnce = false
    private var firstAttempt = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        initFeedback()
        updateContent()
    }

    private fun initFeedback() {
        val feedback11 = arrayListOf(getString(R.string.a1_1_af),getString(R.string.a1_1_bf),getString(R.string.a1_1_cf))
        val feedback12 = arrayListOf(getString(R.string.a1_2_af),getString(R.string.a1_2_bf),getString(R.string.a1_2_cf))
        val feedback13 = arrayListOf(getString(R.string.a1_3_af),getString(R.string.a1_3_bf),getString(R.string.a1_3_cf))
        val feedback1 = arrayListOf(feedback11,feedback12,feedback13)
        val feedback21 = arrayListOf(getString(R.string.a2_1_af),getString(R.string.a2_1_bf),getString(R.string.a2_1_cf))
        val feedback22 = arrayListOf(getString(R.string.a2_2_af),getString(R.string.a2_2_bf),getString(R.string.a2_2_cf))
        val feedback23 = arrayListOf(getString(R.string.a2_3_af),getString(R.string.a2_3_bf),getString(R.string.a2_3_cf))
        val feedback2 = arrayListOf(feedback21,feedback22,feedback23)
        val feedback31 = arrayListOf(getString(R.string.a3_1_af),getString(R.string.a3_1_bf),getString(R.string.a3_1_cf))
        val feedback32 = arrayListOf(getString(R.string.a3_2_af),getString(R.string.a3_2_bf),getString(R.string.a3_2_cf))
        val feedback33 = arrayListOf(getString(R.string.a3_3_af),getString(R.string.a3_3_bf),getString(R.string.a3_3_cf))
        val feedback3 = arrayListOf(feedback31,feedback32,feedback33)
        val feedback41 = arrayListOf(getString(R.string.a4_1_af),getString(R.string.a4_1_bf),getString(R.string.a4_1_cf))
        val feedback42 = arrayListOf(getString(R.string.a4_2_af),getString(R.string.a4_2_bf),getString(R.string.a4_2_cf))
        val feedback43 = arrayListOf(getString(R.string.a4_3_af),getString(R.string.a4_3_bf),getString(R.string.a4_3_cf))
        val feedback4 = arrayListOf(feedback41,feedback42,feedback43)
        val feedback51 = arrayListOf(getString(R.string.a5_1_af),getString(R.string.a5_1_bf),getString(R.string.a5_1_cf))
        val feedback52 = arrayListOf(getString(R.string.a5_2_af),getString(R.string.a5_2_bf),getString(R.string.a5_2_cf))
        val feedback53 = arrayListOf(getString(R.string.a5_3_af),getString(R.string.a5_3_bf),getString(R.string.a5_3_cf))
        val feedback5 = arrayListOf(feedback51,feedback52,feedback53)
        feedback = arrayListOf(feedback1,feedback2,feedback3,feedback4,feedback5)
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
                when(Globals.getQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q2_1_instruction)
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
                when(Globals.getQuestion()) {
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
                when(Globals.getQuestion()) {
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
                when(Globals.getQuestion()) {
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
                when(Globals.getQuestion()) {
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
            7 -> {
                when(Globals.getQuestion()) {
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
            8 -> {
                when(Globals.getQuestion()) {
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
            9 -> {
                when(Globals.getQuestion()) {
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
            10 -> {
                when(Globals.getQuestion()) {
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
            SCAN -> {
                answerButton.visibility = View.VISIBLE
                answerButton.setImageResource(R.drawable.ic_qr_code)
                answerButton.setOnClickListener {
                    scanCode()
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
                DrawableCompat.setTint(buttonDrawableBlue, getColor(R.color.dark_blue))
                var buttonDrawableRed: Drawable = answer2.background
                buttonDrawableRed = DrawableCompat.wrap(buttonDrawableRed)
                DrawableCompat.setTint(buttonDrawableRed, getColor(R.color.dark_red))
                var buttonDrawableGreen: Drawable = answer3.background
                buttonDrawableGreen = DrawableCompat.wrap(buttonDrawableGreen)
                DrawableCompat.setTint(buttonDrawableGreen, getColor(R.color.dark_green))

                answer1.background = buttonDrawableBlue
                answer2.background = buttonDrawableRed
                answer3.background = buttonDrawableGreen
            }
        }

    }

    /**
     * Will initiate the intent for voice recognition
     */
    private fun voiceRecognition() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
        intent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Read the answer...")
        startActivityForResult(intent, speechToTextRequest)
    }

    /**
     * Will initiate the intent for QR Code scanning
     */
    private fun scanCode() {
        val intentIntegrator = IntentIntegrator(this)
        intentIntegrator.setOrientationLocked(false)
        intentIntegrator.setPrompt("Scan QR Code")
        intentIntegrator.initiateScan()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == cameraCaptureImageRequest) {
            getColorFromImage()
        } else if (requestCode == speechToTextRequest) {
            //Populate the strings to an alert dialog to confirm the answer
            val matches = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (matches != null) {
                confirmVoiceRecognition(matches)
            } else {
                //TODO: try again because we got no words back
            }
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
            val answer = contents.split("_")[2].toInt()

            if (section == Globals.getSection() && question == Globals.getQuestion()) {
                checkAnswer(answer)
            } else {
                if ((Globals.getSection() == 1) && (Globals.getQuestion() == 3)) {
                    wrongAnswer("Good try, see help for the correct answer.")
                } else {
                    Toast.makeText(baseContext, "Wrong QR Code", Toast.LENGTH_SHORT).show()
                }
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
    private fun confirmVoiceRecognition(words: ArrayList<String>) {
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
                //try again
                confirmVoiceRecognition(words)
            }
            dialog.dismiss()
        }
        builder.setNegativeButton(getString(R.string.alert_cancel_button)) { dialog, _ ->
            dialog.dismiss()
        }
        val alert: AlertDialog = builder.create()
        alert.show()
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
        val colorDiff = 30
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
            correctAnswer(feedback[section][question][answer-1])
            Globals.addScore(firstAttempt)
        } else {
            firstAttempt = false
            wrongAnswer(feedback[section][question][answer-1])
        }
    }

    private fun correctAnswer(message: String) {
        secondTry = false
        showDialog(message, true)
    }

    private fun wrongAnswer(message:String) {
        showDialog(message, false)
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
        builder.setMessage(message)
        if (correct) {
            builder.setPositiveButton(getString(R.string.button_next)) { dialog, _ ->
                dialog.dismiss()
                var gotoNextSession = false
                if (Globals.getQuestion() == 3) gotoNextSession = true
                Globals.nextQuestion()
                //Go to next section
                //TODO: handle end of game with scoreboard
                if (gotoNextSession) {
                    val sectionActivity = Intent(applicationContext, Section::class.java)
                    startActivity(sectionActivity)
                }
                //Go to next question
                else {
                    val questionActivity = Intent(applicationContext, Question::class.java)
                    startActivity(questionActivity)
                }
                finish()
            }
        } else if (secondTry) {
            val questionActivity = Intent(applicationContext, Question::class.java)
            startActivity(questionActivity)
            finish()
        } else {
            builder.setPositiveButton(getString(R.string.button_try)) { dialog, _ ->
                dialog.dismiss()
                secondTry = true
            }
        }
        val alert: AlertDialog = builder.create()
        alert.show()
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
                    shakingAnswer++
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
            4 -> {
                shakingAnswer = 0
                answer1.isEnabled = false
                answer2.isEnabled = false
                answer3.isEnabled = false
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
            super.onBackPressed()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.toast_back_button), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
/*        if (Globals.getQuestion() == 1) {
            //will finish question activity and return to section activity
            Globals.goBack()
        } else {
            Globals.goBack()
            val questionActivity = Intent(applicationContext, Question::class.java)
            startActivity(questionActivity)
        }*/
    }
}