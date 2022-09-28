package co.d2act.quizgame

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random


object Globals {
    const val CLICK = 1
    const val SHAKE = 2
    const val SPEAK = 3
    const val QRSCAN = 4
    const val COLOR = 5
    private var currentSection = 0
    private var currentQuestion = 0
    lateinit var prefs: SharedPreferences

    private val types1 = arrayListOf(SPEAK,SHAKE,CLICK)
    //private val types1 = arrayListOf(CLICK,SHAKE,QRSCAN)
    private val types2 = arrayListOf(QRSCAN,QRSCAN,CLICK)
    private val types3 = arrayListOf(SPEAK,SHAKE,CLICK)
    private val types4 = arrayListOf(QRSCAN,SPEAK,QRSCAN)
    private val types5 = arrayListOf(CLICK,COLOR,CLICK)
    private val types6 = arrayListOf(QRSCAN,SPEAK,QRSCAN)
    private val types7 = arrayListOf(SPEAK,COLOR,SHAKE)
    private val types8 = arrayListOf(QRSCAN,CLICK,COLOR)
    private val types9 = arrayListOf(SPEAK,COLOR,CLICK)
    private val types10 = arrayListOf(SHAKE,CLICK,QRSCAN)

    //ALL CLICK for DEBUGGING
/*    val types1 = arrayListOf(CLICK,CLICK,CLICK)
    val types2 = arrayListOf(CLICK,CLICK,CLICK)
    val types3 = arrayListOf(CLICK,CLICK,CLICK)
    val types4 = arrayListOf(CLICK,CLICK,CLICK)
    val types5 = arrayListOf(CLICK,CLICK,CLICK)*/
    val questionTypes = arrayListOf(types1,types2,types3,types4,types5,types6,types7,types8,types9,types10)
    private val answers1 = arrayListOf(3,3,3)
    private val answers2 = arrayListOf(0,2,2) //2.1 requires two answers
    private val answers3 = arrayListOf(1,2,3)
    private val answers4 = arrayListOf(1,3,2)
    private val answers5 = arrayListOf(2,1,1)
    private val answers6 = arrayListOf(1,1,2)
    private val answers7 = arrayListOf(2,3,2)
    private val answers8 = arrayListOf(1,3,1)
    private val answers9 = arrayListOf(1,2,1)
    private val answers10 = arrayListOf(1,1,1)
    val answers = arrayListOf(answers1, answers2, answers3, answers4, answers5, answers6, answers7, answers8, answers9, answers10)
    var answeredQuestions : ArrayList<Int> = arrayListOf()
    private var score = 0

    fun start() {
        currentSection = 1
        currentQuestion = 1
    }

    fun firstQuestion() {
        currentQuestion = 1
    }

    fun nextSection() {
        answeredQuestions.clear()
        currentSection++
    }

    fun nextQuestion() {
        if (answeredQuestions.size == 3) {
            currentQuestion = 1
            nextSection()
        } else {
            currentQuestion++
        }
    }

    fun getSection(): Int {
        return currentSection
    }

    fun getQuestion(): Int {
        return currentQuestion
    }

    fun getRandomQuestion(): Int {
        currentQuestion = Random.nextInt(1,4)
        if (!answeredQuestions.contains(currentQuestion))
            answeredQuestions.add(currentQuestion)
        else
            getRandomQuestion()

        return currentQuestion
    }

    fun addScore(firstAttempt: Boolean) {
        score += if (firstAttempt) {
            2
        } else {
            1
        }
    }

    fun getScore():Int {
        return score
    }

    fun saveCache() {
        prefs.edit().putInt("section", currentSection).apply()
        prefs.edit().putInt("question", currentQuestion).apply()
        prefs.edit().putInt("score", score).apply()
        prefs.edit().putString("answeredQuestions", answeredQuestions.toString()).apply()
    }

    fun clearCache() {
        prefs.edit().clear().apply()
    }

    fun restoreCache():Boolean {
        return if (prefs.contains("section")) {
            currentSection = prefs.getInt("section", 1)
            currentQuestion = prefs.getInt("question", 1)
            score = prefs.getInt("score", 0)
            answeredQuestions = prefs.getString("answeredQuestions", "")!!.split(",") as ArrayList<Int>
            true
        } else {
            false
        }
    }
}