package vn.linhpv.miniblogapp.datasource

import com.google.firebase.Firebase
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import vn.linhpv.miniblogapp.MiniApplication
import vn.linhpv.miniblogapp.model.Post
import vn.linhpv.miniblogapp.model.User
import java.util.UUID

@Module
@InstallIn(SingletonComponent::class)
class PostDataSource() {
    companion object {
        const val COLLECTION_NAME = "posts"
        var LAST_PAGINATION_DOCUMENT: QueryDocumentSnapshot? = null
    }

    @Provides
    fun provides(): PostDataSource {
        return PostDataSource()
    }

    suspend fun createPost(post: Post): Boolean {
        val firestore = Firebase.firestore

        return try {
            firestore.collection(COLLECTION_NAME)
                .document(post.id ?: UUID.randomUUID().toString())
                .set(post).await()
            true
        } catch (_: Exception) {
            false
        }
    }

    suspend fun getPostWithPagination(
        pageSize: Int,
        pageNumber: Int,
    ): List<Post> {
        val db = Firebase.firestore
        var query: Query = db.collection(COLLECTION_NAME)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .limit(pageSize.toLong())

        if (LAST_PAGINATION_DOCUMENT != null) {
            query = query.startAfter(LAST_PAGINATION_DOCUMENT)
        }

        val documents = query.get().await()
        val posts = documents.map { it.toObject(Post::class.java) }

        val lastVisible = documents.documents.lastOrNull() as? QueryDocumentSnapshot
        LAST_PAGINATION_DOCUMENT = lastVisible

        return posts
    }

    suspend fun getPostsFromFollowing(userId: String): List<Post> {
        val db = Firebase.firestore
        val userRef = db.collection("users").document(userId)

        return try {
            val userSnapshot = userRef.get().await()
            val followingList = userSnapshot.get("following") as? List<String> ?: emptyList()

            if (followingList.isEmpty()) {
                return emptyList()
            }

            val postsSnapshot = db.collection("posts")
                .whereIn("userId", followingList)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            postsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun getUserPosts(userId: String): List<Post> {
        val db = Firebase.firestore

        return try {
            val postsSnapshot = db.collection("posts")
                .whereEqualTo("userId", userId)
                .get()
                .await()

            postsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Post::class.java)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun searchPost(keyword: String): List<Post> {
        val result = mutableListOf<Post>()
        val db = Firebase.firestore

        val query = db.collection("posts")
            .get().await()
        val documents = query.documents
        val posts = mutableListOf<Post>()

        for(doc in documents) {
            val post = doc.toObject(Post::class.java)
            val user = User(id = (doc.data?.get("userId") ?: "").toString())
            post?.author = user
            if(post != null) posts.add(post)
        }

        for (post in posts) {
            val titleCond = post.title?.contains(keyword, ignoreCase = true) ?: false
            if(titleCond) {
                result.add(post)
            }
        }

        return result
    }

    suspend fun getStarredPosts(): List<Post> {
        val data = MiniApplication.instance.database.favoritePostDao().getAllFavoritePosts()
        val result = mutableListOf<Post>()

        for(post in data) {
            val postRef = Firebase.firestore.collection("posts").document(post.postId)
            val postSnapshot = postRef.get().await()

            if (postSnapshot.exists()) {
                val postObj = postSnapshot.toObject(Post::class.java)
                if (postObj != null) {
                    result.add(postObj)
                }
            }
        }

        return result;
    }

}