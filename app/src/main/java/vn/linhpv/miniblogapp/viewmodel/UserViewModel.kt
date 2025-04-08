package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(private var userRepository: UserRepository) : ViewModel() {

    fun getUser(userId: String): LiveData<User> {
        return userRepository.getUser(userId)
    }

    fun update(user: User) {
        userRepository.updateUser(user)
    }
}