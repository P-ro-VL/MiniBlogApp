package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.repository.post.PostRepository
import javax.inject.Inject

@HiltViewModel
class ListPostViewModel @Inject constructor(private var postRepository: PostRepository): ViewModel()  {

    private var posts: LiveData<PagingData<Post>>? = null

    fun query(keyword: String, lifecycleOwner: LifecycleOwner): LiveData<PagingData<Post>> {
        posts?.removeObservers(lifecycleOwner)

        posts = postRepository.getPosts(keyword).cachedIn(lifecycleOwner.lifecycle)

        return posts!!
    }

}