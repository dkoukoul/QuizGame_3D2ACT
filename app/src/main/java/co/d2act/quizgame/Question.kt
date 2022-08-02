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
import android.text.InputType
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
    private var secondTry = false;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_question)
        initFeedback()

        val answer1 = findViewById<Button>(R.id.answer1)
        val answer2 = findViewById<Button>(R.id.answer2)
        val answer3 = findViewById<Button>(R.id.answer3)
        val answerButton = findViewById<ImageButton>(R.id.button_answer)
        when(questionTypes[Globals.getSection()][Globals.getQuestion()]) {
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
            }
            SHAKE -> {
                answerButton.visibility = View.GONE
            }
            SPEAK -> {
                answerButton.visibility = View.VISIBLE
                answerButton.setImageResource(R.drawable.ic_mic)
                answerButton.setOnClickListener {
                    voiceRecognition()
                }
            }
            SCAN -> {
                answerButton.visibility = View.VISIBLE
                answerButton.setImageResource(R.drawable.ic_qr_code)
                answerButton.setOnClickListener {
                    scanCode()
                }
            }
            COLOR -> {
                answerButton.visibility = View.VISIBLE
                answerButton.setImageResource(R.drawable.ic_camera)
                answerButton.setOnClickListener {
                    captureImage()
                }
            }
        }

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
        val input = EditText(this)
        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        input.layoutParams = lp
        builder.setView(input)
        input.inputType = InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS

        builder.setPositiveButton(getString(R.string.alert_submit_button)) { dialog, _ ->
            val answer1 = findViewById<Button>(R.id.answer1)
            val answer2 = findViewById<Button>(R.id.answer2)
            val answer3 = findViewById<Button>(R.id.answer3)
            when(input.text.toString()) {
                answer1.text -> checkAnswer(1)
                answer2.text -> checkAnswer(2)
                answer3.text -> checkAnswer(3)
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
        if (mCurrentPhotoPath.isNullOrEmpty()) {
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
        val section = Globals.getSection()
        val question = Globals.getQuestion()

        if (Globals.answers[section][question] == answer) {
            correctAnswer(feedback[section][question][(answer-1)])
        } else {
            wrongAnswer(feedback[section][question][(answer-1)])
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

        /* Orientation
        if (event != null) {
            val answer3 = findViewById<Button>(R.id.answer3)
            val answer2 = findViewById<Button>(R.id.answer2)
            val answer1 = findViewById<Button>(R.id.answer1)
            if (event.values[0] in 80f..100f ) {
                findViewById<TextView>(R.id.status).text = "90"
                answer3.background = getDrawable(R.color.black)
            } else if (event.values[0] in 350f..10f ) {
                findViewById<TextView>(R.id.status).text = "0"
                answer2.background = getDrawable(R.color.black)
            } else if (event.values[0] in 260f..280f ) {
                findViewById<TextView>(R.id.status).text = "270"
                answer1.background = getDrawable(R.color.black)
            } else {
                findViewById<TextView>(R.id.status).text = event.values[0].toString()
            }
        }*/
        /* shaking */
       /* if (event != null) {
            val curTime :Long = System.currentTimeMillis()
            // only allow one update every 100ms.
            if (curTime - lastUpdate > 100) {
                val diffTime: Long = curTime - lastUpdate
                lastUpdate = curTime
                val x = event.values[0]
                val y = event.values[1]
                val z = event.values[2]
                val speed: Float = abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000
                if (speed > shakeThreshold) {
                    findViewById<TextView>(R.id.status).text = "SHAKE DETECTED"
                } else {
                    findViewById<TextView>(R.id.status).text = ""
                }
                last_x = x
                last_y = y
                last_z = z
            }
        }*/
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
    }

}