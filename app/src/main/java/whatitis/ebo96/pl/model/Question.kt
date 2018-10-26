package whatitis.ebo96.pl.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import android.graphics.Bitmap
import android.os.Parcel
import android.os.Parcelable

@Entity(tableName = "table_questions")
class Question(
        var description: String,
        var questionId: Long) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    @Ignore
    var photo: Bitmap? = null

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readLong()) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(description)
        parcel.writeLong(questionId)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Question> {
        override fun createFromParcel(parcel: Parcel): Question {
            return Question(parcel)
        }

        override fun newArray(size: Int): Array<Question?> {
            return arrayOfNulls(size)
        }
    }

}