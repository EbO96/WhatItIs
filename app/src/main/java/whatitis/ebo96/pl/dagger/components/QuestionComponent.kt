package whatitis.ebo96.pl.dagger.components

import android.support.v7.app.AppCompatActivity
import dagger.Component
import whatitis.ebo96.pl.dagger.modules.QuestionModule
import whatitis.ebo96.pl.ui.presenter.QuestionViewModel
import javax.inject.Singleton

@Singleton
@Component(modules = [QuestionModule::class])
interface QuestionComponent {

    fun inject(activity: AppCompatActivity)

    fun getQuestionViewModel(): QuestionViewModel
}