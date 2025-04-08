package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.repository.PostRepository
import vn.linhpv.miniblogapp.repository.SearchRepository
import vn.linhpv.miniblogapp.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val userRepository: UserRepository,
    private val postRepository: PostRepository
) : ViewModel() {

    val recentKeywords = searchRepository.getAllKeywords().asLiveData()

    fun saveKeyword(keyword: String) {
        viewModelScope.launch {
            searchRepository.saveKeyword(keyword)
        }
    }

    fun searchUser(keyword: String) : LiveData<List<User>> {
        return userRepository.searchUser(keyword)
    }

    fun searchPost(keyword: String) : LiveData<List<Post>> {
        return postRepository.searchPost(keyword)
    }
}
