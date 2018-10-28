package whatitis.ebo96.pl.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import whatitis.ebo96.pl.model.Question

@Dao()
interface QuestionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(question: Question): Long

    @Delete
    fun delete(question: Question): Int

    @Query("DELETE FROM questions")
    fun deleteAll(): Int

    @Query("DELETE FROM questions WHERE id IN(:questionIds)")
    fun deleteMultiple(questionIds: IntArray): Int

    @Update
    fun update(question: Question): Int

    @Query("UPDATE questions SET selected = 0 WHERE selected = 1")
    fun deselectSelected(): Int

    @Query("SELECT * FROM questions")
    fun getQuestions(): LiveData<List<Question>>

}
