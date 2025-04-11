package vn.linhpv.miniblogapp.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.view.isGone
import androidx.core.view.updateMargins
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.databinding.PostItemBinding
import vn.linhpv.miniblogapp.databinding.SearchLayoutBinding
import vn.linhpv.miniblogapp.databinding.SearchRecentItemBinding
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.util.format
import vn.linhpv.miniblogapp.view.MyProfilePage
import vn.linhpv.miniblogapp.view.PostDetailPage
import vn.linhpv.miniblogapp.view.UserProfilePage
import vn.linhpv.miniblogapp.viewmodel.SearchViewModel
import vn.linhpv.miniblogapp.viewmodel.UserViewModel

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private lateinit var binding: SearchLayoutBinding

    private val viewModel: SearchViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()

    lateinit var userSearchAdapter: UserAdapter
    lateinit var postSearchAdapter: PostListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SearchLayoutBinding.inflate(inflater, container, false)

        initAdapters()
        initRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchBar.doOnTextChanged { text, start, before, count ->
            checkDefaultState(text?.toString())
        }
        binding.searchBar.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)) {
                handleSearch()
                true
            } else {
                false
            }
        }

        initKeywordsObserver()
    }

    private fun initAdapters() {
        userSearchAdapter = UserAdapter(isFollowing = false) {
            navigateToDetailUser(it)
        }

        postSearchAdapter = PostListAdapter(
            lifecycleOwner = viewLifecycleOwner,
            userViewModel = userViewModel
        ) {
            navigateToDetailPost(it.first, it.second)
        }
    }

    private fun initRecyclerView() {
        binding.userItems.layoutManager = LinearLayoutManager(context)
        binding.userItems.adapter = userSearchAdapter

        binding.postItems.layoutManager = LinearLayoutManager(context)
        binding.postItems.adapter = postSearchAdapter
    }

    private fun initKeywordsObserver() {
        viewModel.getRecentKeywords()
        viewModel.recentKeywordLiveData.observe(viewLifecycleOwner) { keywords ->
            if (keywords.isNotEmpty()) {
                binding.recentSection.visibility = View.VISIBLE
                binding.recentItems.removeAllViews()

                for (keyword in keywords) {
                    val chip = createChip(keyword.keyword)
                    binding.recentItems.addView(chip)
                    (chip?.layoutParams as? ViewGroup.MarginLayoutParams)?.updateMargins(
                        bottom = 8,
                        right = 8
                    )
                }
            }
        }
    }

    private fun navigateToDetailUser(user: User?) {
        var i = Intent(activity, UserProfilePage::class.java)
        if(user?.id == MiniApplication.instance.currentUser?.id) {
            i = Intent(activity, MyProfilePage::class.java)
        }
        i.putExtra("user", user)
        startActivity(i)
    }

    private fun navigateToDetailPost(user: User, post: Post) {
        val i = Intent(activity, PostDetailPage::class.java)
        i.putExtra("post", post)
        i.putExtra("user", user)
        startActivity(i)
    }

    private fun checkDefaultState(text: String?) {
        if (text.isNullOrEmpty()) {
            binding.recentSection.visibility = View.VISIBLE
            binding.userSection.visibility = View.GONE
            binding.postSection.visibility = View.GONE
            binding.notFound.visibility = View.GONE
        }
    }

    private fun checkEmptyState() {
        if(binding.userSection.isGone && binding.postSection.isGone) {
            binding.notFound.visibility = View.VISIBLE
        } else {
            binding.notFound.visibility = View.GONE
        }
    }

    private fun handleSearch() {
        val keyword = binding.searchBar.text.toString().trim()
        if (keyword.isNotEmpty()) {
            viewModel.saveKeyword(keyword)

            viewModel.searchUser(keyword)
            viewModel.searchUserLiveData.observe(viewLifecycleOwner) {
                userSearchAdapter.submitList(it)

                binding.recentSection.visibility = View.GONE
                binding.userSection.visibility = if(it.isNotEmpty()) View.VISIBLE else View.GONE

                checkEmptyState()
            }

            viewModel.searchPost(keyword)
            viewModel.searchPostLiveData.observe(viewLifecycleOwner) {
                postSearchAdapter.submitList(it)

                binding.recentSection.visibility = View.GONE
                binding.postSection.visibility = if(it.isNotEmpty()) View.VISIBLE else View.GONE

                checkEmptyState()
            }
        }
    }

    private fun createChip(text: String): View {
        val chipBinding = SearchRecentItemBinding.inflate(layoutInflater)
        chipBinding.recentText.text = text
        chipBinding.root.setOnClickListener {
            binding.searchBar.setText(text)
            viewModel.searchUser(text)
            viewModel.searchPost(text)
        }
        return chipBinding.root
    }
}

class PostListAdapter(val lifecycleOwner: LifecycleOwner, val userViewModel: UserViewModel, var onClick: (Pair<User, Post>) -> Unit) : RecyclerView.Adapter<PostListAdapter.PostViewHolder>() {

    private var postList: List<Post> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = PostItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        val user = post.author ?: return
        updateUI(holder, user, post)
    }

    fun updateUI(holder: PostViewHolder, user: User, post: Post) {
        Glide.with(holder.binding.root.context)
            .load(user.avatar)
            .into(holder.binding.userAvatar)

        holder.binding.userName.text = user.name

        holder.binding.title.text = post.title

        Glide.with(holder.binding.root.context)
            .load(post.thumbnail)
            .into(holder.binding.thumbnail)

        holder.binding.uploadDate.text = post.timestamp?.format()

        holder.itemView.setOnClickListener {
            onClick(Pair(user, post))
        }
    }

    override fun getItemCount(): Int = postList.size

    fun submitList(newList: List<Post>) {
        val diffResult = DiffUtil.calculateDiff(PostDiffCallback(postList, newList))
        postList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class PostViewHolder(val binding: PostItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private class PostDiffCallback(
            private val oldList: List<Post>,
            private val newList: List<Post>
        ) : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldList.size

            override fun getNewListSize(): Int = newList.size

            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition].id == newList[newItemPosition].id
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return oldList[oldItemPosition] == newList[newItemPosition]
            }
        }
    }

}

