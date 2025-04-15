package vn.linhpv.miniblogapp.repository

import android.content.Context
import android.net.Uri
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import vn.linhpv.miniblogapp.datasource.RetrofitAPI
import java.io.File
import java.io.FileOutputStream
import java.io.IOException


@Module
@InstallIn(SingletonComponent::class)
class ImageRepository {

    suspend fun uploadImage(context: Context, image: Uri): String {
        val file = getFileFromUri(context, image)
        val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)
        val type = "file".toRequestBody("text/plain".toMediaTypeOrNull())
        val title = "Simple upload".toRequestBody("text/plain".toMediaTypeOrNull())
        val description = "This is a simple image upload in Imgur".toRequestBody("text/plain".toMediaTypeOrNull())

        val result = RetrofitAPI.instance.imageDataSource
                ?.uploadImage(imagePart, type, title, description)?.execute()

        return result?.body()?.data?.link ?: ""
    }

    @Throws(IOException::class)
    private fun getFileFromUri(context: Context, uri: Uri): File {
        val tempFile = File.createTempFile("upload", ".jpg", context.getCacheDir())
        context.getContentResolver().openInputStream(uri).use { inputStream ->
            FileOutputStream(tempFile).use { outputStream ->
                val buffer = ByteArray(4096)
                var bytesRead: Int
                while ((inputStream?.read(buffer).also { bytesRead = it ?: 0 }) != -1) {
                    outputStream.write(buffer, 0, bytesRead)
                }
            }
        }
        return tempFile
    }

    @Provides
    fun provides(): ImageRepository {
        return ImageRepository()
    }
}