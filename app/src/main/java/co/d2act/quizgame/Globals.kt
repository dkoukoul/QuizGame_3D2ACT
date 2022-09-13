package co.d2act.quizgame

import kotlin.random.Random


object Globals {
    const val CLICK = 1
    const val SHAKE = 2
    const val SPEAK = 3
    const val SCAN = 4
    const val COLOR = 5
    private var currentSection = 0
    private var currentQuestion = 0

    //private val types1 = arrayListOf(CLICK,SHAKE,SCAN)
    private val types1 = arrayListOf(COLOR,SHAKE,SCAN)
    private val types2 = arrayListOf(SCAN,SCAN,CLICK)
    private val types3 = arrayListOf(SPEAK,SHAKE,CLICK)
    private val types4 = arrayListOf(SCAN,SPEAK,SCAN)
    private val types5 = arrayListOf(CLICK,COLOR,CLICK)
    //ALL CLICK for DEBUGGING
/*    val types1 = arrayListOf(CLICK,CLICK,CLICK)
    val types2 = arrayListOf(CLICK,CLICK,CLICK)
    val types3 = arrayListOf(CLICK,CLICK,CLICK)
    val types4 = arrayListOf(CLICK,CLICK,CLICK)
    val types5 = arrayListOf(CLICK,CLICK,CLICK)*/
    val questionTypes = arrayListOf(types1,types2,types3,types4,types5)
    private val answers1 = arrayListOf(3,3,3)
    private val answers2 = arrayListOf(2,2,2)
    private val answers3 = arrayListOf(1,2,3)
    private val answers4 = arrayListOf(1,3,2)
    private val answers5 = arrayListOf(2,1,1)
    val answers = arrayListOf(answers1, answers2, answers3, answers4, answers5)
    private var answeredQuestions : ArrayList<Int> = arrayListOf()
    private var score = 0

    fun start() {
        currentSection = 1
        currentQuestion = 1
    }

    fun firstQuestion() {
        currentQuestion = 1
    }

    private fun nextSection() {
        currentSection++
    }

    fun nextQuestion() {
        if (currentQuestion == 3) {
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
        //get next question excluding the answered ones
        while (!answeredQuestions.contains(currentQuestion)) {
            currentQuestion = Random.nextInt(1,3)
        }
        return currentQuestion
    }


    fun goBack() {
        if (currentQuestion==1) {
            if (currentSection > 1) {
                currentSection--
                currentQuestion = 3
            }
        } else {
            currentQuestion--
        }
    }

    fun addScore(firstAttempt: Boolean) {
        score += if (firstAttempt) {
            2
        } else {
            1
        }
    }
}