package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.cache.search.SearchKeyword
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

    var searchUserLiveData = MutableLiveData<List<User>>()
    var searchPostLiveData = MutableLiveData<List<Post>>()
    val recentKeywordLiveData = MutableLiveData<List<SearchKeyword>>()

    fun saveKeyword(keyword: String) {
        viewModelScope.launch {
            searchRepository.saveKeyword(keyword)
        }
    }

    fun getRecentKeywords() {
        viewModelScope.launch {
            val keywords = searchRepository.getAllKeywords()
            recentKeywordLiveData.postValue(keywords.first())
        }
    }

    fun searchUser(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = userRepository.searchUser(keyword)
            searchUserLiveData.postValue(result)
        }
    }

    fun searchPost(keyword: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = postRepository.searchPost(keyword)
            searchPostLiveData.postValue(result)
        }
    }
}
