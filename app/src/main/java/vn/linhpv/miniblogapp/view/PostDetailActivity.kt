package vn.linhpv.miniblogapp.view

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.R

@AndroidEntryPoint
class PostDetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.post_detail_layout)

        val extras = intent.extras

        val title = extras?.getString("title")
        val author = extras?.getString("author")
        val uploadDate = extras?.getString("uploadDate")
        val content = extras?.getString("content")

        findViewById<TextView>(R.id.postTitle).text = title
        findViewById<TextView>(R.id.authorAndDateInfo).text = author + " - " + uploadDate
        findViewById<TextView>(R.id.postContent).text = content

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

    }

}