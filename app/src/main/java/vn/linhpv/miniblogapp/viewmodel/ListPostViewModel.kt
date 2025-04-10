package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.repository.PostRepository
import vn.linhpv.miniblogapp.repository.QueryPostMode
import javax.inject.Inject

@HiltViewModel
class ListPostViewModel @Inject constructor(private var postRepository: PostRepository) : ViewModel() {

    var postsLiveData = MutableLiveData<PagingData<Post>>()
    var followingPostsLiveData = MutableLiveData<PagingData<Post>>()
    var starredPostLiveData = MutableLiveData<PagingData<Post>>()

    fun getPosts(queryMode: QueryPostMode, pageSize: Int, userId: String = "") {
        CoroutineScope(Dispatchers.IO).launch {
            val result = postRepository.getPosts(queryMode, pageSize, userId).cachedIn(this)
            if(queryMode == QueryPostMode.FOLLOWING)
                followingPostsLiveData.postValue(result.first())
            else
                postsLiveData.postValue(result.first())
        }
    }

    fun getStarredPosts() {
        CoroutineScope(Dispatchers.IO).launch {
            val result = postRepository.getStarredPosts().cachedIn(this)
            starredPostLiveData.postValue(result.first())
        }
    }

}