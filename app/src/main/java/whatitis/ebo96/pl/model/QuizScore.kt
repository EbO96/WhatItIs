package whatitis.ebo96.pl.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.os.Parcel
import android.os.Parcelable

@Entity
class QuizScore(
        @ColumnInfo(name = "time") var time: Long = -1L,
        @ColumnInfo(name = "points") var points: Int = 0,
        @ColumnInfo(name = "maxPoints") var maxPoints: Int = 0) : Parcelable {

    @PrimaryKey(autoGenerate = true)
    var id: Int = 0

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt()) {
        id = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(time)
        parcel.writeInt(points)
        parcel.writeInt(maxPoints)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<QuizScore> {
        override fun createFromParcel(parcel: Parcel): QuizScore {
            return QuizScore(parcel)
        }

        override fun newArray(size: Int): Array<QuizScore?> {
            return arrayOfNulls(size)
        }
    }

    fun getPercent() = "${((points / maxPoints.let { if (it == 0) 1 else it }.toFloat() ) * 100).toInt()}%"

}