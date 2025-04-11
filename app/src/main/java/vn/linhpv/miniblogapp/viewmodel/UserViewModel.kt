package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private var userRepository: UserRepository) : ViewModel() {

    var userLiveData = MutableLiveData<User?>()
    var userUpdateLiveData = MutableLiveData<Boolean>()

    fun getUser(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = userRepository.getUser(userId)
            userLiveData.postValue(result)
        }
    }

    fun update(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = userRepository.updateUser(user)
            userUpdateLiveData.postValue(result)
        }
    }
}