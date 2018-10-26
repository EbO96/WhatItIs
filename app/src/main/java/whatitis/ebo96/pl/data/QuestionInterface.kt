package whatitis.ebo96.pl.data

import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import android.widget.ImageView
import whatitis.ebo96.pl.model.Question

interface QuestionInterface {

    fun addQuestion(question: Question)

    fun deleteQuestion(question: Question)

    fun updateQuestion(question: Question)

    fun getAllQuestions(): LiveData<List<Question>>

    fun pickPhoto(target: ImageView?)

    fun setQuestion(question: Question?)

    fun getQuestion(): Question?

    fun getPhoto(id: String?): Bitmap?
}