package vn.linhpv.miniblogapp.view.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
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
import androidx.paging.LoadStates
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.view.UserDetailActivity
import vn.linhpv.miniblogapp.viewmodel.ListUserViewModel


@AndroidEntryPoint
class ListUserFragment : Fragment() {

    lateinit var root: ViewGroup
    lateinit var swipeRefreshLayout: SwipeRefreshLayout

    lateinit var listView: RecyclerView
    lateinit var listViewAdapter: ListUserAdapter

    val viewModel: ListUserViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        this.root = inflater.inflate(R.layout.user_list_layout, null) as ConstraintLayout

        listView = root.findViewById(R.id.userRecyclerView)
        listView.layoutManager = LinearLayoutManager(root.context)
        listViewAdapter = ListUserAdapter(root.context)
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

        return root
    }

    private fun initPullToRefresh() {
        swipeRefreshLayout.setOnRefreshListener {
            listViewAdapter.refresh()
            swipeRefreshLayout.isRefreshing = false
        }
    }

}

class ListUserAdapter(
        private var context: Context,
) : PagingDataAdapter<User, ListUserAdapter.ViewHolder>(UserDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        if(item != null) {
            holder.bind(item)

            holder.itemView.setOnClickListener {
                val i = Intent(
                    context,
                    UserDetailActivity::class.java
                )
                i.putExtra("userId", item.id)
                i.putExtra("userName", item.firstName + " " + item.lastName)
                i.putExtra("email", item.email)
                i.putExtra("avatar", item.avatar)
                context.startActivity(i)
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