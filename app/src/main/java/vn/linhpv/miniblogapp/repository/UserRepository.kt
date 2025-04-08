package vn.linhpv.miniblogapp.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.datasource.UserDataSource
import vn.linhpv.miniblogapp.model.User
import javax.inject.Inject


class UserRepository @Inject constructor(private val dataSource: UserDataSource) {

    fun getUser(id: String): LiveData<User> {
        val liveData = MutableLiveData<User>()
        dataSource.getUser(id) {
            liveData.postValue(it)
        }

        return liveData
    }

    fun createUser(user: User): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        dataSource.createUser(user) {
            liveData.postValue(it)
        }

        return liveData
    }

    fun updateUser(user: User): LiveData<Boolean> {
        val liveData = MutableLiveData<Boolean>()
        dataSource.updateUser(user) {
            liveData.postValue(it)
        }

        return liveData
    }

    fun authenticate(email: String, password: String): LiveData<User?> {
        val liveData = MutableLiveData<User?>()
        dataSource.authenticate(email, password) {
            liveData.postValue(it)
        }
        return liveData
    }

    fun getFollowings(userId: String): MutableLiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()

        dataSource.getUser(userId) { rootUser ->
            val result = mutableListOf<User>()

            Log.d("followers", rootUser?.following.toString())

            for(followingId: String in rootUser?.following ?: emptyList()) {
                dataSource.getUser(followingId) {
                    if(it != null) {
                        result.add(it)
                        liveData.postValue(result)
                    }
                } 
            }

        }
        return liveData
    }

    fun searchUser(keyword: String): MutableLiveData<List<User>> {
        val liveData = MutableLiveData<List<User>>()

        CoroutineScope(Dispatchers.IO).launch {
            val data = dataSource.searchUser(keyword)
            liveData.postValue(data)
        }

        return liveData
    }

}
