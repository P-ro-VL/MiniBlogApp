package vn.linhpv.miniblogapp.cache.post

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_posts")
data class FavoritePost(
    @PrimaryKey val postId: String
)
