package co.d2act.quizgame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class ScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score)

        val score = findViewById<TextView>(R.id.score)
        score.text = Globals.getScore().toString()+"/60"

        val buttonEnd = findViewById<Button>(R.id.button_end)
        buttonEnd.setOnClickListener {
            Globals.clearCache()
            finishAffinity()
        }
    }
}