package vn.linhpv.miniblogapp.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import dagger.hilt.android.AndroidEntryPoint
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.databinding.UserFollowingItemBinding
import vn.linhpv.miniblogapp.databinding.UserFollowingLayoutBinding
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.view.PostDetailPage
import vn.linhpv.miniblogapp.view.UserProfilePage
import vn.linhpv.miniblogapp.viewmodel.ListFollowingViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class FollowingFragment : Fragment() {

    lateinit var binding: UserFollowingLayoutBinding

    val followingViewModel: ListFollowingViewModel by viewModels()
    var adapter: UserAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = UserFollowingLayoutBinding.inflate(layoutInflater, container, false)

        val recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this.context)
        adapter = UserAdapter(isFollowing = true) {
            var i = Intent(activity, UserProfilePage::class.java)
            i.putExtra("user", it)
            startActivity(i)
        }
        recyclerView.adapter = adapter

        followingViewModel.getFollowings(MiniApplication.instance.currentUser?.id ?: "")
            .observe(viewLifecycleOwner) {
                adapter?.submitList(it)

                if(it.isNotEmpty()) {
                    binding.emptyState.visibility = View.GONE
                } else {
                    binding.emptyState.visibility = View.VISIBLE
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

class UserAdapter(val isFollowing: Boolean, var onClick: (User) -> Unit) : RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userList: List<User> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = UserFollowingItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]

        if(!isFollowing) {
            holder.binding.starIcon.visibility = View.GONE
        }

        Glide.with(holder.binding.root.context)
            .load(user.avatar)
            .into(holder.binding.userAvatar)

        holder.binding.userDisplayName.text = user.name

        holder.itemView.setOnClickListener {
            onClick(user)
        }
    }

    override fun getItemCount(): Int = userList.size

    fun submitList(newList: List<User>) {
        val diffResult = DiffUtil.calculateDiff(UserDiffCallback(userList, newList))
        userList = newList
        diffResult.dispatchUpdatesTo(this)
    }

    class UserViewHolder(val binding: UserFollowingItemBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        private class UserDiffCallback(
            private val oldList: List<User>,
            private val newList: List<User>
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

    private fun formatTimestamp(timestamp: Timestamp?): String {
        if (timestamp == null) return ""
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()) // Corrected format to include year
        return sdf.format(timestamp.toDate())
    }
}
