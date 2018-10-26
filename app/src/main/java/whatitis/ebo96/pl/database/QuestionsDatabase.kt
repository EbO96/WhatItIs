package whatitis.ebo96.pl.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import whatitis.ebo96.pl.model.Question

@Database(entities = [Question::class], version = 1)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun dao(): QuestionDao
}