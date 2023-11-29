package history

import android.os.Parcel
import android.os.Parcelable

data class PolyLines(val stringList: List<String>) : Parcelable {
    constructor(parcel: Parcel) : this(parcel.createStringArrayList() ?: emptyList())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringList(stringList)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<PolyLines> {
        override fun createFromParcel(parcel: Parcel): PolyLines {
            return PolyLines(parcel)
        }

        override fun newArray(size: Int): Array<PolyLines?> {
            return arrayOfNulls(size)
        }
    }
}