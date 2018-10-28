package whatitis.ebo96.pl.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap

@Entity(tableName = "questions")
class Question(@ColumnInfo(name = "description") var description: String) {

    @Ignore
    var photo: Bitmap? = null

    @ColumnInfo(name = "photoId")
    lateinit var photoId: String

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @ColumnInfo(name = "selected")
    var selected: Boolean = false
}
