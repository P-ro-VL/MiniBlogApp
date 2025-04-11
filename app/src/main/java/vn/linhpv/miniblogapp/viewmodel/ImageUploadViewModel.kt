package vn.linhpv.miniblogapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.linhpv.miniblogapp.repository.ImageRepository
import javax.inject.Inject

@HiltViewModel
class ImageUploadViewModel @Inject constructor (private var imageRepository: ImageRepository) : ViewModel() {

    var imageLiveData: LiveData<String>? = null

    fun uploadImage(context: Context, image: Uri) {
        imageRepository.uploadImage(context, image) {
            imageLiveData = MutableLiveData<String>(it)
        }
    }

}