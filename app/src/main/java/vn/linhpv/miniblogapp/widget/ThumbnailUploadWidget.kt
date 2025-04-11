package vn.linhpv.miniblogapp.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.databinding.WidgetThumbnailUploadBinding
import vn.linhpv.miniblogapp.viewmodel.ImageUploadViewModel

class ThumbnailUploadWidget : RelativeLayout {
    private var uploadButton: View? = null
    private var uploadedImage: ImageView? = null
    private var progressBar: ProgressBar? = null
    private var context: Context? = null
    private var uploadListener: OnImageUploadListener? = null

    var imageUploadViewModel: ImageUploadViewModel? = null

    fun interface OnImageUploadListener {
        fun onImageUploaded(imageUrl: String?)
    }

    fun setOnImageUploadListener(listener: OnImageUploadListener?) {
        this.uploadListener = listener
    }

    constructor(context: Context) : super(context) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        init(context)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        init(context)
    }

    private fun init(context: Context) {
        this.context = context
        val binding = WidgetThumbnailUploadBinding.inflate(LayoutInflater.from(context), this, true)

        uploadButton = binding.border
        uploadedImage = binding.thumbnail
        progressBar = binding.loadingIndicator

        uploadButton?.setOnClickListener(OnClickListener { v: View? -> openImageChooser() })
    }

    private fun openImageChooser() {
        val intent = Intent()
        intent.setType("image/*")
        intent.setAction(Intent.ACTION_GET_CONTENT)
        (context as AppCompatActivity).startActivityForResult(
            Intent.createChooser(
                intent,
                "Chọn hình ảnh"
            ), PICK_IMAGE_REQUEST
        )
    }

    fun handleActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == AppCompatActivity.RESULT_OK && data != null && data.data != null) {
            val imageUri = data.data
            showLoadingAnimation(imageUri)

            if (imageUri != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = imageUploadViewModel?.uploadImage(
                        context!!,
                        imageUri
                    )
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(context!!.contentResolver, imageUri)
                    post { showUploadedImage(bitmap, result.toString()) }
                }
            }
        }
    }

    private fun showLoadingAnimation(imageUri: Uri?) {
        uploadButton!!.visibility = GONE
        progressBar!!.visibility = VISIBLE
    }

    private fun showUploadedImage(bitmap: Bitmap, imageUrl: String) {
        progressBar!!.visibility = GONE
        uploadedImage!!.visibility = VISIBLE
        uploadedImage!!.setImageBitmap(bitmap)

        if (uploadListener != null) {
            uploadListener!!.onImageUploaded(imageUrl)
        }
    }

    companion object {
        private const val PICK_IMAGE_REQUEST = 1
    }
}