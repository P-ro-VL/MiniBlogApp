package vn.linhpv.miniblogapp.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.liveData
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.datasource.PostDataSource
import vn.linhpv.miniblogapp.model.Post
import javax.inject.Inject

enum class QueryPostMode {
    ALL,
    FOLLOWING,
    MY_POST,
}

class PostRepository @Inject constructor(private val dataSource: PostDataSource) {

    fun getPosts(queryMode: QueryPostMode, pageSize: Int, userId: String = ""): LiveData<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                when (queryMode) {
                    QueryPostMode.FOLLOWING -> FollowingPostPagingSource(dataSource, userId)
                    QueryPostMode.MY_POST -> UserPostPagingSource(dataSource, userId)
                    else -> AllPostsPagingSource(dataSource, pageSize)
                }
            }
        ).liveData
    }

    fun uploadPost(post: Post, callback: (Boolean) -> Unit) {
        dataSource.createPost(post, callback)
    }

    fun searchPost(keyword: String): LiveData<List<Post>> {
        val liveData = MutableLiveData<List<Post>>()

        CoroutineScope(Dispatchers.IO).launch {
            val data = dataSource.searchPost(keyword)
            liveData.postValue(data)
        }

        return liveData
    }

    fun getStarredPosts(): LiveData<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StarredPostPagingSource(dataSource)
            }
        ).liveData
    }
}

class StarredPostPagingSource(
    private val dataSource: PostDataSource,
) : PagingSource<QueryDocumentSnapshot, Post>() {

    override suspend fun load(params: LoadParams<QueryDocumentSnapshot>): LoadResult<QueryDocumentSnapshot, Post> {
        return try {
            val data = MiniApplication.instance.database.favoritePostDao().getAllFavoritePosts()
            val result = mutableListOf<Post>()

            for(post in data) {
                val postRef = com.google.firebase.Firebase.firestore.collection("posts").document(post.postId)
                val postSnapshot = postRef.get().await()

                if (postSnapshot.exists()) {
                    val postObj = postSnapshot.toObject(Post::class.java)
                    if (postObj != null) {
                        result.add(postObj)
                    }
                }
            }

            LoadResult.Page(
                data = result,
                prevKey = null,
                nextKey = null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QueryDocumentSnapshot, Post>): QueryDocumentSnapshot? {
        return null
    }
}

class FollowingPostPagingSource(
    private val dataSource: PostDataSource,
    private val userId: String
) : PagingSource<QueryDocumentSnapshot, Post>() {

    override suspend fun load(params: LoadParams<QueryDocumentSnapshot>): LoadResult<QueryDocumentSnapshot, Post> {
        try {
            val userRef = Firebase.firestore.collection("users").document(userId)

            val userSnapshot = userRef.get().await()
            val followingList = userSnapshot.get("following") as? List<String> ?: emptyList()

            if(followingList.isEmpty())
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = null,
                    nextKey = null
                )

            var query = Firebase.firestore.collection("posts")
                .whereIn("userId", followingList)

            val key = params.key
            if (key != null) {
                query = query.startAfter(key)
            }
            val snapshot = query.get().await()

            var posts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)
                }
            val lastVisible = snapshot.documents.lastOrNull() as? QueryDocumentSnapshot

            return LoadResult.Page(
                data = posts,
                prevKey = null,
                nextKey = if (posts.isEmpty()) null else lastVisible
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QueryDocumentSnapshot, Post>): QueryDocumentSnapshot? {
        return null
    }
}


class UserPostPagingSource(
    private val dataSource: PostDataSource,
    private val userId: String
) : PagingSource<QueryDocumentSnapshot, Post>() {

    override suspend fun load(params: LoadParams<QueryDocumentSnapshot>): LoadResult<QueryDocumentSnapshot, Post> {
        return try {
            var query = Firebase.firestore.collection(PostDataSource.COLLECTION_NAME)
                .whereEqualTo("userId", userId)

            val key = params.key
            if (key != null) {
                query = query.startAfter(key)
            }

            val snapshot = query.get().await()
            val posts = snapshot.toObjects(Post::class.java)
            val lastVisible = snapshot.documents.lastOrNull() as? QueryDocumentSnapshot

            LoadResult.Page(
                data = posts,
                prevKey = null,
                nextKey = if (posts.isEmpty()) null else lastVisible
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QueryDocumentSnapshot, Post>): QueryDocumentSnapshot? {
        return null
    }
}


class AllPostsPagingSource(private val dataSource: PostDataSource, private val pageSize: Int, var userId: String = "") : PagingSource<QueryDocumentSnapshot, Post>() {

    override suspend fun load(params: LoadParams<QueryDocumentSnapshot>): LoadResult<QueryDocumentSnapshot, Post> {
        return try {
            var query = Firebase.firestore.collection(PostDataSource.COLLECTION_NAME)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(pageSize.toLong())

            val key = params.key
            if (key != null) {
                query = query.startAfter(key)
            }

            val snapshot = query.get().await()
            val posts = snapshot.toObjects(Post::class.java)
            val lastVisible = snapshot.documents.lastOrNull() as? QueryDocumentSnapshot

            LoadResult.Page(
                data = posts,
                prevKey = null, // No backward pagination
                nextKey = if (posts.isEmpty()) null else lastVisible // Stop if empty
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<QueryDocumentSnapshot, Post>): QueryDocumentSnapshot? {
        return null
    }
}


