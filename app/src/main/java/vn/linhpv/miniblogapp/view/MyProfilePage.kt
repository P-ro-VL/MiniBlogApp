package vn.linhpv.miniblogapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.databinding.MyDetailLayoutBinding
import vn.linhpv.miniblogapp.repository.QueryPostMode
import vn.linhpv.miniblogapp.view.fragment.PostAdapter
import vn.linhpv.miniblogapp.viewmodel.ListPostViewModel
import vn.linhpv.miniblogapp.viewmodel.UserViewModel

@AndroidEntryPoint
class MyProfilePage : AppCompatActivity() {

    lateinit var binding: MyDetailLayoutBinding
    lateinit var myPostListAdapter: PostAdapter
    lateinit var starredPostListAdapter: PostAdapter

    val userViewModel: UserViewModel by viewModels()
    val listPostViewModel: ListPostViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MyDetailLayoutBinding.inflate(layoutInflater )
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        val user = MiniApplication.instance.currentUser

        binding.nameTextView.text = user?.name
        binding.handleTextView.text = user?.email

        initSignOut()

        Glide.with(applicationContext)
            .load(user?.avatar)
            .into(binding.headerImage)

        Glide.with(applicationContext)
            .load(user?.avatar)
            .into(binding.profileImage)

        myPostListAdapter = PostAdapter(
            lifecycleOwner = this,
            userViewModel
        ) {
            val i = Intent(applicationContext, PostDetailPage::class.java)
            i.putExtra("post", it.second)
            i.putExtra("user", it.first)
            startActivity(i)
        }

        starredPostListAdapter = PostAdapter(
            lifecycleOwner = this,
            userViewModel
        ) {
            val i = Intent(applicationContext, PostDetailPage::class.java)
            i.putExtra("post", it.second)
            i.putExtra("user", it.first)
            startActivity(i)
        }

        binding.myPostRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.myPostRecyclerView.adapter = myPostListAdapter

        binding.starredPostRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.starredPostRecyclerView.adapter = starredPostListAdapter

        listPostViewModel.getPosts(QueryPostMode.MY_POST, 10000,  userId = user?.id ?: "")
            .observe(this) {
                CoroutineScope(Dispatchers.IO).launch {
                    myPostListAdapter.submitData(it)

                    if(myPostListAdapter.itemCount == 0) {
                        binding.emptyState.visibility = View.VISIBLE
                    } else {
                        binding.emptyState.visibility = View.GONE
                    }
                }
            }

        listPostViewModel.getStarredPosts()
            .observe(this) {
                CoroutineScope(Dispatchers.IO).launch {
                    starredPostListAdapter.submitData(it)
                }
            }
    }

    private fun initSignOut() {
        binding.signOutButton.setOnClickListener {
            MiniApplication.instance.signOut()

            val intent = Intent(
                applicationContext,
                SignInPage::class.java
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            startActivity(intent)
        }
    }

}