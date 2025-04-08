package vn.linhpv.miniblogapp.view

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import dagger.hilt.android.AndroidEntryPoint
import jp.wasabeef.glide.transformations.ColorFilterTransformation
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.cache.post.FavoritePost
import vn.linhpv.miniblogapp.databinding.PostDetailLayoutBinding
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.util.PersistentSnackbar
import vn.linhpv.miniblogapp.util.SnackbarType
import vn.linhpv.miniblogapp.util.format

@AndroidEntryPoint
class PostDetailPage : AppCompatActivity() {

    lateinit var binding: PostDetailLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PostDetailLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val post = intent.extras?.getParcelable<Post>("post")
        val user = intent.extras?.getParcelable<User>("user")

        binding.title.text = post?.title
        binding.content.text = post?.content
        binding.uploadDate.text = post?.timestamp?.format()
        binding.userName.text = user?.name

        Glide.with(applicationContext)
            .load(post?.thumbnail)
            .transform(ColorFilterTransformation(Color.argb(100, 0, 0, 0)))
            .into(binding.thumbnail)

        Glide.with(applicationContext)
            .load(user?.avatar)
            .into(binding.userAvatar)
        binding.authorSection.setOnClickListener {
            var intent = Intent(this, UserProfilePage::class.java)
            if(user?.id == MiniApplication.instance.currentUser?.id) {
                intent = Intent(this, MyProfilePage::class.java)
            }
            intent.putExtra("user", user)
            startActivity(intent)
        }

        binding.backBtn.setOnClickListener {
            finish()
        }

        initFavouriteButton(post)
    }

    fun initFavouriteButton(post: Post?) {
        val dao = MiniApplication.instance.database.favoritePostDao()

        lifecycleScope.launch {
            binding.favouriteBtn.isChecked = dao.isFavorite(post?.id ?: "")
        }

        binding.favouriteBtn.setOnClickListener {
            lifecycleScope.launch {

                val isFavourite = dao.isFavorite(post?.id ?: "")

                if (isFavourite) {
                    dao.removeFavoritePost(post?.id ?: "")

                    PersistentSnackbar.show(
                        this@PostDetailPage,
                        "Xoá bỏ khỏi danh sách yêu thích thành công",
                        SnackbarType.success
                    );
                } else {
                    dao.addFavoritePost(FavoritePost(post?.id ?: ""))

                    PersistentSnackbar.show(
                        this@PostDetailPage,
                        "Thêm vào danh sách yêu thích thành công",
                        SnackbarType.success
                    );
                }
            }
        }
    }

}