package vn.linhpv.miniblogapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.databinding.SignUpLayoutBinding
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.util.PersistentSnackbar
import vn.linhpv.miniblogapp.util.SnackbarType
import vn.linhpv.miniblogapp.viewmodel.ImageUploadViewModel
import vn.linhpv.miniblogapp.viewmodel.SignUpViewModel
import vn.linhpv.miniblogapp.widget.ImageUploadWidget
import java.util.UUID

@AndroidEntryPoint
class SignUpPage : AppCompatActivity() {

    lateinit var binding: SignUpLayoutBinding

    lateinit var imageUploadWidget: ImageUploadWidget

    var user: User = User("", "", "", "", "", "USER")

    val imageUploadViewModel: ImageUploadViewModel by viewModels()
    val signUpViewModel: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SignUpLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initUI()

        binding.signInText.setOnClickListener {
            finish()
        }

        binding.signUpButton.setOnClickListener {
            handleSignUp()
            handleSignUpObserver()
        }
    }

    private fun initUI() {
        imageUploadWidget = binding.uploadAvatar
        imageUploadWidget.imageUploadViewModel = imageUploadViewModel
        imageUploadWidget.setOnImageUploadListener(ImageUploadWidget. OnImageUploadListener {
            user.avatar = it ?: ""
        })
    }

    private fun handleSignUp() {
        user.name = binding.fullnameInput.text.toString()
        user.email = binding.emailInput.text.toString()
        user.password = binding.passwordInput.text.toString()

        if(user.name.isEmpty() || user.email.isEmpty() || user.password.isEmpty()) {
            PersistentSnackbar.show(
                this,
                "Vui lòng điền đầy đủ thông tin để đăng ký",
                SnackbarType.error
            )
            return
        }

        if(!android.util.Patterns.EMAIL_ADDRESS
                .matcher(user.email)
                .matches()) {
            PersistentSnackbar.show(
                this,
                "Vui lòng điền một email hợp lệ",
                SnackbarType.error
            )
            return
        }

        user.id = UUID.randomUUID().toString()

        binding.signUpButton.visibility = View.INVISIBLE
        binding.loadingIndicator.visibility = View.VISIBLE
    }

    private fun handleSignUpObserver() {
        signUpViewModel.signUp(user)
        signUpViewModel.signUpLiveData.observe(this) {
            if (it) {
                PersistentSnackbar.show(
                    this@SignUpPage,
                    "Đăng ký tài khoản thành công. Vui lòng đăng nhập.",
                    SnackbarType.success
                )

                finish()
            } else {
                binding.signUpButton.visibility = View.VISIBLE
                binding.loadingIndicator.visibility = View.INVISIBLE

                PersistentSnackbar.show(
                    this,
                    "Có lỗi xảy ra, vui lòng thử lại sau!",
                    SnackbarType.error
                )
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        imageUploadWidget.handleActivityResult(requestCode, resultCode, data)
    }

}