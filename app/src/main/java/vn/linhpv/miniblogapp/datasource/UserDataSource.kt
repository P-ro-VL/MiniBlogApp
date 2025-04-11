package vn.linhpv.miniblogapp.datasource

import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.tasks.await
import vn.linhpv.miniblogapp.model.User
import java.util.UUID

@Module
@InstallIn(SingletonComponent::class)
class UserDataSource {
    companion object {
        const val COLLECTION_NAME = "users"
    }

    @Provides
    fun provides(): UserDataSource {
        return UserDataSource()
    }

    suspend fun createUser(user: User): Boolean {
        return try {
            val firestore = Firebase.firestore
            firestore.collection(COLLECTION_NAME)
                .document(user.id ?: UUID.randomUUID().toString())
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            val firestore = Firebase.firestore
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("id", userId)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(user: User): Boolean {
        return try {
            val firestore = Firebase.firestore
            firestore.collection(COLLECTION_NAME)
                .document(user.id ?: UUID.randomUUID().toString())
                .set(user)
                .await()
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun authenticate(email: String, password: String): User? {
        return try {
            val firestore = Firebase.firestore
            val snapshot = firestore.collection(COLLECTION_NAME)
                .whereEqualTo("email", email)
                .whereEqualTo("password", password)
                .get()
                .await()
            snapshot.documents.firstOrNull()?.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun searchUser(keyword: String): List<User> {
        return try {
            val db = Firebase.firestore
            val querySnapshot = db.collection(COLLECTION_NAME)
                .get().await()

            querySnapshot.documents.mapNotNull { doc ->
                doc.toObject(User::class.java)
            }.filter { user ->
                user.name?.contains(keyword, ignoreCase = true) == true
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
