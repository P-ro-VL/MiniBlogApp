package vn.linhpv.miniblogapp.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.databinding.SignInLayoutBinding
import vn.linhpv.miniblogapp.util.PersistentSnackbar
import vn.linhpv.miniblogapp.util.SnackbarType
import vn.linhpv.miniblogapp.viewmodel.SignInViewModel

@AndroidEntryPoint
class SignInPage : AppCompatActivity() {

    lateinit var binding: SignInLayoutBinding

    val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val loadCachedUserResult = MiniApplication.instance.loadUser()
        if(loadCachedUserResult) {
            navigateToHome()
        }

        binding = SignInLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signUpText.setOnClickListener {
            navigateToSignUp()
        }

        binding.signInButton.setOnClickListener {
            handleRegister()
        }
    }

    private fun navigateToHome() {
        val i = Intent(
            this@SignInPage,
            MainLayout::class.java
        )
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)
    }

    private fun navigateToSignUp() {
        val i = Intent(
            this@SignInPage,
            SignUpPage::class.java
        )
        startActivity(i)
    }

    private fun handleRegister() {
        val email = binding.usernameInput.text.toString()
        val password = binding.passwordInput.text.toString()

        if(email.isEmpty() || password.isEmpty()) {
            PersistentSnackbar.show(
                this,
                "Vui lòng điền đầy đủ thông tin đăng nhập để tiếp tục",
                SnackbarType.error)
            return
        }

        handleObserver(email, password)
    }

    private fun handleObserver(email: String, password: String) {
        signInViewModel.signIn(email, password)
        signInViewModel.userLiveData.observe(this) {
            if(it == null) {
                PersistentSnackbar.show(
                    this,
                    "Tài khoản hoặc mật khẩu không đúng",
                    SnackbarType.error)
                return@observe
            }

            MiniApplication.instance.currentUser = it

            runOnUiThread {
                val i = Intent(
                    this@SignInPage,
                    MainLayout::class.java
                )
                startActivity(i)

                MiniApplication.instance.saveUser()
            }
        }
    }

}