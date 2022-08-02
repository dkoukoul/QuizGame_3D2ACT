package co.d2act.quizgame

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class Section : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_section)

        showSectionContent()
        val nextButton = findViewById<Button>(R.id.section_button_next)
        nextButton.setOnClickListener {
            val questionActivity = Intent(applicationContext, Question::class.java)
            startActivity(questionActivity)
        }
    }

    private fun showSectionContent() {
        val index = Globals.getSection()
        val header = findViewById<TextView>(R.id.section_header)
        val whyText = findViewById<TextView>(R.id.section_why_text)
        val howText = findViewById<TextView>(R.id.section_how_text)
        val nextText = findViewById<TextView>(R.id.section_next_text)
        when(index) {
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

            }
            5-> {

            }
        }
    }
}