package vn.linhpv.miniblogapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import vn.linhpv.miniblogapp.databinding.PostCreateLayoutBinding
import vn.linhpv.miniblogapp.databinding.UserListItemBinding
import vn.linhpv.miniblogapp.databinding.UserListLayoutBinding
import vn.linhpv.miniblogapp.datasource.CreatePostRequest
import vn.linhpv.miniblogapp.datasource.RetrofitAPI
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.util.PersistentSnackbar
import vn.linhpv.miniblogapp.util.SnackbarType
import vn.linhpv.miniblogapp.viewmodel.ListUserViewModel

@AndroidEntryPoint
class CreatePostActivity : AppCompatActivity() {

    var selectedUser: User? = null

    private lateinit var binding: PostCreateLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = PostCreateLayoutBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val backButton = binding.backButton
        backButton.setOnClickListener {
            finish()
        }

        val postUserDropdown = binding.postUserDropdown
        postUserDropdown.setOnClickListener {
            showBottomSheet()
        }

        val submitPostBtn = binding.submitPost
        submitPostBtn.setOnClickListener {
            submitPostBtn.isVisible = false
            binding.loadingIndicator.isVisible = true

            lifecycleScope.launch {
                createPost()
            }
        }

    }

    fun showBottomSheet() {
        val bottomSheetFragment = BottomSheetFragment() {
            binding.postUserDropdown.text = it.firstName + " " + it.lastName
            selectedUser = it
        }

        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    suspend fun createPost() {
        val activity = this

        val title = binding.postNameInput.text
        val content = binding.postContentInput.text

        val body = CreatePostRequest(
            title?.toString() ?: "--",
            content?.toString() ?: "--",
            selectedUser?.id ?: 0
        )

        withContext(Dispatchers.IO) {
            val response = RetrofitAPI.instance.postDataSource?.createPost(body)

            runOnUiThread {
                if(response != null) {

                    PersistentSnackbar.show(activity, "Tạo bài viết thành công", SnackbarType.success)
                    finish()
                } else {
                    PersistentSnackbar.show(activity, "Có lỗi xảy ra, vui lòng thử lại sau", SnackbarType.error)
                }
            }
        }

    }

}

@AndroidEntryPoint
class BottomSheetFragment(private val onItemClick: (User) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ListUserBottomsheetAdapter

    val viewModel: ListUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = UserListLayoutBinding.inflate(inflater, container, false)

        recyclerView = binding.userRecyclerView

        adapter = ListUserBottomsheetAdapter(binding.root.context) {
            onItemClick(it)
            dismiss()
        }
        adapter.addLoadStateListener {
            binding.loadingIndicator.isVisible = it.refresh is LoadState.Loading
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.query("", this).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        return binding.root
    }
}

class ListUserBottomsheetAdapter(
    private var context: Context,
    private val onItemClick: (User) -> Unit
) : PagingDataAdapter<User, ListUserBottomsheetAdapter.ViewHolder>(UserDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item)

            holder.itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val binding = UserListItemBinding.inflate(LayoutInflater.from(context), parent, false)

        return ViewHolder(binding)
    }

    class ViewHolder(binding: UserListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        var userAvatar: ShapeableImageView = binding.userAvatar
        var userDisplayName: TextView = binding.userDisplayName

        fun bind(user: User) {
            val displayName = "${user.firstName} ${user.lastName}"
            userDisplayName.text = displayName
            Glide.with(itemView.context)
                .load(user.avatar)
                .circleCrop()
                .into(userAvatar)
        }
    }
}

class UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem.id == newItem.id
    }

    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        return oldItem == newItem
    }
}

