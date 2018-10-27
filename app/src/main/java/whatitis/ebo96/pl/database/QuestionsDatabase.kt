package whatitis.ebo96.pl.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import whatitis.ebo96.pl.model.Question
import whatitis.ebo96.pl.model.QuizScore

@Database(entities = [Question::class, QuizScore::class], version = 3)
abstract class QuestionsDatabase : RoomDatabase() {
    abstract fun dao(): QuestionDao
}