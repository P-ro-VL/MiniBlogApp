package vn.linhpv.miniblogapp.view.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.view.CreatePostActivity
import vn.linhpv.miniblogapp.view.PostDetailActivity
import vn.linhpv.miniblogapp.viewmodel.ListPostViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class ListPostFragment : Fragment() {

    lateinit var root: ViewGroup
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var listView: RecyclerView
    lateinit var listViewAdapter: ListPostAdapter

    val viewModel: ListPostViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.root = inflater.inflate(R.layout.post_list_layout, null) as ConstraintLayout

        listView = root.findViewById(R.id.userRecyclerView)
        listView.layoutManager = LinearLayoutManager(root.context)
        listViewAdapter = ListPostAdapter(root.context, false)
        listViewAdapter.addLoadStateListener {
            root.findViewById<ProgressBar>(R.id.loadingIndicator).isVisible = it.refresh is LoadState.Loading
        }
        listView.adapter = listViewAdapter

        viewModel.query("", viewLifecycleOwner).observe(viewLifecycleOwner) {
            listViewAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }

        swipeRefreshLayout = root.findViewById(R.id.swipeRefreshLayout)
        initPullToRefresh()

        val searchBar = root.findViewById<EditText>(R.id.searchBar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable) {
                viewModel.query(s.toString(), viewLifecycleOwner).observe(viewLifecycleOwner) {
                    listViewAdapter.submitData(viewLifecycleOwner.lifecycle, it)
                }
            }
        })

        val fab = root.findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val i = Intent(
                context,
                CreatePostActivity::class.java
            )

            startActivity(i)
        }

        return root
    }

    private fun initPullToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            listViewAdapter.refresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }

}

class ListPostAdapter(
    private var context: Context,
    var userMode: Boolean
) : PagingDataAdapter<Post, ListPostAdapter.ViewHolder>(PostDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item, userMode)

            holder.itemView.setOnClickListener {
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

                val i = Intent(
                    context,
                    PostDetailActivity::class.java
                )

                i.putExtra("title", item.title)
                i.putExtra("author", item.user?.firstName + " " + item.user?.lastName)
                i.putExtra("uploadDate", dateFormat.format(Date()))
                i.putExtra("content", item.body)

                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(i)
            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_list_item, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postTitle: TextView = itemView.findViewById(R.id.postTitle);
        var authorName: TextView = itemView.findViewById(R.id.authorName);
        var uploadDate: TextView = itemView.findViewById(R.id.uploadDate)

        fun bind(post: Post, userMode: Boolean) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.US)

            val userName = post.user?.firstName + " " + post.user?.lastName

            postTitle.text = post.title
            authorName.text = userName
            uploadDate.text = dateFormat.format(Date())

            authorName.isVisible = !userMode
        }
    }
}

class PostDiffCallback : DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
        return oldItem.id == newItem.id
    }
}