package vn.linhpv.miniblogapp.view

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.databinding.UserDetailLayoutBinding
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.repository.QueryPostMode
import vn.linhpv.miniblogapp.util.PersistentSnackbar
import vn.linhpv.miniblogapp.util.SnackbarType
import vn.linhpv.miniblogapp.view.fragment.PostAdapter
import vn.linhpv.miniblogapp.viewmodel.ListPostViewModel
import vn.linhpv.miniblogapp.viewmodel.UserViewModel

@AndroidEntryPoint
class UserProfilePage : AppCompatActivity() {

    lateinit var binding: UserDetailLayoutBinding
    lateinit var postListAdapter: PostAdapter

    val userViewModel: UserViewModel by viewModels()
    val listPostViewModel: ListPostViewModel by viewModels()

    var isFollowing = false

    var user: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = UserDetailLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        user = intent.getParcelableExtra<User>("user")

        initUI()

        initAdapter()
        initRecyclerView()

        initObserver()

        binding.backButton.setOnClickListener {
            finish()
        }
    }

    private fun initUI() {
        binding.nameTextView.text = user?.name
        binding.handleTextView.text = user?.email

        initFollowingButton(user)

        Glide.with(applicationContext)
            .load(user?.avatar)
            .into(binding.headerImage)

        Glide.with(applicationContext)
            .load(user?.avatar)
            .into(binding.profileImage)
    }

    private fun initAdapter() {
        postListAdapter = PostAdapter(
            this,
            lifecycleOwner = this,
            userViewModel
        ) {
            val i = Intent(applicationContext, PostDetailPage::class.java)
            i.putExtra("post", it.second)
            i.putExtra("user", it.first)
            startActivity(i)
        }
    }

    private fun initRecyclerView() {
        binding.postRecyclerView.layoutManager = LinearLayoutManager(applicationContext)
        binding.postRecyclerView.adapter = postListAdapter
    }

    private fun initObserver() {
        listPostViewModel.getPosts(QueryPostMode.MY_POST, 10000, user?.id ?: "")
        listPostViewModel.postsLiveData.observe(this) {
                CoroutineScope(Dispatchers.IO).launch {
                    postListAdapter.submitData(it)

                    if(postListAdapter.itemCount == 0) {
                        binding.emptyState.visibility = View.VISIBLE
                    } else {
                        binding.emptyState.visibility = View.GONE
                    }
                }
            }
    }

    private fun initFollowingButton(user: User?) {
        isFollowing = MiniApplication.instance.currentUser?.following?.contains(user?.id) == true

        if (isFollowing) {
            binding.followButton.text = "Bỏ theo dõi"

            binding.followButton.setBackgroundResource(R.drawable.secondary_button)
            binding.followButton.setTextColor(ContextCompat.getColor(this, R.color.charleston90))
        }

        binding.followButton.setOnClickListener {
            if(!isFollowing) {
                binding.followButton.text = "Bỏ theo dõi"
                binding.followButton.setBackgroundResource(R.drawable.secondary_button)
                binding.followButton.setTextColor(ContextCompat.getColor(this, R.color.charleston90))

                MiniApplication.instance.currentUser?.following?.add(user?.id ?: "")

                PersistentSnackbar.show(this@UserProfilePage, "Đã theo dõi ${user?.name}", SnackbarType.success)
            } else {
                binding.followButton.text = "Theo dõi"
                binding.followButton.setBackgroundResource(R.drawable.primary_button)
                binding.followButton.setTextColor(ContextCompat.getColor(this, R.color.white))

                MiniApplication.instance.currentUser?.following?.remove(user?.id ?: "");

                PersistentSnackbar.show(this@UserProfilePage, "Đã huỷ theo dõi ${user?.name}", SnackbarType.success)
            }

            isFollowing = !isFollowing

            if(MiniApplication.instance.currentUser != null)
                changeFollowingButton(MiniApplication.instance.currentUser!!)
        }
    }

    fun changeFollowingButton(user: User) {
        userViewModel.update(user)
    }

}