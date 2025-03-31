package vn.linhpv.miniblogapp.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.databinding.PostDetailLayoutBinding

@AndroidEntryPoint
class PostDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = PostDetailLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val extras = intent.extras

        val title = extras?.getString("title")
        val author = extras?.getString("author")
        val uploadDate = extras?.getString("uploadDate")
        val content = extras?.getString("content")

        binding.postTitle.text = title
        binding.authorAndDateInfo.text = author + " - " + uploadDate
        binding.postContent.text = content

        val backButton = binding.backButton
        backButton.setOnClickListener {
            finish()
        }

    }

}