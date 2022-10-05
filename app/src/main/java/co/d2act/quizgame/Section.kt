package co.d2act.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Button
import android.widget.ImageView
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
        val icon = findViewById<ImageView>(R.id.section_icon)
        val whyText = findViewById<TextView>(R.id.section_why_text)
        val howText = findViewById<TextView>(R.id.section_how_text)
        val nextText = findViewById<TextView>(R.id.section_next_text)
        when(Globals.getSection()) {
            1-> {
                header.text = getString(R.string.section1_title)
                whyText.text = getString(R.string.section1_why_text)
                howText.text = getString(R.string.section1_how_text)
                nextText.text = getString(R.string.section1_next_text)
                icon.setImageResource(R.drawable.s1_icon)
            }
            2-> {
                header.text = getString(R.string.section2_title)
                whyText.text = getString(R.string.section2_why_text)
                howText.text = getString(R.string.section2_how_text)
                nextText.text = getString(R.string.section2_next_text)
                icon.setImageResource(R.drawable.s2_icon)
            }
            3-> {
                header.text = getString(R.string.section3_title)
                whyText.text = getString(R.string.section3_why_text)
                howText.text = getString(R.string.section3_how_text)
                nextText.text = getString(R.string.section3_next_text)
                icon.setImageResource(R.drawable.s3_icon)
            }
            4-> {
                header.text = getString(R.string.section4_title)
                whyText.text = getString(R.string.section4_why_text)
                howText.text = getString(R.string.section4_how_text)
                nextText.text = getString(R.string.section4_next_text)
                icon.setImageResource(R.drawable.s4_icon)
            }
            5-> {
                header.text = getString(R.string.section5_title)
                whyText.text = getString(R.string.section5_why_text)
                howText.text = getString(R.string.section5_how_text)
                nextText.text = getString(R.string.section5_next_text)
                icon.setImageResource(R.drawable.s5_icon)
            }
            6-> {
                header.text = getString(R.string.section6_title)
                whyText.text = getString(R.string.section6_why_text)
                howText.text = getString(R.string.section6_how_text)
                nextText.text = getString(R.string.section6_next_text)
                icon.setImageResource(R.drawable.s6_icon)
            }
            7-> {
                header.text = getString(R.string.section7_title)
                whyText.text = getString(R.string.section7_why_text)
                howText.text = getString(R.string.section7_how_text)
                nextText.text = getString(R.string.section7_next_text)
                icon.setImageResource(R.drawable.s7_icon)
            }
            8-> {
                header.text = getString(R.string.section8_title)
                whyText.text = getString(R.string.section8_why_text)
                howText.text = getString(R.string.section8_how_text)
                nextText.text = getString(R.string.section8_next_text)
                icon.setImageResource(R.drawable.s8_icon)
            }
            9-> {
                header.text = getString(R.string.section9_title)
                whyText.text = getString(R.string.section9_why_text)
                howText.text = getString(R.string.section9_how_text)
                nextText.text = getString(R.string.section9_next_text)
                icon.setImageResource(R.drawable.s9_icon)
            }
            10-> {
                header.text = getString(R.string.section10_title)
                whyText.text = getString(R.string.section10_why_text)
                howText.text = getString(R.string.section10_how_text)
                nextText.text = getString(R.string.section10_next_text)
                icon.setImageResource(R.drawable.s10_icon)
            }
        }
    }

    override fun onBackPressed() {
        if(doubleBackToExitPressedOnce) {
            //clear cache
            Globals.clearCache()
            this.finishAffinity()
            return
        }
        doubleBackToExitPressedOnce = true
        Toast.makeText(this, getString(R.string.toast_back_button), Toast.LENGTH_SHORT).show()
        Handler().postDelayed({ doubleBackToExitPressedOnce = false }, 2000)
    }
}