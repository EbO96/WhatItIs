package whatitis.ebo96.pl.util

import android.arch.persistence.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import whatitis.ebo96.pl.data.QuestionsRepository
import whatitis.ebo96.pl.data.QuestionsViewModel
import whatitis.ebo96.pl.database.QuestionsDatabase
import whatitis.ebo96.pl.network.BackupService
import whatitis.ebo96.pl.network.MyRetrofit

val appModule = module {

    single {
        Room.databaseBuilder(androidApplication().applicationContext,
                QuestionsDatabase::class.java,
                "database_questions")
                .fallbackToDestructiveMigration()
                .build()
                .dao()
    }

    single { MyRetrofit() }

    single { MyRetrofit().getService("http://127.0.0.1:3000/", BackupService::class.java) }

    single { QuestionsRepository(androidApplication().applicationContext, get()) }

    viewModel { params -> QuestionsViewModel(params[0], get()) }
}