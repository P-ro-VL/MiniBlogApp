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
class SignUpViewModel @Inject constructor(private var userRepository: UserRepository) : ViewModel() {

    var signUpLiveData = MutableLiveData<Boolean>()

    fun signUp(user: User) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = userRepository.createUser(user)
            signUpLiveData.postValue(result)
        }
    }

}