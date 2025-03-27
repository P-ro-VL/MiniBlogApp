package vn.linhpv.miniblogapp.util

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import vn.linhpv.miniblogapp.R


enum class SnackbarType {
    info,
    error,
    success
}

class PersistentSnackbar {
    companion object {
        fun show(activity: Activity, message: String, messageType: SnackbarType, durationMillis: Long = 5000L) {

            val inflater: LayoutInflater = activity.getLayoutInflater()

            val layout: View = inflater.inflate(
                R.layout.snackbar,
                null
            )

            val text = layout.findViewById<View>(R.id.text) as TextView
            text.setText(message)

            val toast = Toast(activity.applicationContext)

            layout.setBackgroundColor(
                when(messageType) {
                    SnackbarType.info -> Color.BLUE
                    SnackbarType.success -> Color.GREEN
                    SnackbarType.error -> Color.RED
                }
            )

            toast.setGravity(Gravity.TOP or Gravity.LEFT or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.duration = Toast.LENGTH_LONG
            toast.setView(layout)
            toast.show()
        }
    }
}