package whatitis.ebo96.pl.data

import android.arch.lifecycle.LiveData
import io.reactivex.Single
import whatitis.ebo96.pl.database.QuestionDao
import whatitis.ebo96.pl.model.Question

class QuestionLocalRepository(private val questionDao: QuestionDao) : QuestionUserCases {

    override fun insert(question: Question): Single<Long> = Single.fromCallable<Long> {
        questionDao.insert(question)
    }

    override fun delete(question: Question): Single<Int> = Single.fromCallable<Int> {
        questionDao.delete(question)
    }

    override fun deleteAll(): Single<Int> = Single.fromCallable<Int> {
        questionDao.deleteAll()
    }

    override fun deleteMultiple(questionIds: IntArray): Single<Int> = Single.fromCallable<Int> {
        questionDao.deleteMultiple(questionIds)
    }

    override fun update(question: Question): Single<Int> = Single.fromCallable<Int> {
        questionDao.update(question)
    }

    override fun deselectSelected(): Single<Int> = Single.fromCallable<Int> {
        questionDao.deselectSelected()
    }

    override fun getQuestions(): LiveData<List<Question>> = questionDao.getQuestions()

}

interface QuestionUserCases {

    fun insert(question: Question): Single<Long>
    fun delete(question: Question): Single<Int>
    fun deleteAll(): Single<Int>
    fun deleteMultiple(questionIds: IntArray): Single<Int>
    fun update(question: Question): Single<Int>
    fun deselectSelected(): Single<Int>
    fun getQuestions(): LiveData<List<Question>>
}