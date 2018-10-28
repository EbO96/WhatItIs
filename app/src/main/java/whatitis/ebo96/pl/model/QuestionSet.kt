package whatitis.ebo96.pl.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
class QuestionSet(
        @PrimaryKey
        @ColumnInfo(name = "setId") val setId: Int,
        @ColumnInfo(name = "questionId") val questionId: Int)