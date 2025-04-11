package vn.linhpv.miniblogapp.util

import android.app.Activity
import android.graphics.Color
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.Toast
import vn.linhpv.miniblogapp.databinding.SnackbarBinding

enum class SnackbarType {
    info,
    error,
    success
}

class PersistentSnackbar {
    companion object {
        fun show(activity: Activity, message: String, messageType: SnackbarType, durationMillis: Long = 5000L) {

            val inflater: LayoutInflater = activity.getLayoutInflater()

            val binding = SnackbarBinding.inflate(inflater)

            val text = binding.text
            text.setText(message)

            val toast = Toast(activity.applicationContext)

            binding.root.setBackgroundColor(
                when(messageType) {
                    SnackbarType.info -> Color.BLUE
                    SnackbarType.success -> Color.GREEN
                    SnackbarType.error -> Color.RED
                }
            )

            toast.setGravity(Gravity.TOP or Gravity.LEFT or Gravity.FILL_HORIZONTAL, 0, 0)
            toast.duration = Toast.LENGTH_LONG
            toast.setView(binding.root)
            toast.show()
        }
    }
}