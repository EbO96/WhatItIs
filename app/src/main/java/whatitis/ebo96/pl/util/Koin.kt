package whatitis.ebo96.pl.util

import android.arch.persistence.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import whatitis.ebo96.pl.data.QuestionsRepository
import whatitis.ebo96.pl.data.QuestionsViewModel
import whatitis.ebo96.pl.database.QuestionsDatabase

val appModule = module {

    single {
        Room.databaseBuilder(androidApplication().applicationContext,
                QuestionsDatabase::class.java,
                "database_questions")
                .fallbackToDestructiveMigration()
                .build()
                .dao()
    }

    single { QuestionsRepository(androidApplication().applicationContext, get()) }

    viewModel { params -> QuestionsViewModel(params[0], get()) }
}