package vn.linhpv.miniblogapp.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.databinding.ExploreLayoutBinding
import vn.linhpv.miniblogapp.databinding.PostItemBinding
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.repository.QueryPostMode
import vn.linhpv.miniblogapp.view.PostDetailPage
import vn.linhpv.miniblogapp.viewmodel.ListPostViewModel
import vn.linhpv.miniblogapp.viewmodel.UserViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ExploreFragment : Fragment() {

    lateinit var binding: ExploreLayoutBinding

    val listPostViewModel: ListPostViewModel by viewModels();
    val userViewModel: UserViewModel by viewModels();
    
    var allTabAdapter: PostAdapter? = null
    var followingTabAdapter: PostAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ExploreLayoutBinding.inflate(layoutInflater, container, false)

        allTabAdapter = PostAdapter(viewLifecycleOwner, userViewModel) {
            initAdapter(it)
        }
        followingTabAdapter = PostAdapter(viewLifecycleOwner, userViewModel) {
            initAdapter(it)
        }

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        recyclerView.adapter = allTabAdapter

        listPostViewModel.getPosts(queryMode = QueryPostMode.ALL, pageSize = 10)
            .observe(viewLifecycleOwner) { pagingData ->
                lifecycleScope.launch {
                    allTabAdapter?.submitData(pagingData)
                }
            }
        listPostViewModel.getPosts(queryMode = QueryPostMode.FOLLOWING, pageSize = 10, userId = MiniApplication.instance.currentUser?.id ?: "")
            .observe(viewLifecycleOwner) { pagingData ->
                lifecycleScope.launch {
                    followingTabAdapter?.submitData(pagingData)
                }
            }

        binding.tabsGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.allButton -> recyclerView.adapter = allTabAdapter
                R.id.followingButton -> recyclerView.adapter = followingTabAdapter
            }
        }

        return binding.root
    }

    fun initAdapter(data: Pair<User, Post>) {
        val i = Intent(activity, PostDetailPage::class.java)
        i.putExtra("post", data.second)
        i.putExtra("user", data.first)
        startActivity(i)
    }

}

class PostAdapter(private val lifecycleOwner: LifecycleOwner, private val userViewModel: UserViewModel, var onClick: (Pair<User, Post>) -> Unit) : PagingDataAdapter<Post, PostAdapter.PostViewHolder>(POST_COMPARATOR) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = getItem(position) ?: return

        userViewModel.getUser(post.userId ?: "")
            .observe(holder.binding.lifecycleOwner ?: lifecycleOwner) {
                val user = it

                Glide.with(holder.binding.root.context)
                    .load(it.avatar)
                    .into(holder.binding.userAvatar)

                holder.binding.userName.text = it.name

                holder.binding.title.text = post.title

                Glide.with(holder.binding.root.context)
                    .load(post.thumbnail)
                    .into(holder.binding.thumbnail)

                holder.binding.uploadDate.text = formatTimestamp(post.timestamp)

                holder.itemView.setOnClickListener {
                    onClick(Pair(user, post))
                }
        }
    }

    class PostViewHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        val POST_COMPARATOR = object : DiffUtil.ItemCallback<Post>() {
            override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem.id == newItem.id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
                return oldItem == newItem
            }
        }
    }

    private fun formatTimestamp(timestamp: Timestamp?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        return sdf.format(timestamp.toDate())
    }

}
