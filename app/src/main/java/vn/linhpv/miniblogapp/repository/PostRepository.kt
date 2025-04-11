package vn.linhpv.miniblogapp.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.datasource.PostDataSource
import vn.linhpv.miniblogapp.datasource.UserDataSource
import vn.linhpv.miniblogapp.model.Post
import javax.inject.Inject

enum class QueryPostMode {
    ALL,
    FOLLOWING,
    MY_POST,
}

class PostRepository @Inject constructor(private val dataSource: PostDataSource, private val userDataSource: UserDataSource) {

    fun getPosts(queryMode: QueryPostMode, pageSize: Int, userId: String = ""): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = pageSize,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                when (queryMode) {
                    QueryPostMode.FOLLOWING -> FollowingPostPagingSource(userDataSource, userId)
                    QueryPostMode.MY_POST -> UserPostPagingSource(userDataSource, dataSource, userId)
                    else -> AllPostsPagingSource(userDataSource, pageSize)
                }
            }
        ).flow
    }

    suspend fun uploadPost(post: Post): Boolean {
        return dataSource.createPost(post)
    }

    suspend fun searchPost(keyword: String): List<Post> {
        val result = mutableListOf<Post>()

        for(post in dataSource.searchPost(keyword)) {
            val user = userDataSource.getUser(post.author?.id ?: "")
            post.author = user

            result.add(post)
        }

        return result
    }

    fun getStarredPosts(): Flow<PagingData<Post>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                StarredPostPagingSource(dataSource)
            }
        ).flow
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
    private val userDataSource: UserDataSource,
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

            var posts = snapshot.documents.mapNotNull {
                val doc = it.toObject(Post::class.java)

                val user = userDataSource.getUser(doc?.id ?: "")
                doc?.author = user

                return@mapNotNull doc
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
    private val userDataSource: UserDataSource,
    private val dataSource: PostDataSource,
    private val userId: String
) : PagingSource<QueryDocumentSnapshot, Post>() {

    override suspend fun load(params: LoadParams<QueryDocumentSnapshot>): LoadResult<QueryDocumentSnapshot, Post> {
        return try {
            val user = userDataSource.getUser(userId)

            var query = Firebase.firestore.collection(PostDataSource.COLLECTION_NAME)
                .whereEqualTo("userId", userId)

            val key = params.key
            if (key != null) {
                query = query.startAfter(key)
            }

            val snapshot = query.get().await()
            val posts = snapshot.toObjects(Post::class.java)
            for(post in posts) {
                post.author = user
            }
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


class AllPostsPagingSource(private val userDataSource: UserDataSource, private val pageSize: Int, var userId: String = "") : PagingSource<QueryDocumentSnapshot, Post>() {

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
            val posts = mutableListOf<Post>()
            for(doc in snapshot.documents) {
                val post = doc.toObject(Post::class.java)
                val user = userDataSource.getUser((doc.data?.get("userId") ?: "").toString())
                post?.author = user
                if(post != null) posts.add(post)
            }
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


