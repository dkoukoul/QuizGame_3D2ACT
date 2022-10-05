package co.d2act.quizgame

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val requestCodeAskPermissions = 1
    private val requiredSDKPermissions = arrayOf(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val buttonStart = findViewById<Button>(R.id.button_start)
        Globals.prefs = getSharedPreferences("co.d2act.quizgame", AppCompatActivity.MODE_PRIVATE)
        buttonStart.setOnClickListener {
            //Launch 1st section
            Globals.start()
            val sectionActivity = Intent(applicationContext, Section::class.java)
            startActivity(sectionActivity)
        }
        checkCacheAndResume()
    }

    private fun checkCacheAndResume() {
        if (Globals.restoreCache()) {
            if (Globals.answeredQuestions.size > 0) {
                val activity = Intent(applicationContext, Question::class.java)
                startActivity(activity)
            } else {
                val sectionActivity = Intent(applicationContext, Section::class.java)
                startActivity(sectionActivity)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        checkPermissions()

    }

    private fun checkPermissions() {
        val missingPermissions: MutableList<String> = ArrayList()
        // check all required dynamic permissions
        for (permission in requiredSDKPermissions) {
            val result = ContextCompat.checkSelfPermission(this, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (missingPermissions.isNotEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions.toTypedArray()
            ActivityCompat.requestPermissions(this, permissions, requestCodeAskPermissions)
        } else {
            val grantResults = IntArray(requiredSDKPermissions.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            onRequestPermissionsResult(requestCodeAskPermissions, requiredSDKPermissions, grantResults)
        }
    }
}