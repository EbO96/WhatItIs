package whatitis.ebo96.pl.view

import whatitis.ebo96.pl.model.Answer
import whatitis.ebo96.pl.model.Question

interface QuizGameCallback {

    fun getQuestion(questionId: Long): Pair<Question, ArrayList<Answer>>?
}