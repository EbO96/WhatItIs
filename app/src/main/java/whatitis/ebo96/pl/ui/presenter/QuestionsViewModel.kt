package whatitis.ebo96.pl.ui.presenter

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.ViewModel
import android.content.Intent
import android.graphics.Bitmap
import android.widget.ImageView
import whatitis.ebo96.pl.data.QuestionInterface
import whatitis.ebo96.pl.data.QuestionsRepository
import whatitis.ebo96.pl.model.Question
import whatitis.ebo96.pl.model.QuizScore
import whatitis.ebo96.pl.view.ActivityInteractions
import whatitis.ebo96.pl.ui.activity.MainActivity

class QuestionsViewModel(private val activityInteractions: ActivityInteractions,
                         private val questionsRepository: QuestionsRepository) : ViewModel(), QuestionInterface {

    private var question: Question? = null

    var photosMap: Map<String, Bitmap>? = null

    override fun addQuestion(question: Question) {
        questionsRepository.addQuestion(question)
    }

    override fun deleteQuestion(question: Question) {
        questionsRepository.deleteQuestion(question)
    }

    override fun updateQuestion(question: Question) {
        questionsRepository.updateQuestion(question)
    }

    override fun getAllQuestions(): LiveData<List<Question>> {
        return questionsRepository.getAllQuestions()
    }

    override fun pickPhoto(target: ImageView?) {
        performPhotoSearch()
    }

    override fun setQuestion(question: Question?) {
        this.question = question
    }

    override fun getQuestion(): Question? = question

    override fun getPhoto(id: String?): Bitmap? = photosMap?.get(id)

    /**
     * Fires an intent to spin up the "file chooser" UI and select an image.
     */
    private fun performPhotoSearch() {

        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {

            addCategory(Intent.CATEGORY_OPENABLE)

            type = "image/*"
        }

        activityInteractions.get().startActivityForResult(intent, MainActivity.PICK_PHOTO_CODE)
    }

}