package vn.linhpv.miniblogapp.datasource

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import vn.linhpv.miniblogapp.model.User

data class UsersResponse(
    val users: List<User>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

interface UserDataSource {
    @GET("/users/search")
    fun searchUser(@Query("q") query: String,
                   @Query("limit") pageSize: Int,
                   @Query("skip") skip: Int): UsersResponse

    @GET("/users/{id}")
    fun getUserDetail(@Path("id") userId: Int) : User
}