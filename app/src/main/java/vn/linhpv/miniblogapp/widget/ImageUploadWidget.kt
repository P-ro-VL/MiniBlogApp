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
import com.bumptech.glide.Glide
import vn.linhpv.miniblogapp.R
import vn.linhpv.miniblogapp.viewmodel.ImageUploadViewModel

class ImageUploadWidget : RelativeLayout {
    private var uploadButton: ImageView? = null
    private var uploadedImage: ImageView? = null
    private var uploadPlaceholder: ImageView? = null
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
        LayoutInflater.from(context).inflate(R.layout.widget_image_upload, this, true)

        uploadButton = findViewById(R.id.uploadButton)
        uploadedImage = findViewById(R.id.uploadedImage)
        uploadPlaceholder = findViewById(R.id.uploadPlaceholder)
        progressBar = findViewById(R.id.progressBar)

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
                imageUploadViewModel?.uploadImage(
                    context!!,
                    imageUri
                ) {
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(context!!.contentResolver, imageUri)
                    post { showUploadedImage(bitmap, it.toString()) }
                }
            }
        }
    }

    private fun showLoadingAnimation(imageUri: Uri?) {
        uploadButton!!.visibility = GONE
        uploadPlaceholder!!.visibility = VISIBLE
        Glide.with(context!!)
            .load(imageUri)
            .centerCrop()
            .into(uploadPlaceholder!!)
        progressBar!!.visibility = VISIBLE
    }

    private fun showUploadedImage(bitmap: Bitmap, imageUrl: String) {
        progressBar!!.visibility = GONE
        uploadPlaceholder!!.visibility = GONE
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