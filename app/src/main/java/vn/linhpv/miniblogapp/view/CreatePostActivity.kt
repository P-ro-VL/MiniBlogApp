package vn.linhpv.miniblogapp.view

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.coroutineScope
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.datasource.CreatePostRequest
import vn.linhpv.miniblogapp.datasource.RetrofitAPI
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.util.PersistentSnackbar
import vn.linhpv.miniblogapp.util.SnackbarType
import vn.linhpv.miniblogapp.viewmodel.ListUserViewModel
import kotlin.coroutines.coroutineContext

@AndroidEntryPoint
class CreatePostActivity : AppCompatActivity() {

    var selectedUser: User? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.post_create_layout)

        val backButton = findViewById<ImageView>(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        val postUserDropdown = findViewById<TextView>(R.id.postUserDropdown)
        postUserDropdown.setOnClickListener {
            showBottomSheet()
        }

        val submitPostBtn = findViewById<TextView>(R.id.submitPost)
        submitPostBtn.setOnClickListener {
            submitPostBtn.isVisible = false
            findViewById<ProgressBar>(R.id.loadingIndicator).isVisible = true

            lifecycleScope.launch {
                createPost()
            }
        }

    }

    fun showBottomSheet() {
        val bottomSheetFragment = BottomSheetFragment() {
            findViewById<TextView>(R.id.postUserDropdown).text = it.firstName + " " + it.lastName
            selectedUser = it
        }

        bottomSheetFragment.show(supportFragmentManager, bottomSheetFragment.tag)
    }

    suspend fun createPost() {
        val activity = this

        val title = findViewById<TextView>(R.id.postNameInput).text
        val content = findViewById<TextView>(R.id.postContentInput).text

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
        val root = inflater.inflate(R.layout.user_list_layout, null)

        recyclerView = root.findViewById(R.id.userRecyclerView)

        adapter = ListUserBottomsheetAdapter(root.context) {
            onItemClick(it)
            dismiss()
        }
        adapter.addLoadStateListener {
            root.findViewById<ProgressBar>(R.id.loadingIndicator).isVisible = it.refresh is LoadState.Loading
        }
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter

        viewModel.query("", this).observe(this) {
            adapter.submitData(lifecycle, it)
        }

        return root
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
        val view = LayoutInflater.from(context).inflate(R.layout.user_list_item, parent, false)

        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userAvatar: ShapeableImageView = itemView.findViewById(R.id.userAvatar);
        var userDisplayName: TextView = itemView.findViewById(R.id.userDisplayName);

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

