package co.d2act.quizgame

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
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
import java.lang.Math.abs
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class Question : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var sensor: Sensor
    var lastUpdate: Long = 0
    var last_x = 0f
    var last_y = 0f
    var last_z = 0f
    val shakeThreshold = 1000
    private val CAMERA_CAPTURE_IMAGE_REQUEST = 102
    private val SPEECH_TO_TEXT_REQUEST = 103
    private var mCurrentPhotoPath = ""
    private var imageUri: Uri? = null
    private var feedback: ArrayList<ArrayList<ArrayList<String>>> = arrayListOf(arrayListOf(arrayListOf()))
    private var secondTry = false
    private var shakingAnswer = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)

        initFeedback()

        updateContent()

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    }

    fun initFeedback() {
        val feedback11 = arrayListOf(getString(R.string.a1_1_af),getString(R.string.a1_1_bf),getString(R.string.a1_1_cf))
        val feedback12 = arrayListOf(getString(R.string.a1_2_af),getString(R.string.a1_2_bf),getString(R.string.a1_2_cf))
        val feedback13 = arrayListOf(getString(R.string.a1_3_af),getString(R.string.a1_3_bf),getString(R.string.a1_3_cf))
        val feedback1 = arrayListOf(feedback11,feedback12,feedback13)
        feedback = arrayListOf(feedback1)
    }

    @SuppressLint("UseCompatLoadingForDrawables", "SetTextI18n")
    private fun updateContent() {
        val qInstruction = findViewById<TextView>(R.id.question_instruction)
        val question = findViewById<TextView>(R.id.question)
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
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q1_2_instruction)
                        question.text = getString(R.string.q1_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a1_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_2_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a1_2_c)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q1_3_instruction)
                        question.text = getString(R.string.q1_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a1_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_3_b)
                        answer3.text = getString(R.string.c) +" " +getString(R.string.a1_3_c)
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
                        answer3.text = getString(R.string.a2_1_c)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q1_2_instruction)
                        question.text = getString(R.string.q2_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a2_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a2_2_b)
                        answer3.text = getString(R.string.a2_2_c)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q2_3_instruction)
                        question.text = getString(R.string.q2_3)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a2_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a2_3_b)
                        answer3.text = getString(R.string.a2_3_c)
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
                        answer3.text = getString(R.string.a3_1_c)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q3_2_instruction)
                        question.text = getString(R.string.q3_2)
                        answer1.text = getString(R.string.a) +" " +getString(R.string.a3_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a3_2_b)
                        answer3.text = getString(R.string.a3_2_c)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q3_3_instruction)
                        question.text = getString(R.string.q3_3)
                        answer1.text = getString(R.string.a) +" " + getString(R.string.a3_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a3_3_b)
                        answer3.text = getString(R.string.a3_3_c)
                    }
                }
            }
            4 -> {
                when(Globals.getQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q1_1_instruction)
                        question.text = getString(R.string.q1_1)
                        answer1.text = getString(R.string.a1_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_1_b)
                        answer3.text = getString(R.string.a1_1_c)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q1_2_instruction)
                        question.text = getString(R.string.q1_2)
                        answer1.text = getString(R.string.a1_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_2_b)
                        answer3.text = getString(R.string.a1_2_c)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q1_3_instruction)
                        question.text = getString(R.string.q1_3)
                        answer1.text = getString(R.string.a1_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_3_b)
                        answer3.text = getString(R.string.a1_3_c)
                    }
                }
            }
            5 -> {
                when(Globals.getQuestion()) {
                    1 -> {
                        qInstruction.text = getString(R.string.q1_1_instruction)
                        question.text = getString(R.string.q1_1)
                        answer1.text = getString(R.string.a1_1_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_1_b)
                        answer3.text = getString(R.string.a1_1_c)
                    }
                    2 -> {
                        qInstruction.text = getString(R.string.q1_2_instruction)
                        question.text = getString(R.string.q1_2)
                        answer1.text = getString(R.string.a1_2_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_2_b)
                        answer3.text = getString(R.string.a1_2_c)
                    }
                    3 -> {
                        qInstruction.text = getString(R.string.q1_3_instruction)
                        question.text = getString(R.string.q1_3)
                        answer1.text = getString(R.string.a1_3_a)
                        answer2.text = getString(R.string.b) +" " +getString(R.string.a1_3_b)
                        answer3.text = getString(R.string.a1_3_c)
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
        startActivityForResult(intent, SPEECH_TO_TEXT_REQUEST)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST) {
            getColorFromImage()
        } else if (requestCode == SPEECH_TO_TEXT_REQUEST) {
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
                    findViewById<TextView>(R.id.status).text = intentResult.contents
                }
            }
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
            if (answer.equals(answer1.text.toString().substring(3), ignoreCase = true)) {
                checkAnswer(1)
            } else if (answer.equals(answer2.text.toString().substring(3), ignoreCase = true)) {
                checkAnswer(2)
            } else if (answer.equals(answer3.text.toString().substring(3), ignoreCase = true)) {
                checkAnswer(3)
            } else {
               //
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
    private fun getDominantColor(bitmap: Bitmap): String{
        var  redBucket: Int = 0
        var  greenBucket: Int = 0
        var  blueBucket: Int = 0
        var  pixelCount: Int = 0
        for (y in 0 until bitmap.height) {
            for (x in 0 until bitmap.width) {
                val c: Int = bitmap.getPixel(x, y)
                pixelCount++
                redBucket += Color.red(c)
                greenBucket += Color.green(c)
                blueBucket += Color.blue(c)
                // does alpha matter?
            }
        }

        val red = redBucket / pixelCount
        val green = greenBucket / pixelCount
        val blue = blueBucket / pixelCount
        if ((red > green) && (red > blue)) {
            return "red"
        } else if ((green > red) && (green > blue)) {
            return "green"
        } else if ((blue > red) && (blue > green)) {
            return "blue"
        } else {
            return "unknown"
        }
        //val averageColor: Int = Color.rgb(red, green, blue)
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
            findViewById<TextView>(R.id.status).text = getDominantColor(bmp)
        }
    }

    private fun checkAnswer(answer: Int) {
        val section = Globals.getSection()-1
        val question = Globals.getQuestion()-1

        if (Globals.answers[section][question] == answer) {
            correctAnswer(feedback[section][question][answer-1])
        } else {
            wrongAnswer(feedback[section][question][answer-1])
        }
    }

    private fun correctAnswer(message: String) {
        secondTry = false
        showDialog(message, true)
    }

    private fun wrongAnswer(message:String) {
        showDialog(message, false)
    }

    private fun showDialog(message: String, correct: Boolean) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle("")
        builder.setMessage(message)
        if (correct) {
            builder.setPositiveButton(getString(R.string.button_next)) { dialog, _ ->
                dialog.dismiss()
                Globals.nextQuestion()
                val questionActivity = Intent(applicationContext, Question::class.java)
                startActivity(questionActivity)
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
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    override fun onSensorChanged(event: SensorEvent?) {
        /* shaking */
        if (event != null) {
            val answer1 = findViewById<Button>(R.id.answer1)
            val answer2 = findViewById<Button>(R.id.answer2)
            val answer3 = findViewById<Button>(R.id.answer3)
            val curTime :Long = System.currentTimeMillis()

            if (curTime - lastUpdate > 800) {
                val diffTime: Long = curTime - lastUpdate
                lastUpdate = curTime
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val speed: Float = kotlin.math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000
                if (speed > shakeThreshold) {
                    shakingAnswer++
                    when(shakingAnswer) {
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
                last_x = x
                last_y = y
                last_z = z
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
    }

}