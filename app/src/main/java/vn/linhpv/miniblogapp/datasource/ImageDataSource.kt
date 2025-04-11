package vn.linhpv.miniblogapp.datasource

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

data class ImgurResponse(
    val status: Int,
    val success: Boolean,
    val data: ImgurData
)

data class ImgurData(
    val link: String
)

interface ImageDataSource {

    @Multipart
    @POST("/3/image")
    @Headers("Authorization: Client-ID 546c25a59c58ad7")
    fun uploadImage(
        @Part image: MultipartBody.Part,
        @Part("type") type: RequestBody,
        @Part("title") title: RequestBody,
        @Part("description") description: RequestBody
    ): Call<ImgurResponse>

}