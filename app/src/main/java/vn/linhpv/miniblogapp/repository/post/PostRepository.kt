package vn.linhpv.miniblogapp.repository.post

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
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.repository.UserPagingSource
import kotlin.math.ceil

@Module
@InstallIn(SingletonComponent::class)
class PostRepository {

    companion object {
        const val DEFAULT_PAGE_SIZE = 10
    }

    fun getPosts(keyword: String): LiveData<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = DEFAULT_PAGE_SIZE, maxSize = 200),
        pagingSourceFactory = { PostPagingSource(keyword) }
    ).liveData

    fun getPostsByUserId(userId: Int): LiveData<PagingData<Post>> = Pager(
        config = PagingConfig(pageSize = 5, maxSize = 200),
        pagingSourceFactory = { UserPostPagingSource(userId) }
    ).liveData

    @Provides
    fun provides(): PostRepository {
        return PostRepository()
    }


}

class PostPagingSource(var keyword: String) : PagingSource<Int, Post>() {

    override fun getRefreshKey(state: PagingState<Int, Post>): Int? {
        return state.anchorPosition?.let {
            state.closestPageToPosition(it)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(it)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Post> {
        return try {
            val position = params.key ?: 0

            val response = withContext(Dispatchers.IO) {
                RetrofitAPI.instance.postDataSource?.searchPost(
                    keyword,
                    PostRepository.DEFAULT_PAGE_SIZE,
                    position * PostRepository.DEFAULT_PAGE_SIZE
                )
            }

            if (response != null) {
                for (post: Post in response.posts) {
                    if(UserPagingSource.userCache.containsKey(post.userId ?: 0)) {
                        post.user = UserPagingSource.userCache[post.userId ?: 0]
                    } else {
                        val userInfo = withContext(Dispatchers.IO) {
                            RetrofitAPI.instance.userDataSource?.getUserDetail(
                                post.userId ?: 0,
                            )
                        }
                        if(userInfo == null) continue
                        post.user = userInfo
                        UserPagingSource.userCache[post.userId ?: 0] = userInfo
                    }
                }

                val maxPage = ceil(response.total / PostRepository.DEFAULT_PAGE_SIZE.toDouble()).toInt()

                LoadResult.Page(
                    data = response.posts,
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