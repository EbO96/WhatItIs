package whatitis.ebo96.pl.dagger.modules

import android.arch.lifecycle.ViewModelProviders
import android.arch.persistence.room.Room
import android.support.v7.app.AppCompatActivity
import dagger.Module
import dagger.Provides
import whatitis.ebo96.pl.data.QuestionLocalRepository
import whatitis.ebo96.pl.database.QuestionDao
import whatitis.ebo96.pl.database.QuestionsDatabase
import whatitis.ebo96.pl.ui.presenter.QuestionViewModel
import whatitis.ebo96.pl.ui.presenter.providers.QuestionViewModelProvider
import javax.inject.Singleton

@Singleton
@Module
class QuestionModule(private val activity: AppCompatActivity) {

    @Singleton
    @Provides
    fun provideQuestionDatabase(): QuestionsDatabase = Room
            .databaseBuilder(activity, QuestionsDatabase::class.java, "question_database")
            .fallbackToDestructiveMigration()
            .build()

    @Singleton
    @Provides
    fun provideQuestionDao(questionsDatabase: QuestionsDatabase) = questionsDatabase.dao()

    @Provides
    fun provideQuestionViewModel(questionLocalRepository: QuestionLocalRepository): QuestionViewModel {
        return ViewModelProviders.of(activity, QuestionViewModelProvider(questionLocalRepository))
                .get(QuestionViewModel::class.java)
    }

    @Singleton
    @Provides
    fun provideQuestionLocalRepository(questionDao: QuestionDao): QuestionLocalRepository {
        return QuestionLocalRepository(questionDao)
    }
}