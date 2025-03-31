package vn.linhpv.miniblogapp.view

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.databinding.UserDetailLayoutBinding
import vn.linhpv.miniblogapp.view.fragment.ListPostAdapter
import vn.linhpv.miniblogapp.viewmodel.UserPostsViewModel

@AndroidEntryPoint
class UserDetailActivity : AppCompatActivity() {

    lateinit var binding: UserDetailLayoutBinding

    lateinit var listView: RecyclerView
    lateinit var listViewAdapter: ListPostAdapter

    val viewModel: UserPostsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UserDetailLayoutBinding.inflate(layoutInflater)

        setupUserInformation()

        listView = binding.userPostList
        listView.layoutManager = LinearLayoutManager(applicationContext)
        listViewAdapter = ListPostAdapter(applicationContext, true)
        listView.adapter = listViewAdapter

        viewModel.query(intent.extras?.getInt("userId") ?: 0, this)
            .observe(this) {
                listViewAdapter.submitData(lifecycle, it)
            }

        listViewAdapter.addLoadStateListener {
           if(it.refresh !is LoadState.Loading) {
               binding.userHasNoPost.visibility =
                   if (listViewAdapter.itemCount == 0) View.VISIBLE else View.GONE
           }
        }

    }

    fun setupUserInformation() {
        val extras = intent.extras

        val backButton = binding.backButton
        backButton.setOnClickListener {
            finish()
        }

        val userAvatar = binding.userAvatar
        Glide.with(userAvatar.context)
            .load(extras?.getString("avatar"))
            .circleCrop()
            .into(userAvatar)

        val userDisplayName = binding.userDisplayName
        val displayName = extras?.getString("userName")
        userDisplayName.text = displayName

        val userEmail = binding.userEmail
        userEmail.text = extras?.getString("email")
    }

}