package whatitis.ebo96.pl.ui.fragment

import android.support.v7.app.AppCompatActivity
import whatitis.ebo96.pl.dagger.components.DaggerQuestionComponent
import whatitis.ebo96.pl.dagger.components.QuestionComponent
import whatitis.ebo96.pl.dagger.modules.QuestionModule
import whatitis.ebo96.pl.view.LifecycleFragment

open class QuestionFragment : LifecycleFragment() {

    companion object {

        fun getQuestionViewModel(activity: AppCompatActivity) =
                getQuestionComponent(activity).getQuestionViewModel()

        private fun getQuestionComponent(activity: AppCompatActivity): QuestionComponent {
            return DaggerQuestionComponent.builder().questionModule(QuestionModule(activity)).build().also {
                it.inject(activity)
            }
        }
    }

}