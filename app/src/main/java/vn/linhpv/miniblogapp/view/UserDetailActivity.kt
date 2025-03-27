package vn.linhpv.miniblogapp.view

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.view.fragment.ListPostAdapter
import vn.linhpv.miniblogapp.viewmodel.UserPostsViewModel

@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {

    lateinit var listView: RecyclerView
    lateinit var listViewAdapter: ListPostAdapter

    val viewModel: UserPostsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupUserInformation()

        listView = findViewById(R.id.userPostList)
        listView.layoutManager = LinearLayoutManager(applicationContext)
        listViewAdapter = ListPostAdapter(applicationContext, true)
        listView.adapter = listViewAdapter

        viewModel.query(intent.extras?.getInt("userId") ?: 0, this)
            .observe(this) {
                listViewAdapter.submitData(lifecycle, it)
            }

        listViewAdapter.addLoadStateListener {
           if(it.refresh !is LoadState.Loading) {
               findViewById<TextView>(R.id.userHasNoPost).visibility =
                   if (listViewAdapter.itemCount == 0) View.VISIBLE else View.GONE
           }
        }

    }

    fun setupUserInformation() {
        val extras = intent.extras

        setContentView(R.layout.user_detail_layout)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val userAvatar = findViewById<ShapeableImageView>(R.id.userAvatar)
        Glide.with(userAvatar.context)
            .load(extras?.getString("avatar"))
            .circleCrop()
            .into(userAvatar)

        val userDisplayName = findViewById<TextView>(R.id.userDisplayName)
        val displayName = extras?.getString("userName")
        userDisplayName.text = displayName

        val userEmail = findViewById<TextView>(R.id.userEmail)
        userEmail.text = extras?.getString("email")
    }

}