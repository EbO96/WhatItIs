package whatitis.ebo96.pl.view

import whatitis.ebo96.pl.model.Answer
import whatitis.ebo96.pl.model.Question
import java.util.*

interface QuizCallback {

    fun getQuizQuestions(): ArrayList<Pair<Question, ArrayList<Answer>>>

    fun changeQuizPageIndicator(questionNumber: Int)

    fun checkQuiz()
}