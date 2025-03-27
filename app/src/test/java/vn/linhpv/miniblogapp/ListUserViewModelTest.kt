package vn.linhpv.miniblogapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Before
import org.junit.Test
import vn.linhpv.miniblogapp.repository.UserRepository
import vn.linhpv.miniblogapp.viewmodel.ListUserViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.Rule
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
class ListUserViewModelTest {

    private lateinit var viewModel: ListUserViewModel

    lateinit var userRepository: UserRepository

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        Dispatchers.setMain(testDispatcher)
        userRepository = UserRepository()
        viewModel = ListUserViewModel(userRepository)
    }

    @Test
    fun `test query returns expected LiveData`() = runTest {
        val keyword = "Johnson"

        val data = userRepository.getUsers(keyword)

        data.observeForever {
            assert(it != null)
        }
    }
}