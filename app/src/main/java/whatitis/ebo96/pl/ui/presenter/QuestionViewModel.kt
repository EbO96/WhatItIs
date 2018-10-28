package whatitis.ebo96.pl.ui.presenter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import whatitis.ebo96.pl.data.QuestionLocalRepository
import whatitis.ebo96.pl.data.QuestionUserCases
import whatitis.ebo96.pl.model.Question

class QuestionViewModel(private val questionLocalRepository: QuestionLocalRepository) : ViewModel(),
        QuestionUserCases {

    val compositeDisposable = CompositeDisposable()

    var picketPhoto = MutableLiveData<Bitmap>()

    var picketPhotoBytes = MutableLiveData<ByteArray>()

    var questionToEdit = MutableLiveData<Question>()

    val deleteMode = MutableLiveData<DeleteMode>()

    fun startDeleteMode() {
        deleteMode.value = DeleteMode(questionLocalRepository)
    }

    fun stopDeleteMode() {
        deleteMode.value = null
    }

    fun isInDeleteMode() = deleteMode.value != null

    fun getIds() = Observable.fromCallable {
        (getQuestions().value?.map { question -> question.id }?.toIntArray()) ?: intArrayOf()
    }

    override fun insert(question: Question): Single<Long> = questionLocalRepository.insert(question)

    override fun delete(question: Question): Single<Int> = questionLocalRepository.delete(question)

    override fun deleteAll(): Single<Int> = questionLocalRepository.deleteAll()

    override fun deleteMultiple(questionIds: IntArray): Single<Int> = questionLocalRepository.deleteMultiple(questionIds)

    override fun update(question: Question): Single<Int> = questionLocalRepository.update(question)

    override fun deselectSelected(): Single<Int> = questionLocalRepository.deselectSelected()

    override fun getQuestions(): LiveData<List<Question>> = questionLocalRepository.getQuestions()

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}

class DeleteMode(private val questionLocalRepository: QuestionLocalRepository) {

    private val questionsIds = ArrayList<Int>()

    val photosIds = ArrayList<String>()

    fun query(question: Question) {
        if (question.selected) {
            addToQuery(question)
        } else {
            removeFromQuery(question)
        }
    }

    private fun addToQuery(question: Question) {
        questionsIds.add(question.id)
        photosIds.add(question.photoId)
    }

    private fun removeFromQuery(question: Question) {
        questionsIds.remove(question.id)
        photosIds.remove(question.photoId)
    }

    fun delete(): Single<Int> =
            questionLocalRepository.deleteMultiple(questionsIds.toIntArray())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
}
