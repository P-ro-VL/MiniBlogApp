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
class ListFollowingViewModel @Inject constructor(private var userRepository: UserRepository) : ViewModel() {

    var followingsLiveData = MutableLiveData<List<User>>();

    fun getFollowings(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            followingsLiveData.postValue(userRepository.getFollowings(userId))
        }
    }
}