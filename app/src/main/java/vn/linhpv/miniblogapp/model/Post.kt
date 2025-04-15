package vn.linhpv.miniblogapp.model

import android.os.Parcel
import android.os.Parcelable
import com.google.firebase.Timestamp

class Post(
    var id: String? = null,
    var title: String? = null,
    var content: String? = null,
    var timestamp: Timestamp? = null,
    var thumbnail: String? = null,
    var userId: String? = null,

    var author: User? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(Timestamp::class.java.classLoader),
        parcel.readString(),
        parcel.readString(),
        parcel.readParcelable(User::class.java.classLoader)
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(content)
        parcel.writeParcelable(timestamp, flags)
        parcel.writeString(thumbnail)
        parcel.writeString(userId)
        parcel.writeParcelable(author, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Post> {
        override fun createFromParcel(parcel: Parcel): Post {
            return Post(parcel)
        }

        override fun newArray(size: Int): Array<Post?> {
            return arrayOfNulls(size)
        }
    }
}