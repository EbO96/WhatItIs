package whatitis.ebo96.pl

import android.app.Application
import org.koin.android.ext.android.startKoin
import org.koin.log.EmptyLogger
import whatitis.ebo96.pl.util.appModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin(this, modules = listOf(appModule), logger = EmptyLogger())
    }
}