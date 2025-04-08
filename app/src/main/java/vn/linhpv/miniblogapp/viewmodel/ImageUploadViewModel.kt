package vn.linhpv.miniblogapp.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.linhpv.miniblogapp.repository.ImageRepository
import javax.inject.Inject

@HiltViewModel
class ImageUploadViewModel @Inject constructor (private var imageRepository: ImageRepository) : ViewModel() {

    fun uploadImage(context: Context, image: Uri, callback: (String) -> Unit) {
        imageRepository.uploadImage(context, image, callback)
    }

}