package vn.linhpv.miniblogapp.view

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

        initUploadImage()

        binding.uploadButton.setOnClickListener {
            val result = uploadAction()
            if (result != null)
                handleObserver(result)
        }

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun uploadAction(): Post? {
        val title = binding.titleInput.text.toString()
        val content = binding.contentInput.text.toString()

        if (thumbnailUrl.isNullOrEmpty()) {
            Toast.makeText(this, "Please upload a thumbnail", Toast.LENGTH_SHORT).show()
            return null
        }

        if (title.isEmpty()) {
            binding.titleInput.error = "Title cannot be empty"
            return null
        }

        if (content.isEmpty()) {
            binding.contentInput.error = "Content cannot be empty"
            return null
        }

        val post = Post()
        post.id = UUID.randomUUID().toString()
        post.thumbnail = thumbnailUrl
        post.title = title
        post.content = content
        post.userId = MiniApplication.instance.currentUser?.id
        post.timestamp = Timestamp.now()

        return post
    }

    private fun handleObserver(post: Post) {
        uploadPostViewModel.uploadPost(post)
        uploadPostViewModel.uploadResultLiveData.observe(this) {
            if(it) {
                PersistentSnackbar.show(this@PostCreatePage, "Tạo bài viết thành công", SnackbarType.success);

                finish()
            } else {
                PersistentSnackbar.show(this@PostCreatePage, "Có lỗi xảy ra. Vui lòng thử lại sau.", SnackbarType.error);
            }
        }
    }

    private fun initUploadImage() {
        binding.uploadThumbnail.imageUploadViewModel = imageUploadViewModel
        binding.uploadThumbnail.setOnImageUploadListener { url ->
            thumbnailUrl = url
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        binding.uploadThumbnail.handleActivityResult(requestCode, requestCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }
}