package co.d2act.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

@Suppress("DEPRECATION")
class Section : AppCompatActivity() {
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        showSectionContent()
        val nextButton = findViewById<Button>(R.id.section_button_next)
        nextButton.setOnClickListener {
            Globals.firstQuestion()
            val questionActivity = Intent(applicationContext, Question::class.java)
            startActivity(questionActivity)
        }
    }

    private fun showSectionContent() {
        val header = findViewById<TextView>(R.id.section_header)
        val whyText = findViewById<TextView>(R.id.section_why_text)
        val howText = findViewById<TextView>(R.id.section_how_text)
        val nextText = findViewById<TextView>(R.id.section_next_text)
        when(Globals.getSection()) {
            1-> {
                header.text = getString(R.string.section1_title)
                whyText.text = getString(R.string.section1_why_text)
                howText.text = getString(R.string.section1_how_text)
                nextText.text = getString(R.string.section1_next_text)
            }
            2-> {
                header.text = getString(R.string.section2_title)
                whyText.text = getString(R.string.section2_why_text)
                howText.text = getString(R.string.section2_how_text)
                nextText.text = getString(R.string.section2_next_text)
            }
            3-> {
                header.text = getString(R.string.section3_title)
                whyText.text = getString(R.string.section3_why_text)
                howText.text = getString(R.string.section3_how_text)
                nextText.text = getString(R.string.section3_next_text)
            }
            4-> {
                header.text = getString(R.string.section4_title)
                whyText.text = getString(R.string.section4_why_text)
                howText.text = getString(R.string.section4_how_text)
                nextText.text = getString(R.string.section4_next_text)
            }
            5-> {
                header.text = getString(R.string.section5_title)
                whyText.text = getString(R.string.section5_why_text)
                howText.text = getString(R.string.section5_how_text)
                nextText.text = getString(R.string.section5_next_text)
            }
            6-> {
                header.text = getString(R.string.section5_title)
                whyText.text = getString(R.string.section5_why_text)
                howText.text = getString(R.string.section5_how_text)
                nextText.text = getString(R.string.section5_next_text)
            }
            7-> {
                header.text = getString(R.string.section5_title)
                whyText.text = getString(R.string.section5_why_text)
                howText.text = getString(R.string.section5_how_text)
                nextText.text = getString(R.string.section5_next_text)
            }
            8-> {
                header.text = getString(R.string.section5_title)
                whyText.text = getString(R.string.section5_why_text)
                howText.text = getString(R.string.section5_how_text)
                nextText.text = getString(R.string.section5_next_text)
            }
            9-> {
                header.text = getString(R.string.section5_title)
                whyText.text = getString(R.string.section5_why_text)
                howText.text = getString(R.string.section5_how_text)
                nextText.text = getString(R.string.section5_next_text)
            }
            10-> {
                header.text = getString(R.string.section5_title)
                whyText.text = getString(R.string.section5_why_text)
                howText.text = getString(R.string.section5_how_text)
                nextText.text = getString(R.string.section5_next_text)
            }
        }
    }

    override fun onBackPressed() {
/*        super.onBackPressed()
        if (Globals.getSection() > 1) {
            Globals.goBack()
            val questionActivity = Intent(applicationContext, Question::class.java)
            startActivity(questionActivity)
        }*/

        if(doubleBackToExitPressedOnce) {
            /*super.onBackPressed()*/
            this.finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.toast_back_button), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}