package vn.linhpv.miniblogapp.repository.post

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import vn.linhpv.miniblogapp.datasource.RetrofitAPI
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.repository.UserPagingSource
import kotlin.math.ceil

class UserPostPagingSource(var userId: Int) : PagingSource<Int, Post>() {

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
                RetrofitAPI.instance.postDataSource?.getPostsByUserId(
                    userId
                )
            }

            if (response != null) {
                for (post: Post in response.posts) {
                    post.user = UserPagingSource.userCache[userId ?: 0]
                }

                val maxPage = ceil(response.total / 5.toDouble()).toInt() - 1
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