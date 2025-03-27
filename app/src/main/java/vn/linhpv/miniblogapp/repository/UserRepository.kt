package vn.linhpv.miniblogapp.repository

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.liveData
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vn.linhpv.miniblogapp.datasource.RetrofitAPI
import vn.linhpv.miniblogapp.model.User
import kotlin.math.ceil


@Module
@InstallIn(SingletonComponent::class)
class UserRepository {

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }

    fun getUsers(keyword: String): LiveData<PagingData<User>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE, maxSize = 200),
        pagingSourceFactory = { UserPagingSource(keyword) }
    ).liveData

    @Provides
    fun provides(): UserRepository {
        return UserRepository()
    }

}

class UserPagingSource(val keyword: String) : PagingSource<Int, User>() {

    companion object {
        val userCache = HashMap<Int, User>()
    }

    override fun getRefreshKey(state: PagingState<Int, User>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, User> {
        return try {
            val position = params.key ?: 0

            val response = withContext(Dispatchers.IO) {
                RetrofitAPI.instance.userDataSource?.searchUser(
                    keyword,
                    UserRepository.DEFAULT_PAGE_SIZE,
                    position * UserRepository.DEFAULT_PAGE_SIZE
                )
            }

            if (response != null) {
                val maxPage = ceil(response.total / UserRepository.DEFAULT_PAGE_SIZE.toDouble()).toInt()

                for(user: User in response.users) {
                    userCache[user.id ?: 0] = user
                }

                LoadResult.Page(
                    data = response.users,
                    prevKey = if (position == 0) null else position - 1,
                    nextKey = if (position >= maxPage) null else position + 1
                )
            } else {
                LoadResult.Error(Exception("No Response"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            LoadResult.Error(e)
        }
    }

}