package vn.linhpv.miniblogapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.HiltAndroidApp
import vn.linhpv.miniblogapp.util.PersistentSnackbar
import vn.linhpv.miniblogapp.util.SnackbarType
import vn.linhpv.miniblogapp.view.HomeActivity

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<AppCompatButton>(R.id.signInButton).setOnClickListener {
            val username = findViewById<EditText>(R.id.usernameInput).text.toString();
            val password = findViewById<EditText>(R.id.passwordInput).text.toString();

            if (username == "admin" && password == "admin") {
                PersistentSnackbar.show(
                    this,
                    "Đăng nhập thành công",
                    SnackbarType.success
                )

                val i = Intent(
                    applicationContext,
                    HomeActivity::class.java
                )
                startActivity(i)
            } else {
                PersistentSnackbar.show(
                    this,
                    "Sai tên đăng nhập hoặc mật khẩu. Vui lòng kiểm tra lại",
                    SnackbarType.error
                )
            }
        }
    }
}