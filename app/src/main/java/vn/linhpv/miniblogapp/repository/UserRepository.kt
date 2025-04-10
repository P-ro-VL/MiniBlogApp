package vn.linhpv.miniblogapp.repository

import vn.linhpv.miniblogapp.datasource.UserDataSource
import vn.linhpv.miniblogapp.model.User
import javax.inject.Inject

class UserRepository @Inject constructor(private val dataSource: UserDataSource) {

    suspend fun getUser(id: String): User? {
        return dataSource.getUser(id)
    }

    suspend fun createUser(user: User): Boolean {
        return dataSource.createUser(user)
    }

    suspend fun updateUser(user: User): Boolean {
        return dataSource.updateUser(user)
    }

    suspend fun authenticate(email: String, password: String): User? {
        return dataSource.authenticate(email, password)
    }

    suspend fun getFollowings(userId: String): List<User> {
        val rootUser = dataSource.getUser(userId)
        val followings = mutableListOf<User>()

        rootUser?.following?.forEach { followingId ->
            val user = dataSource.getUser(followingId)
            if (user != null) {
                followings.add(user)
            }
        }

        return followings
    }

    suspend fun searchUser(keyword: String): List<User> {
        return dataSource.searchUser(keyword)
    }
}
