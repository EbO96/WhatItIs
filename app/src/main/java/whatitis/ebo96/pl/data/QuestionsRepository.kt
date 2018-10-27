package whatitis.ebo96.pl.data

import android.arch.lifecycle.LiveData
import android.content.Context
import whatitis.ebo96.pl.database.QuestionDao
import whatitis.ebo96.pl.model.Question
import whatitis.ebo96.pl.model.QuizScore

class QuestionsRepository(private val ctx: Context, private val questionDao: QuestionDao) {

    fun addQuestion(question: Question) {
        questionDao.insert(question)
    }

    fun deleteQuestion(question: Question) {
        questionDao.delete(question)
    }

    fun updateQuestion(question: Question) {
        questionDao.update(question)
    }

    fun getAllQuestions(): LiveData<List<Question>> = questionDao.getQuestions()

    fun saveQuizScore(quizScore: QuizScore) = questionDao.insertQuizScore(quizScore)

    fun getQuizScores(): LiveData<List<QuizScore>> = questionDao.getQuizScores()
}