package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(private var userRepository: UserRepository) : ViewModel() {

    fun signUp(user: User): LiveData<Boolean> {
        return userRepository.createUser(user)
    }

}