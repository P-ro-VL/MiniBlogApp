package vn.linhpv.miniblogapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import vn.linhpv.miniblogapp.cache.AppDatabase
import vn.linhpv.miniblogapp.model.User

@HiltAndroidApp
class MiniApplication : Application() {

    companion object {
        lateinit var instance: MiniApplication
    }

    lateinit var database: AppDatabase
    var currentUser: User? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        database = AppDatabase.getDatabase(this)
    }

    fun signOut() {
        currentUser = null
        UserPreferences.clearUser(this)
    }

    fun loadUser(): Boolean {
        currentUser = UserPreferences.loadUser(this)
        return currentUser != null
    }

    fun saveUser() {
        if(currentUser == null) return;
        UserPreferences.saveUser(this, currentUser!!)
    }
}