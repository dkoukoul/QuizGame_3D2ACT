package co.d2act.quizgame

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
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
            //Ask user if they want to resume
            askToResume()
        }
    }

    private fun askToResume() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.dialog_resume_title))
        builder.setMessage(getString(R.string.dialog_resume_message))
        builder.setPositiveButton(getString(R.string.dialog_resume_resume)) { dialog, _ ->
            dialog.dismiss()
            if (Globals.answeredQuestions.size > 0) {
                val activity = Intent(applicationContext, Question::class.java)
                startActivity(activity)
            } else {
                val sectionActivity = Intent(applicationContext, Section::class.java)
                startActivity(sectionActivity)
            }
        }
        builder.setNegativeButton(getString(R.string.dialog_resume_restart)) { dialog, _ ->
            dialog.dismiss()
            Globals.clearCache()
            Globals.start()
            val sectionActivity = Intent(applicationContext, Section::class.java)
            startActivity(sectionActivity)
        }

        val alert: AlertDialog = builder.create()
        alert.show()
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