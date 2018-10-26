package whatitis.ebo96.pl.model

import android.os.Parcel
import android.os.Parcelable

class Answer(val questionId: Long, val content: String) : Parcelable {

    var correct = false

    var selected = false

    var position = 0

    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString()) {
        correct = parcel.readByte() != 0.toByte()
        selected = parcel.readByte() != 0.toByte()
        position = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(questionId)
        parcel.writeString(content)
        parcel.writeByte(if (correct) 1 else 0)
        parcel.writeByte(if (selected) 1 else 0)
        parcel.writeInt(position)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Answer> {
        override fun createFromParcel(parcel: Parcel): Answer {
            return Answer(parcel)
        }

        override fun newArray(size: Int): Array<Answer?> {
            return arrayOfNulls(size)
        }
    }


}