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
class UserDataSource() {
    companion object {
        const val COLLECTION_NAME = "users"
    }

    @Provides
    fun provides(): UserDataSource {
        return UserDataSource()
    }

    fun createUser(user: User, callback: (Boolean) -> Unit) {
        val firestore = Firebase.firestore

        firestore.collection(COLLECTION_NAME)
            .document(user.id ?: UUID.randomUUID().toString())
            .set(user)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        val firestore = Firebase.firestore

        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("id", userId)
            .get()
            .addOnSuccessListener {
                val user = it.documents.firstOrNull()?.toObject(User::class.java)
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }

    fun updateUser(user: User, callback: (Boolean) -> Unit) {
        val firestore = Firebase.firestore

        firestore.collection(COLLECTION_NAME)
            .document(user.id ?: UUID.randomUUID().toString())
            .set(user)
            .addOnSuccessListener {
                callback(true)
            }
            .addOnFailureListener {
                callback(false)
            }
    }

    fun authenticate(email: String, password: String, function: (User?) -> Unit) {
        val firestore = Firebase.firestore

        firestore.collection(COLLECTION_NAME)
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener {
                val user = it.documents.firstOrNull()?.toObject(User::class.java)
                function(user)
            }
            .addOnFailureListener {
                function(null)
            }
    }

    suspend fun searchUser(keyword: String): List<User> {
        val result = mutableListOf<User>()

        val db = Firebase.firestore

        val query = db.collection("users")
            .get().await()
        val users = query.documents.mapNotNull { doc ->
            doc.toObject(User::class.java)
        }

        for (user in users) {
            val cond = user.name.contains(keyword, ignoreCase = true) ?: false
            if(cond) {
                result.add(user)
            }
        }

        return result
    }

}