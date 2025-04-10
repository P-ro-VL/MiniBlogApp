package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.repository.PostRepository
import javax.inject.Inject

@HiltViewModel
class UploadPostViewModel @Inject constructor(private var repository: PostRepository) : ViewModel() {

    var uploadResultLiveData = MutableLiveData<Boolean>()

    fun uploadPost(post: Post) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = repository.uploadPost(post)
            uploadResultLiveData.postValue(result)
        }
    }

}