package whatitis.ebo96.pl.ui.presenter.providers

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import whatitis.ebo96.pl.data.QuestionLocalRepository
import whatitis.ebo96.pl.ui.presenter.QuestionViewModel

class QuestionViewModelProvider(private val questionLocalRepository: QuestionLocalRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return QuestionViewModel(questionLocalRepository) as T
    }
}