package vn.linhpv.miniblogapp.datasource

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import vn.linhpv.miniblogapp.model.Post

data class PostsResponse (
    val posts: List<Post>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class CreatePostRequest (
    var title: String,
    var body: String,
    var userId: Int,
)

interface PostDataSource {
    @GET("/posts/search")
    fun searchPost(@Query("q") query: String,
                   @Query("limit") pageSize: Int,
                   @Query("skip") skip: Int): PostsResponse


    @GET("/posts/user/{id}")
    fun getPostsByUserId(@Path("id") userId: Int) : PostsResponse

    @POST("/posts/add")
    fun createPost(@Body body: CreatePostRequest) : Post
}