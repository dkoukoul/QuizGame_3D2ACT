package co.d2act.quizgame


object Globals {
    val CLICK = 1
    val SHAKE = 2
    val SPEAK = 3
    val SCAN = 4
    val COLOR = 5
    private var currentSection = 0
    private var currentQuestion = 0
    var lastAnswer = 0

    /*val types1 = arrayListOf(CLICK,SHAKE,SCAN)
    val types2 = arrayListOf(SCAN,COLOR,CLICK)
    val types3 = arrayListOf(SPEAK,SHAKE,CLICK)
    val types4 = arrayListOf(SCAN,SPEAK,SCAN)
    val types5 = arrayListOf(CLICK,COLOR,CLICK)*/
    //ALL CLICK for DEBUGGING
    val types1 = arrayListOf(CLICK,CLICK,CLICK)
    val types2 = arrayListOf(CLICK,CLICK,CLICK)
    val types3 = arrayListOf(CLICK,CLICK,CLICK)
    val types4 = arrayListOf(CLICK,CLICK,CLICK)
    val types5 = arrayListOf(CLICK,CLICK,CLICK)
    val questionTypes = arrayListOf(types1,types2,types3,types4,types5)
    val answers1 = arrayListOf(3,3,3)
    val answers2 = arrayListOf(2,2,2)
    val answers3 = arrayListOf(1,2,3)
    val answers4 = arrayListOf(1,3,2)
    val answers5 = arrayListOf(2,1,1)
    val answers = arrayListOf(answers1, answers2, answers3, answers4, answers5)

    fun start() {
        currentSection = 1
        currentQuestion = 1
    }

    fun firstQuestion() {
        currentQuestion = 1
    }

    fun nextSection() {
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
}