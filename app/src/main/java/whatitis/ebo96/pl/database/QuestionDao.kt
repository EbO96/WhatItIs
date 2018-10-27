package whatitis.ebo96.pl.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import whatitis.ebo96.pl.model.Question
import whatitis.ebo96.pl.model.QuizScore

@Dao()
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question)

    @Delete
    fun delete(question: Question)

    @Update
    fun update(question: Question)

    @Query("SELECT * FROM table_questions")
    fun getQuestions(): LiveData<List<Question>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertQuizScore(quizScore: QuizScore)

    @Query("SELECT * FROM quizscore")
    fun getQuizScores(): LiveData<List<QuizScore>>

}