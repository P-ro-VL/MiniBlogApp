package vn.linhpv.miniblogapp.util

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat

fun Timestamp.format(): String {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")
    return dateFormat.format(this.toDate())
}