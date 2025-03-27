package vn.linhpv.miniblogapp.viewmodel

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import vn.linhpv.miniblogapp.model.User
import vn.linhpv.miniblogapp.repository.UserRepository
import javax.inject.Inject

@HiltViewModel
class ListUserViewModel @Inject constructor(private var userRepository: UserRepository): ViewModel()  {

    private var users: LiveData<PagingData<User>>? = null

    fun query(keyword: String, lifecycleOwner: LifecycleOwner): LiveData<PagingData<User>> {
        users?.removeObservers(lifecycleOwner)

        users = userRepository.getUsers(keyword).cachedIn(lifecycleOwner.lifecycle)

        return users!!
    }

}