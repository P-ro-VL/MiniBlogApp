package vn.linhpv.miniblogapp.view

import android.app.ComponentCaller
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.databinding.PostCreateLayoutBinding
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.util.PersistentSnackbar
import vn.linhpv.miniblogapp.util.SnackbarType
import vn.linhpv.miniblogapp.viewmodel.ImageUploadViewModel
import vn.linhpv.miniblogapp.viewmodel.UploadPostViewModel
import java.util.UUID

@AndroidEntryPoint
class PostCreatePage : AppCompatActivity() {

    private lateinit var binding: PostCreateLayoutBinding
    private val imageUploadViewModel: ImageUploadViewModel by viewModels()
    private val uploadPostViewModel: UploadPostViewModel by viewModels()
    private var thumbnailUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.post_create_layout)

        val user = MiniApplication.instance.currentUser

        binding.uploadThumbnail.imageUploadViewModel = imageUploadViewModel
        binding.uploadThumbnail.setOnImageUploadListener { url ->
            thumbnailUrl = url
        }

        binding.uploadButton.setOnClickListener {
            val title = binding.titleInput.text.toString()
            val content = binding.contentInput.text.toString()

            if (thumbnailUrl.isNullOrEmpty()) {
                Toast.makeText(this, "Please upload a thumbnail", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (title.isEmpty()) {
                binding.titleInput.error = "Title cannot be empty"
                return@setOnClickListener
            }

            if (content.isEmpty()) {
                binding.contentInput.error = "Content cannot be empty"
                return@setOnClickListener
            }

            val post = Post()
            post.id = UUID.randomUUID().toString()
            post.thumbnail = thumbnailUrl
            post.title = title
            post.content = content
            post.userId = user?.id
            post.timestamp = Timestamp.now()

            uploadPostViewModel.uploadPost(post) {
                if(it) {
                    PersistentSnackbar.show(this@PostCreatePage, "Tạo bài viết thành công", SnackbarType.success);

                    finish()
                } else {
                    PersistentSnackbar.show(this@PostCreatePage, "Có lỗi xảy ra. Vui lòng thử lại sau.", SnackbarType.error);
                }
            }
        }
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
        caller: ComponentCaller
    ) {
        super.onActivityResult(requestCode, resultCode, data, caller)
        binding.uploadThumbnail.handleActivityResult(requestCode, resultCode, data)
    }
}