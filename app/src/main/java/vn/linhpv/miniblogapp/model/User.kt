package vn.linhpv.miniblogapp.model

import com.google.gson.annotations.SerializedName

class User {
    var id: Int? = null
    var firstName: String? = null
    var lastName: String? = null
    var email: String? = null

    @SerializedName("image")
    var avatar: String? = null
}