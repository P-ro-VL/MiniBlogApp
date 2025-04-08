package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.repository.PostRepository
import vn.linhpv.miniblogapp.repository.QueryPostMode
import javax.inject.Inject

@HiltViewModel
class ListPostViewModel @Inject constructor(private var postRepository: PostRepository) : ViewModel() {

    fun getPosts(queryMode: QueryPostMode, pageSize: Int, userId: String = "") : LiveData<PagingData<Post>> {
        return postRepository.getPosts(queryMode, pageSize, userId)
    }

    fun getStarredPosts(): LiveData<PagingData<Post>> {
        return postRepository.getStarredPosts()
    }

}