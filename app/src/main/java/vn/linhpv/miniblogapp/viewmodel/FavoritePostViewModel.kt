package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.cache.post.FavoritePost
import vn.linhpv.miniblogapp.cache.post.FavoritePostDao

class FavoritePostViewModel(private val dao: FavoritePostDao) : ViewModel() {

    fun addFavorite(postId: String) {
        viewModelScope.launch {
            dao.addFavoritePost(FavoritePost(postId))
        }
    }

    fun removeFavorite(postId: String) {
        viewModelScope.launch {
            dao.removeFavoritePost(postId)
        }
    }

    suspend fun isFavorite(postId: String): Boolean {
        return dao.isFavorite(postId)
    }
}
