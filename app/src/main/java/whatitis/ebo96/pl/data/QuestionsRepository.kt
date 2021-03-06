package whatitis.ebo96.pl.data

import android.arch.lifecycle.LiveData
import android.content.Context
import whatitis.ebo96.pl.database.QuestionDao
import whatitis.ebo96.pl.model.Question

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

}