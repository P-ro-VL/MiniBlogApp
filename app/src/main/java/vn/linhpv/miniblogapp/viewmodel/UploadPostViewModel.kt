package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(private var repository: PostRepository) : ViewModel() {

    fun uploadPost(post: Post, callback: (Boolean) -> Unit) {
        repository.uploadPost(post, callback)
    }

}