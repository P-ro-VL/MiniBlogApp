package vn.linhpv.miniblogapp.cache.post

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FavoritePostDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addFavoritePost(favoritePost: FavoritePost)

    @Query("DELETE FROM favorite_posts WHERE postId = :postId")
    suspend fun removeFavoritePost(postId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_posts WHERE postId = :postId)")
    suspend fun isFavorite(postId: String): Boolean

    @Query("SELECT * FROM favorite_posts")
    suspend fun getAllFavoritePosts(): List<FavoritePost>
}
